package io.choerodon.file.app.service.impl;

import java.util.Collections;
import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.file.api.dto.FileDTO;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.FileService;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.service.factory.StoreFactory;
import org.hzero.file.domain.service.factory.StoreService;
import org.hzero.file.infra.util.CodeUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.file.service.AbstractFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.file.app.service.FileC7nService;
import io.choerodon.file.infra.constant.DevOpsConstants;
import io.choerodon.file.infra.exception.DevopsCiInvalidException;
import io.choerodon.file.infra.feign.DevOpsClient;
import io.choerodon.file.infra.utils.ImageUtils;

/**
 * @author scp
 * @date 2020/5/14
 * @description
 */
@Service
public class FileC7nServiceImpl implements FileC7nService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileC7nServiceImpl.class);

    @Autowired
    private FileService fileService;
    @Autowired
    private DevOpsClient devOpsClient;
    @Autowired
    private StoreFactory storeFactory;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private CapacityUsedService capacityUsedService;

    @Override
    public String cutImage(Long tenantId, String bucketName, MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        try {
            file = ImageUtils.cutImage(file, rotate, axisX, axisY, width, height);
            return fileService.uploadMultipart(tenantId, bucketName, null, null, file.getOriginalFilename(), 0, null, file);
        } catch (Exception e) {
            throw new CommonException("error.cut.and.upload.image", e);
        }
    }

    @Override
    public String uploadDevOpsArtifactFile(Long tenantId, String token, String commit, Long ciPipelineId, Long ciJobId, String artifactName, MultipartFile multipartFile) {
        // 为了抛出非2xx的状态码让客户端感知到
        try {
            return doUploadDevOpsArtifactFile(tenantId, token, commit, ciPipelineId, ciJobId, artifactName, multipartFile);
        } catch (DevopsCiInvalidException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DevopsCiInvalidException(ex.getMessage(), ex);
        }
    }

    public String doUploadDevOpsArtifactFile(Long tenantId, String token, String commit, Long ciPipelineId, Long ciJobId, String artifactName, MultipartFile multipartFile) {
        LOGGER.info("UploadDevOpsArtifactFile: the tenant id {}, token {}, commit {}, ciPipelineId {}, ciJobId: {}, artifactName: {}, size: {}", tenantId, token, commit, ciPipelineId, ciJobId, artifactName, multipartFile.getSize());
        String fileName = String.format(DevOpsConstants.CI_JOB_ARTIFACT_NAME_TEMPLATE, ciPipelineId, artifactName);
        // 先去devops-service校验参数
        ResponseEntity<Boolean> checkResult = devOpsClient.checkJobArtifactInfo(token, commit, ciPipelineId, ciJobId, artifactName, multipartFile.getSize());
        if (checkResult.getStatusCode() != HttpStatus.OK || !Boolean.TRUE.equals(checkResult.getBody())) {
            throw new DevopsCiInvalidException("artifact.information.invalid");
        }

        // 查询是否已经有这个文件
        // 这个查询从数据库来说是不保证唯一性的, 但是从这个逻辑这里保证了唯一性
        // 除非有另一处上传文件的地方使用了相同的bucket和命令规则, 否则, 不会出现误删除文件的情况
        List<File> files = fileRepository.selectByCondition(Condition.builder(File.class).where(
                Sqls.custom()
                        .andEqualTo(File.FIELD_FILE_NAME, fileName)
                        .andEqualTo(File.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(File.FIELD_BUCKET_NAME, DevOpsConstants.DEV_OPS_CI_ARTIFACT_FILE_BUCKET)
        ).build());

        if (!CollectionUtils.isEmpty(files)) {
            if (files.size() > 1) {
                LOGGER.warn("Unexpected size of the files with name {} in doUploadDevOpsArtifactFile. The size is {}", fileName, files.size());
            }
            for (File dbRecord : files) {
                // 有的话先删除了, 保证唯一性
                if (dbRecord != null && dbRecord.getFileKey() != null) {
                    fileService.deleteFileByKey(tenantId, dbRecord.getFileKey());
                }
            }
        }

        // 上传文件
        String fileUrl = fileService.uploadMultipart(tenantId, DevOpsConstants.DEV_OPS_CI_ARTIFACT_FILE_BUCKET, null, null, fileName, BaseConstants.Flag.NO, null, multipartFile);

        // 将信息给devops-service保存
        ResponseEntity<Void> responseEntity;
        try {
            responseEntity = devOpsClient.saveJobArtifactInfo(token, commit, ciPipelineId, ciJobId, artifactName, fileUrl);
        } catch (Exception e) {
            fileService.deleteByUrls(tenantId, DevOpsConstants.DEV_OPS_CI_ARTIFACT_FILE_BUCKET, null, Collections.singletonList(fileUrl));
            throw new DevopsCiInvalidException("error.save.job.artifact.information", artifactName, e);
        }

        // 判断响应
        if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK) {
            fileService.deleteByUrls(tenantId, DevOpsConstants.DEV_OPS_CI_ARTIFACT_FILE_BUCKET, null, Collections.singletonList(fileUrl));
            throw new DevopsCiInvalidException("error.save.job.artifact.information", artifactName);
        }
        LOGGER.info("Finished UploadDevOpsArtifactFile: the tenant id {}, token {}, commit {}, ciPipelineId {}, ciJobId: {}, artifactName: {}, size: {}", tenantId, token, commit, ciPipelineId, ciJobId, artifactName, multipartFile.getSize());
        return fileUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUrls(Long organizationId, String bucketName, List<String> urls) {
        if (ObjectUtils.isEmpty(urls)) {
            return;
        }
        String attachmentUuid = "$";
        List<String> decodeUrls = CodeUtils.decode(urls);
        StoreService storeService = storeFactory.build(organizationId, null);
        Assert.notNull(storeService, "hfle.error.file_store_config");
        List<FileDTO> dbFileRecords =
                fileRepository.selectFileByUrls(organizationId, bucketName, decodeUrls, attachmentUuid);
        AbstractFileService abstractFileService = storeService.getAbstractFileService();
        //先删文件
        decodeUrls.forEach(url -> abstractFileService.deleteFile(bucketName, url, null));
        //数据库有数据，删数据库
        if (!dbFileRecords.isEmpty()) {
            fileRepository.deleteFileByUrls(organizationId, bucketName, attachmentUuid, decodeUrls);
            dbFileRecords.forEach(r -> capacityUsedService.refreshCache(organizationId, -r.getFileSize()));
        }
    }
}

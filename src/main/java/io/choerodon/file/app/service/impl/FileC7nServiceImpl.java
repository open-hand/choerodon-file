package io.choerodon.file.app.service.impl;

import java.util.Collections;

import org.hzero.boot.file.FileClient;
import org.hzero.boot.file.dto.FileSimpleDTO;
import org.hzero.file.app.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    @Autowired
    private FileService fileService;
    @Autowired
    private FileClient fileClient;
    @Autowired
    private DevOpsClient devOpsClient;

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
        String fileName = String.format(DevOpsConstants.CI_JOB_ARTIFACT_NAME_TEMPLATE, ciPipelineId, artifactName);
        // 先去devops-service校验参数
        ResponseEntity<Boolean> checkResult = devOpsClient.checkJobArtifactInfo(token, commit, ciPipelineId, ciJobId, artifactName, multipartFile.getSize());
        if (checkResult.getStatusCode() != HttpStatus.OK || !Boolean.TRUE.equals(checkResult.getBody())) {
            throw new DevopsCiInvalidException("artifact.information.invalid");
        }

        // 查询是否已经有这个文件
        FileSimpleDTO fileSimpleDTO = fileClient.getSignedUrl(tenantId, fileName);
        if (fileSimpleDTO != null && fileSimpleDTO.getFileTokenUrl() != null) {
            // 有的话先删除了
            fileClient.deleteFileByUrl(tenantId, DevOpsConstants.DEV_OPS_CI_ARTIFACT_FILE_BUCKET, Collections.singletonList(fileSimpleDTO.getFileTokenUrl()));
        }

        // 上传文件
        String fileUrl = fileClient.uploadFile(tenantId, DevOpsConstants.DEV_OPS_CI_ARTIFACT_FILE_BUCKET, null, fileName, multipartFile);

        // 将信息给devops-service保存
        ResponseEntity responseEntity;
        try {
            responseEntity = devOpsClient.saveJobArtifactInfo(token, commit, ciPipelineId, ciJobId, artifactName, fileUrl);
        } catch (Exception e) {
            fileClient.deleteFileByUrl(tenantId, DevOpsConstants.DEV_OPS_CI_ARTIFACT_FILE_BUCKET, Collections.singletonList(fileUrl));
            throw new DevopsCiInvalidException("error.save.artifact.information", e);
        }

        // 判断响应
        if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK) {
            fileClient.deleteFileByUrl(tenantId, DevOpsConstants.DEV_OPS_CI_ARTIFACT_FILE_BUCKET, Collections.singletonList(fileUrl));
            throw new DevopsCiInvalidException("error.save.artifact.information");
        }
        return fileUrl;
    }
}

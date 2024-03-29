package io.choerodon.file.app.service.impl;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.hzero.file.api.dto.FileDTO;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.FileService;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.StorageConfig;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.service.factory.StoreFactory;
import org.hzero.file.domain.service.factory.StoreService;
import org.hzero.file.infra.mapper.FileMapper;
import org.hzero.file.infra.util.CodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.file.app.service.FileC7nService;
import io.choerodon.file.infra.mapper.C7nFileMapper;
import io.choerodon.file.infra.utils.ImageUtils;

/**
 * @author scp
 * @date 2020/5/14
 * @description
 */
@Service
public class FileC7nServiceImpl implements FileC7nService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileC7nServiceImpl.class);

    private static final String URL_FORMAT = "%s/choerodon/v1/%s/download/%s";
    @Value("${hzero.file.gateway-path}")
    private String FILE_GATEWAY_URL;
    @Autowired
    private FileService fileService;
    @Autowired
    private StoreFactory storeFactory;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private C7nFileMapper c7nFileMapper;
    @Autowired
    private CapacityUsedService capacityUsedService;
    @Autowired
    @Qualifier("restTemplateForIp")
    private RestTemplate restTemplate;

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
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUrls(Long organizationId, String bucketName, List<String> urls) {
        if (ObjectUtils.isEmpty(urls)) {
            return;
        }
        String attachmentUuid = "$";
        List<String> decodeUrls = CodeUtils.decode(urls);
        StoreService storeService = storeFactory.build(organizationId, null);
        Assert.notNull(storeService, "hfle.error.file_store_config");
        List<File> dbFileRecords =
                c7nFileMapper.selectFileByUrls(organizationId, bucketName, decodeUrls, attachmentUuid);
        if (!dbFileRecords.isEmpty()) {
            dbFileRecords.forEach(r -> {
                if (r.getFileId() == null) {
                    throw new CommonException("error.get.file.id");
                }
                fileService.deleteFileByKey(r.getFileId(), r.getFileKey());
            });
        }
    }

    @Override
    public String uploadMultipart(Long tenantId, String bucketName, String attachmentUuid, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        String minioUrl = fileService.uploadMultipart(tenantId, bucketName, null, directory, fileName, docType, storageCode, multipartFile);
        File queryDTO = new File();
        queryDTO.setTenantId(tenantId).setBucketName(bucketName).setFileName(fileName).setFileUrl(minioUrl);
        File file = fileMapper.selectOne(queryDTO);
        return String.format(URL_FORMAT, FILE_GATEWAY_URL, tenantId, file.getFileId());
    }

    @Override
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, Long tenantId, Long fileId) {
        File file = fileMapper.selectByPrimaryKey(fileId);
        fileService.downloadFile(request, response, tenantId, file.getBucketName(), file.getStorageCode(), file.getFileUrl());
    }

    @Override
    public List<File> listFileByIds(List<Long> fileIds) {
        return fileRepository.selectByIds(Joiner.on(",").join(fileIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long tenantId, Long fileId) {
        if (ObjectUtils.isEmpty(fileId)) {
            return;
        }
        StoreService storeService = storeFactory.build(tenantId, null);
        Assert.notNull(storeService, "hfle.error.file_store_config");
        File file = fileRepository.selectByPrimaryKey(fileId);
        storeService.deleteFileByKey(file.getFileId(), file.getBucketName(), file.getFileKey(), file.getFileUrl(), file.getFileSize(), tenantId);
    }

    @Override
    public StorageConfig queryDefaultConfig() {
        return c7nFileMapper.queryDefaultConfig();
    }

    @Override
    public File queryFileWithUrl(Long organizationId, String bucketName, String fileUrl) {
        return c7nFileMapper.queryFileWithUrl(organizationId, bucketName, fileUrl);
    }

    @Override
    public List<File> queryFileDTOByIds(Long organizationId, List<String> fileKeys) {
        if (CollectionUtils.isEmpty(fileKeys)) {
            return Collections.EMPTY_LIST;
        }
        return c7nFileMapper.queryFileByKeys(organizationId, fileKeys);
    }
}

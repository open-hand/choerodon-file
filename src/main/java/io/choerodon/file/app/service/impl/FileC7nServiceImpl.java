package io.choerodon.file.app.service.impl;

import java.util.List;

import org.hzero.file.api.dto.FileDTO;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.FileService;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.service.factory.StoreFactory;
import org.hzero.file.domain.service.factory.StoreService;
import org.hzero.file.infra.util.CodeUtils;
import org.hzero.starter.file.service.AbstractFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.file.app.service.FileC7nService;
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

package io.choerodon.file.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.ValidUtils;
import org.hzero.file.app.service.UploadConfigService;
import org.hzero.file.app.service.impl.FileServiceImpl;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.service.FileOperationLogService;
import org.hzero.file.domain.service.factory.StoreFactory;
import org.hzero.file.domain.service.factory.StoreService;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.util.ContentTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import javax.validation.Validator;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件服务实现类
 * c7n 覆盖方法 添加文件分片上传校验
 * {@link FileHzeroServiceImpl#uploadFragmentFile(Long, String, String, String, String, String, Long)}
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/11 17:33
 */

public class FileHzeroServiceImpl extends FileServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(FileHzeroServiceImpl.class);

    @Autowired
    private StoreFactory factory;
    @Autowired
    private Validator validator;
    @Autowired
    private UploadConfigHzeroServiceImpl uploadConfigHzeroService;

    public FileHzeroServiceImpl(StoreFactory factory,
                                FileRepository fileRepository,
                                UploadConfigService uploadConfigService,
                                Validator validator,
                                FileOperationLogService operationLogService) {
        super(factory, fileRepository, uploadConfigService, validator, operationLogService);
    }


    @Override
    public String uploadFragmentFile(Long tenantId, String bucketName, String directory, String fileName, String storageCode, String filePath, Long fileSize) {
        Path path = Paths.get(filePath);
        try (InputStream is = new FileInputStream(filePath)) {
            String contentType = Files.probeContentType(path);
            Files.size(path);
            // 组合文件对象
            File file = new File()
                    .setMd5(DigestUtils.md5DigestAsHex(is))
                    .setBucketName(bucketName)
                    .setDirectory(directory)
                    .setFileName(fileName)
                    .setFileType(StringUtils.isBlank(contentType) ? ContentTypeUtils.getContentType(fileName) : contentType)
                    .setFileSize(fileSize)
                    .setTenantId(tenantId)
                    .setStorageCode(storageCode)
                    .setAttachmentUuid(HfleConstant.DEFAULT_ATTACHMENT_UUID);
            uploadConfigHzeroService.validateFileSize(file, StringUtils.isBlank(contentType) ? ContentTypeUtils.getContentType(fileName) : contentType, false);

            // 验证数据
            ValidUtils.valid(validator, file);
            StoreService storeService = factory.build(tenantId, storageCode);
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            return storeService.uploadFile(file, filePath).getFileUrl();
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }
}

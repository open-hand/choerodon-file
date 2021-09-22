package io.choerodon.file.app.task;

import org.hzero.file.domain.entity.StorageConfig;
import org.hzero.file.domain.repository.StorageConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.choerodon.file.infra.config.OssProperties;
import io.choerodon.file.infra.enums.OssTypeEnum;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  16:44 2019/3/11
 * Description:
 */
@Component
public class FileCommandRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCommandRunner.class);
    @Autowired
    private StorageConfigRepository storageConfigRepository;
    @Autowired
    private OssProperties ossProperties;
    private static final String STORAGE_CODE_FORMAT = "CHOERODON-%s";

    @Override
    public void run(String... strings) {
        if (ossProperties.getUpdate()) {
            String storageCode = String.format(STORAGE_CODE_FORMAT, ossProperties.getType());
            StorageConfig queryDTO = new StorageConfig();
            queryDTO.setStorageCode(storageCode);
            queryDTO.setTenantId(0L);
            StorageConfig storageConfig = storageConfigRepository.selectOne(queryDTO);
            if (ObjectUtils.isEmpty(storageConfig)) {
                storageConfig = new StorageConfig();
            }

            OssTypeEnum typeEnum = OssTypeEnum.valueOf(ossProperties.getType());
            storageConfig.setTenantId(0L);
            storageConfig.setAccessControl(typeEnum.getAccessControl());
            storageConfig.setAccessKeyId(ossProperties.getAccessKey());
            storageConfig.setAccessKeySecret(ossProperties.getSecretKey());
            storageConfig.setEndPoint(ossProperties.getEndpoint());
            storageConfig.setStorageCode(storageCode);
            storageConfig.setBucketPrefix(ossProperties.getPrefix());
            storageConfig.setDefaultFlag(1);
            storageConfig.setStorageType(typeEnum.getTypeNum());
            storageConfig.setCreateBucketFlag(1);
            storageConfig.setRegion(ossProperties.getRegion());
            storageConfigRepository.insertOrUpdateStorageConfig(storageConfig);
        }
    }
}

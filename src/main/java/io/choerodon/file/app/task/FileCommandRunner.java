package io.choerodon.file.app.task;

import org.hzero.file.domain.entity.StorageConfig;
import org.hzero.file.domain.repository.StorageConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.access_control:read-write}")
    private String accessControl;

    @Value("${minio.storage_code:MINIO}")
    private String storageCode;

    @Value("${minio.update:true}")
    private Boolean minioUpdate;

    @Override
    public void run(String... strings) {
        if (minioUpdate) {
            StorageConfig queryDTO = new StorageConfig();
            queryDTO.setStorageCode(storageCode);
            queryDTO.setTenantId(0L);
            StorageConfig storageConfig = storageConfigRepository.selectOne(queryDTO);
            if (ObjectUtils.isEmpty(storageConfig)) {
                storageConfig = new StorageConfig();
            }
            storageConfig.setTenantId(0L);
            storageConfig.setAccessControl(accessControl);
            storageConfig.setAccessKeyId(accessKey);
            storageConfig.setAccessKeySecret(secretKey);
            storageConfig.setEndPoint(endpoint);
            storageConfig.setStorageCode(storageCode);
            storageConfig.setBucketPrefix("");
            storageConfig.setDefaultFlag(1);
            storageConfig.setStorageType(3);
            storageConfig.setCreateBucketFlag(1);
            storageConfigRepository.insertOrUpdateStorageConfig(storageConfig);
        }
    }
}

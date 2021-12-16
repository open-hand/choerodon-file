package io.choerodon.file.infra.config;

import io.choerodon.file.app.service.impl.FileHzeroServiceImpl;
import io.choerodon.file.app.service.impl.UploadConfigHzeroServiceImpl;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.app.service.CapacityConfigService;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.FileService;
import org.hzero.file.app.service.UploadConfigService;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.repository.UploadConfigRepository;
import org.hzero.file.domain.service.FileOperationLogService;
import org.hzero.file.domain.service.factory.StoreFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.validation.Validator;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/4/23
 * @Modified By:
 */
@Configuration
public class FileC7nConfig {

    @Bean
    public UploadConfigService uploadConfigService(UploadConfigRepository uploadConfigRepository,
                                                   CapacityConfigService capacityConfigService,
                                                   CapacityUsedService capacityUsedService,
                                                   RedisHelper redisHelper) {
        return new UploadConfigHzeroServiceImpl(uploadConfigRepository, capacityConfigService, capacityUsedService, redisHelper);
    }


    @Bean
    @DependsOn("uploadConfigService")
    public FileService fileService(StoreFactory factory,
                                   FileRepository fileRepository,
                                   UploadConfigService uploadConfigService,
                                   Validator validator,
                                   FileOperationLogService operationLogService) {
        return new FileHzeroServiceImpl(factory, fileRepository, uploadConfigService, validator, operationLogService);
    }

}

package org.hzero.file.app.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.app.service.CapacityConfigService;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.UploadConfigService;
import org.hzero.file.domain.entity.CapacityConfig;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.UploadConfig;
import org.hzero.file.domain.repository.UploadConfigRepository;
import org.hzero.file.domain.vo.UploadConfigVO;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.util.FileHeaderUtils;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 文件上传配置默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/20 11:25
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class UploadConfigServiceImpl extends BaseAppService implements UploadConfigService {
    private static final Logger logger = LoggerFactory.getLogger(UploadConfigServiceImpl.class);

    @Value("${choerodon.file.system.file-type}")
    private String systemFileType;

    private final UploadConfigRepository uploadConfigRepository;
    private final CapacityConfigService capacityConfigService;
    private final CapacityUsedService capacityUsedService;
    private final RedisHelper redisHelper;

    @Autowired
    public UploadConfigServiceImpl(UploadConfigRepository uploadConfigRepository,
                                   CapacityConfigService capacityConfigService,
                                   CapacityUsedService capacityUsedService,
                                   RedisHelper redisHelper) {
        this.uploadConfigRepository = uploadConfigRepository;
        this.capacityConfigService = capacityConfigService;
        this.capacityUsedService = capacityUsedService;
        this.redisHelper = redisHelper;
    }

    @Override
    public UploadConfig detailConfig(String bucketName, Long tenantId, String directory) {
        if (directory == null) {
            directory = StringUtils.EMPTY;
        }
        UploadConfig uploadConfig = uploadConfigRepository.selectOne(
                new UploadConfig().setTenantId(tenantId).setBucketName(bucketName).setDirectory(directory));
        // 若未获取租户配置，查询平台配置
        if (uploadConfig == null) {
            uploadConfig = uploadConfigRepository.selectOne(new UploadConfig()
                    .setTenantId(BaseConstants.DEFAULT_TENANT_ID).setBucketName(bucketName).setDirectory(directory));
        }
        return uploadConfig;
    }

    @ProcessLovValue
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UploadConfig createUploadConfig(Long tenantId, UploadConfig uploadConfig) {
        if (tenantId != null) {
            uploadConfig.setTenantId(tenantId);
        }
        if (uploadConfig.getDirectory() == null) {
            uploadConfig.setDirectory(StringUtils.EMPTY);
        }
        uploadConfig.setDirectory(uploadConfig.getDirectory());
        validObject(uploadConfig);
        uploadConfig.validateRepeat(uploadConfigRepository);
        uploadConfig.validateSize(capacityConfigService);
        uploadConfigRepository.insertSelective(uploadConfig);
        // 添加缓存
        UploadConfigVO uploadConfigVO = new UploadConfigVO();
        BeanUtils.copyProperties(uploadConfig, uploadConfigVO);
        UploadConfig.refreshCache(redisHelper, uploadConfig.getTenantId(), uploadConfig.getBucketName(), uploadConfig.getDirectory(), uploadConfigVO);
        return uploadConfig;
    }

    @ProcessLovValue
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UploadConfig updateUploadConfig(Long tenantId, UploadConfig uploadConfig) {
        if (tenantId != null) {
            uploadConfig.setTenantId(tenantId);
        }
        if (uploadConfig.getDirectory() == null) {
            uploadConfig.setDirectory(StringUtils.EMPTY);
        }
        uploadConfig.setDirectory(uploadConfig.getDirectory());
        SecurityTokenHelper.validToken(uploadConfig);
        validObject(uploadConfig);
        uploadConfig.validateSize(capacityConfigService);
        uploadConfigRepository.updateOptional(uploadConfig, UploadConfig.FIELD_CONTENT_TYPE,
                UploadConfig.FIELD_STORAGE_UNIT, UploadConfig.FIELD_STORAGE_SIZE,
                UploadConfig.FIELD_FILE_FORMAT, UploadConfig.MULTIPLE_FILE_FLAG);
        // 更新缓存
        UploadConfigVO uploadConfigVO = new UploadConfigVO();
        BeanUtils.copyProperties(uploadConfig, uploadConfigVO);
        UploadConfig.refreshCache(redisHelper, uploadConfig.getTenantId(), uploadConfig.getBucketName(), uploadConfig.getDirectory(), uploadConfigVO);
        return uploadConfig;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUploadConfig(UploadConfig uploadConfig) {
        SecurityTokenHelper.validToken(uploadConfig);
        Long uploadConfigId = uploadConfig.getUploadConfigId();
        uploadConfig = uploadConfigRepository.selectByPrimaryKey(uploadConfigId);
        uploadConfigRepository.deleteByPrimaryKey(uploadConfigId);
        // 清除缓存
        UploadConfig.clearRedisCache(redisHelper, uploadConfig.getTenantId(), uploadConfig.getBucketName(), uploadConfig.getDirectory());
    }

    @Override
    public void validateFileSize(File file, String fileCode) {
        logger.info("======test logger555555555!!!!==========={}",file.getTenantId());

        // 检查租户剩余容量
        checkResidualCapacity(file.getTenantId());

        Long tenantId = file.getTenantId();
        String bucketName = file.getBucketName();
        String directory = file.getDirectory();
        // 获取租户配置
        UploadConfig uploadConfig = getConfig(tenantId, bucketName, directory);
        if (uploadConfig == null) {
            // 获取租户桶配置
            uploadConfig = getConfig(tenantId, bucketName, null);
        }
        if (!BaseConstants.DEFAULT_TENANT_ID.equals(tenantId) && uploadConfig == null) {
            // 获取平台级配置
            uploadConfig = getConfig(BaseConstants.DEFAULT_TENANT_ID, bucketName, directory);
        }
        if (!BaseConstants.DEFAULT_TENANT_ID.equals(tenantId) && uploadConfig == null) {
            // 获取平台级桶配置
            uploadConfig = getConfig(BaseConstants.DEFAULT_TENANT_ID, bucketName, null);
        }
        if (uploadConfig == null) {
            return;
        }
        logger.info("======test logger566666666!!!!==========={}",file.getTenantId());
        Long fileSize = file.getFileSize();
        String[] str = file.getFileName().split("\\.");
        Assert.isTrue(str.length > 1, HfleMessageConstant.ERROR_LOAD_FILE_TYPE);
        // 文件后缀名
        String suffix = str[str.length - BaseConstants.Digital.ONE].toLowerCase();
        // 检查文件头与文件类型是否匹配
        FileHeaderUtils.checkFileType(fileCode, suffix);
        // 校验配置
        if (StringUtils.isBlank(uploadConfig.getFileFormat())) {
            // 文件格式为空
            Long size = uploadConfig.getStorageSize();
            String unit = uploadConfig.getStorageUnit();
            // 判断文件大小
            this.checkFileSize(unit, fileSize, size);
        } else {
            // todo 覆盖hzero该类唯一更改部分
            logger.info("======test logger3333333333333!!!!===========");
            if (StringUtils.isNotEmpty(systemFileType)) {
                uploadConfig.setFileFormat(uploadConfig.getFileFormat() + BaseConstants.Symbol.COMMA + systemFileType);
            }
            logger.info("======test logger44444444!!!!==========={}",uploadConfig.getFileFormat());
            // 指定了文件格式
            List<String> fileFormat = Arrays.asList(uploadConfig.getFileFormat().split(BaseConstants.Symbol.COMMA));
            if (!fileFormat.contains(suffix)) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE);
            }
            // 定义了该后缀
            Long size = uploadConfig.getStorageSize();
            String unit = uploadConfig.getStorageUnit();
            // 判断并抛出
            this.checkFileSize(unit, fileSize, size);
        }
    }

    /**
     * 获取文件上传配置
     *
     * @param tenantId   租户Id
     * @param bucketName 桶
     * @param directory  目录
     * @return 文件上传配置
     */
    private UploadConfig getConfig(Long tenantId, String bucketName, String directory) {
        UploadConfig uploadConfig = UploadConfig.getCache(redisHelper, tenantId, bucketName, directory);
        // todo 是bug hzero处理以后删除
        if (uploadConfig == null) {
            UploadConfig queryDTO=new UploadConfig();
            queryDTO.setTenantId(tenantId);
            if (StringUtils.isNotEmpty(directory)) {
                queryDTO.setDirectory(directory);
            }
            queryDTO.setBucketName(bucketName);
            uploadConfig = uploadConfigRepository.selectOne(queryDTO);
            // 写入缓存
            if (uploadConfig != null) {
                UploadConfigVO configVO = new UploadConfigVO();
                BeanUtils.copyProperties(uploadConfig, configVO);
                UploadConfig.refreshCache(redisHelper, tenantId, bucketName, directory, configVO);
            }
        }
        return uploadConfig;
    }

    /**
     * @param unit     文件上传配置中的单位
     * @param fileSize 实际文件大小
     * @param size     文件上传配置中的文件大小
     */
    private void checkFileSize(String unit, Long fileSize, Long size) {
        if (HfleConstant.StorageUnit.MB.equals(unit) && fileSize > size * HfleConstant.ENTERING * HfleConstant.ENTERING) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, size + unit);
        } else if (HfleConstant.StorageUnit.KB.equals(unit) && fileSize > size * HfleConstant.ENTERING) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, size + unit);
        }
    }

    @Override
    public void validateByteFileSize(File file) {
        Long tenantId = file.getTenantId();
        Long fileSize = file.getFileSize();
        CapacityConfig capacityConfig = capacityConfigService.selectByTenantId(tenantId);
        if (capacityConfig == null && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            // 获取平台配置
            capacityConfig = capacityConfigService.selectByTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        if (capacityConfig == null) {
            // 未配置，不校验
            return;
        }
        Long size = capacityConfig.getStorageSize();
        String unit = capacityConfig.getStorageUnit();
        this.checkFileSize(unit, fileSize, size);
        checkResidualCapacity(file.getTenantId());
    }

    @Override
    public UploadConfig detailUploadConfig(Long tenantId, Long uploadConfigId) {
        UploadConfig uploadConfig = new UploadConfig();
        uploadConfig.setTenantId(tenantId);
        uploadConfig.setUploadConfigId(uploadConfigId);
        return uploadConfigRepository.selectOne(uploadConfig);
    }

    @Override
    @ProcessLovValue(targetField = {HfleConstant.BODY_LIST_CONFIG})
    public UploadConfig detailUploadConfig(Long uploadConfigId) {
        return uploadConfigRepository.selectByPrimaryKey(uploadConfigId);
    }

    /**
     * 检查租户可用容量剩余
     *
     * @param tenantId 租户Id
     */
    private void checkResidualCapacity(Long tenantId) {
        // 检查租户可用容量剩余
        Long used = capacityUsedService.getCache(tenantId);
        if (used == null) {
            return;
        }
        CapacityConfig capacity = capacityConfigService.selectByTenantId(tenantId);
        if (capacity == null && BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            // 数据库未配置该租户数据, 对比平台默认容量配置
            capacity = capacityConfigService.selectByTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        if (capacity == null) {
            return;
        }
        Long totalCapacity = capacity.getTotalCapacity();
        String unit = capacity.getTotalCapacityUnit();
        if (HfleConstant.StorageUnit.MB.equals(unit)) {
            totalCapacity = totalCapacity * HfleConstant.ENTERING * HfleConstant.ENTERING;
        } else if (HfleConstant.StorageUnit.KB.equals(unit)) {
            totalCapacity = totalCapacity * HfleConstant.ENTERING;
        }
        if (used > totalCapacity) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_CAPACITY);
        }
    }
}

package io.choerodon.file.app.service.impl;

import org.hzero.file.app.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Autowired
    private FileService fileService;

    @Override
    public String cutImage(Long tenantId, String bucketName, MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        try {
            file = ImageUtils.cutImage(file, rotate, axisX, axisY, width, height);
            return fileService.uploadMultipart(tenantId, bucketName, null, null, file.getOriginalFilename(), 0, null, file);
        } catch (Exception e) {
            throw new CommonException("error.cut.and.upload.image", e);
        }
    }
}

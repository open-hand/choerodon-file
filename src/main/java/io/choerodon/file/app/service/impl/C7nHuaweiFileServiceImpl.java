package io.choerodon.file.app.service.impl;

import java.io.InputStream;
import java.util.Map;

import org.hzero.starter.file.entity.FileInfo;
import org.hzero.starter.file.service.HuaweiFileServiceImpl;

/**
 * @author scp
 * @since 2022/10/13
 */
public class C7nHuaweiFileServiceImpl extends HuaweiFileServiceImpl {


    @Override
    public String upload(FileInfo file, String filePath) {
        String fileUrl = super.upload(file, filePath);
        return fileUrl.replace("+", "%2B");
    }

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String fileUrl = super.upload(file, inputStream);
        return fileUrl.replace("+", "%2B");
    }

    @Override
    public String combine(FileInfo file, String uploadId, Map<String, String> map) {
        String fileUrl = super.combine(file, uploadId, map);
        return fileUrl.replace("+", "%2B");
    }

    @Override
    public String copyFile(FileInfo file, String oldFileKey, String oldBucketName) {
        String fileUrl = super.copyFile(file, oldFileKey, oldBucketName);
        return fileUrl.replace("+", "%2B");
    }

}

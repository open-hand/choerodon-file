package io.choerodon.file.app.service.impl;

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
}

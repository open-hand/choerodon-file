package io.choerodon.file.app.service.impl;

import org.hzero.starter.file.service.AbstractFileService;
import org.hzero.starter.file.service.HuaweiFileServiceImpl;
import org.hzero.starter.file.service.HuaweiStoreCreator;
import org.springframework.context.annotation.Primary;

/**
 * @author scp
 * @since 2022/10/13
 */
@Primary
public class C7nHuaweiStoreCreator extends HuaweiStoreCreator {
    @Override
    public AbstractFileService getFileService() {
        return new C7nHuaweiFileServiceImpl();
    }
}

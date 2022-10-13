package org.hzero.starter.file.service;

import org.springframework.stereotype.Component;

import io.choerodon.file.app.service.impl.C7nHuaweiFileServiceImpl;

/**
 * @author scp
 * @since 2022/10/13
 */
@Component
public class HuaweiStoreCreator implements StoreCreator {
    public HuaweiStoreCreator() {
    }

    public Integer storeType() {
        return 2;
    }

    @Override
    public AbstractFileService getFileService() {
        return new C7nHuaweiFileServiceImpl();
    }
}

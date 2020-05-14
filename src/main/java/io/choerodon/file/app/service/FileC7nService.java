package io.choerodon.file.app.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author scp
 * @date 2020/5/14
 * @description
 */
public interface FileC7nService {

    /**
     * 上传Multipart文件
     *
     * @param tenantId      租户Id
     * @param bucketName    桶
     * @param multipartFile 文件
     * @return 文件路径
     */
    String cutImage(Long tenantId,
                    String bucketName,
                    MultipartFile multipartFile,
                    Double rotate,
                    Integer axisX,
                    Integer axisY,
                    Integer width,
                    Integer height);

}

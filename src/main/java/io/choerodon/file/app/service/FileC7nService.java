package io.choerodon.file.app.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import io.choerodon.file.api.controller.vo.CiCdPipelineRecordVO;

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


    /**
     * 根据url和bucketName删除minio的文件，如果file表中也有数据，连带删除
     *
     * @param organizationId
     * @param bucketName
     * @param urls
     */
    void deleteByUrls(Long organizationId, String bucketName, List<String> urls);

    void auidt(CiCdPipelineRecordVO devopsPipelineVO);
}

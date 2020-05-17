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

    /**
     * 上传DevOps的CI过程产生的软件包
     *
     * @param tenantId      组织id
     * @param token         应用服务token
     * @param commit        此次ci的commit sha值
     * @param ciPipelineId  流水线id
     * @param ciJobId       流水线的job id
     * @param artifactName  软件包名称
     * @param multipartFile 软件包
     * @return 软件包地址， 如果有异常，返回400
     */
    String uploadDevOpsArtifactFile(Long tenantId, String token, String commit, Long ciPipelineId, Long ciJobId, String artifactName, MultipartFile multipartFile);
}

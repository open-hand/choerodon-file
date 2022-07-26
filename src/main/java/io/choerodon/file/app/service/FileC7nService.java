package io.choerodon.file.app.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.StorageConfig;
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
     * 根据url和bucketName删除minio的文件，如果file表中也有数据，连带删除
     *
     * @param organizationId
     * @param bucketName
     * @param urls
     */
    void deleteByUrls(Long organizationId, String bucketName, List<String> urls);

    /**
     * 上传Multipart文件
     * c7n定制化需求 返回的文件地址为 文件服务地址+fileKey
     *
     * @param tenantId       租户Id
     * @param bucketName     桶
     * @param attachmentUuid 附件ID
     * @param directory      文件夹
     * @param fileName       文件名
     * @param docType        是否锁定文件
     * @param storageCode    存储编码
     * @param multipartFile  文件
     * @return 文件路径 文件服务地址+fileKey
     */
    String uploadMultipart(Long tenantId, String bucketName, String attachmentUuid, String directory,
                           String fileName, Integer docType, String storageCode, MultipartFile multipartFile,
                           String prefix);


    /**
     * 下载文件
     * c7n定制化需求  配合c7n定制化文件下载使用
     *
     * @param request  request
     * @param response response
     * @param tenantId 租户ID
     * @param fileId   文件id
     */
    void downloadFile(HttpServletRequest request, HttpServletResponse response, Long tenantId, Long fileId);

    /**
     * 根据文件Id查询文件
     *
     * @param fileIds
     * @return
     */
    List<File> listFileByIds(List<Long> fileIds);

    /**
     * 删除文件
     *
     * @param tenantId
     * @param fileId
     */
    void deleteById(Long tenantId, Long fileId);

    /**
     * 存想对路径需要 查询默认存储配置
     *
     * @return
     */
    StorageConfig queryDefaultConfig();

    /**
     * 根据url查询文件
     * 不对url进行解码
     * 大文件上传文件url没有进行加码
     *
     * @param organizationId
     * @param fileUrl
     * @return
     */
    File queryFileWithUrl(Long organizationId, String bucketName, String fileUrl);

    List<File> queryFileDTOByIds(Long organizationId, List<String> fileKeys);

    void updateFile(Long organizationId, File file);

    /**
     * 大文件上传用文件夹唯一作为前缀方式
     * 因为某些地方拼接的文件地址 不可设置默认存储code为文件夹前缀
     * @param storageCode
     * @return
     */
    String getStorageCode(String storageCode);
}

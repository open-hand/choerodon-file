package io.choerodon.file.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.StorageConfig;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/14
 * @Modified By:
 */
public interface C7nFileMapper {

    List<File> selectFileByUrls(@Param("tenantId") Long tenantId,
                                @Param("bucketName") String bucketName,
                                @Param("urls") List<String> urls,
                                @Param("attachmentUUID") String attachmentUUID);


    StorageConfig queryDefaultConfig();

    File queryFileWithUrl(@Param("tenantId") Long tenantId,
                          @Param("bucketName") String bucketName,
                          @Param("url") String url);

    List<File> queryFileByKeys(@Param("organizationId") Long organizationId,
                                 @Param("fileKeys") List<String> fileKeys);
}

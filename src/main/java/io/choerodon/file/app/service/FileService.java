package io.choerodon.file.app.service;

import io.choerodon.file.api.dto.BucketCreateResultDTO;
import io.choerodon.file.api.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author HuangFuqiang@choerodon.io
 */
public interface FileService {

    String uploadFile(String backetName, String fileName, MultipartFile multipartFile);

    void deleteFile(String backetName, String url);

    FileDTO uploadDocument(String bucketName, String originFileName, MultipartFile multipartFile);

    /**
     * 返回处理后图片url
     *
     * @param file   图片
     * @param rotate 顺时针旋转的角度
     * @param axisX  裁剪的X轴
     * @param axisY  裁剪的Y轴
     * @param width  裁剪的宽度
     * @param height 裁剪的高度
     * @return
     */
    String cutImage(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height);


    /**
     * 创建无策略的Bucket
     *
     * @param bucketName
     * @return
     */
    BucketCreateResultDTO createBucketWithNonePolicy(String bucketName);

    /**
     * 获取文件的临时下载路径
     *
     * @param bucketName
     * @param url
     * @return 临时路径
     */
    String presignedGetObject(String bucketName, String url, Integer expires);


    /**
     * 获取文件的临时下载路径
     *
     * @param bucketName
     * @param urls
     * @return 临时路径
     */
    Map<String, String> presignedGetObjectList(String bucketName, List<String> urls, Integer expires);

}

package io.choerodon.file.app.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/4/16.
 * Email: fuqianghuang01@gmail.com
 */
public interface FileService {

    String uploadFile(String backetName, String fileName, MultipartFile multipartFile);

    void deleteFile(String backetName, String url);

}

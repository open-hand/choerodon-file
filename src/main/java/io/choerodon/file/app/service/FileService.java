package io.choerodon.file.app.service;

import io.choerodon.file.api.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author  HuangFuqiang@choerodon.io
 */
public interface FileService {

    String uploadFile(String backetName, String fileName, MultipartFile multipartFile);

    void deleteFile(String backetName, String url);

    FileDTO uploadDocument(String bucketName, String originFileName, MultipartFile multipartFile);

}

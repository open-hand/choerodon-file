package io.choerodon.file.app.service.impl;

import io.choerodon.file.api.dto.FileDTO;
import io.choerodon.file.app.service.FileService;
import io.choerodon.file.infra.exception.FileUploadException;
import io.minio.MinioClient;
import io.minio.policy.PolicyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author HuangFuqiang@choerodon.io
 */
@Service
public class FileServiceImpl implements FileService {

    private static final String FILE = "file";

    @Value("${minio.endpoint}")
    private String endpoint;

    @Autowired
    private MinioClient minioClient;

    public void setMinioClient(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String uploadFile(String bucketName, String fileName, MultipartFile multipartFile) {
        try {
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
                minioClient.setBucketPolicy(bucketName, FILE, PolicyType.READ_ONLY);
            }
            InputStream inputStream = multipartFile.getInputStream();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = FILE + "_" + uuid + "_" + fileName;
            minioClient.putObject(bucketName, fileName, inputStream, "application/octet-stream");
            return minioClient.getObjectUrl(bucketName, fileName);
        } catch (Exception e) {
            throw new FileUploadException("error.file.upload", e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String url) {
        try {
            boolean isExist = minioClient.bucketExists(bucketName);
            if (!isExist) {
                throw new FileUploadException("error.bucketName.notExist");
            }
            String prefixUrl = endpoint + "/" + bucketName + "/";
            int prefixUrlSize = prefixUrl.length();
            String fileName = url.substring(prefixUrlSize);
            if (StringUtils.isEmpty(fileName)
                    || minioClient.getObject(bucketName, fileName) == null) {
                throw new FileUploadException("error.file.notExist");
            }
            minioClient.removeObject(bucketName, fileName);
        } catch (Exception e) {
            throw new FileUploadException("error.file.delete", e);
        }
    }

    @Override
    public FileDTO uploadDocument(String bucketName, String originFileName, MultipartFile multipartFile) {
        try {
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
                minioClient.setBucketPolicy(bucketName, FILE, PolicyType.READ_ONLY);
            }
            InputStream inputStream = multipartFile.getInputStream();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String fileName = FILE + "_" + uuid + "_" + originFileName;
            minioClient.putObject(bucketName, fileName, inputStream, "application/octet-stream");
            return new FileDTO(endpoint, originFileName, fileName);
        } catch (Exception e) {
            throw new FileUploadException("error.file.upload", e);
        }
    }
}

package io.choerodon.file.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.file.api.dto.FileDTO;
import io.choerodon.file.app.service.FileService;
import io.minio.MinioClient;
import io.minio.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Override
    public String uploadFile(String backetName, String fileName, MultipartFile multipartFile) {
        try {
            MinioClient minioClient = new MinioClient(endpoint, accessKey, secretKey);
            boolean isExist = minioClient.bucketExists(backetName);
            if (isExist) {
                LOGGER.debug("Bucket already exists.");
            } else {
                minioClient.makeBucket(backetName);
                minioClient.setBucketPolicy(backetName, "", PolicyType.READ_ONLY);
            }
            InputStream is = multipartFile.getInputStream();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + "_" + fileName;
            minioClient.putObject(backetName, fileName, is, "application/octet-stream");
            return minioClient.getObjectUrl(backetName, fileName);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteFile(String backetName, String url) {
        try {
            MinioClient minioClient = new MinioClient(endpoint, accessKey, secretKey);
            boolean isExist = minioClient.bucketExists(backetName);
            if (!isExist) {
                throw new CommonException("error.backetName.notExist");
            }

            String prefixUrl = endpoint + "/" + backetName + "/";
            int prefixUrlSize = prefixUrl.length();
            String fileName = url.substring(prefixUrlSize);
            if (StringUtils.isEmpty(fileName)) {
                throw new CommonException("error.file.notExist");
            }
            if (minioClient.getObject(backetName, fileName) == null) {
                throw new CommonException("error.file.notExist");
            }
            minioClient.removeObject(backetName, fileName);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public FileDTO uploadDocument(String bucketName, String originFileName, MultipartFile multipartFile) {
        try {
            MinioClient minioClient = new MinioClient(endpoint, accessKey, secretKey);
            boolean isExist = minioClient.bucketExists(bucketName);
            if (isExist) {
                LOGGER.debug("Bucket already exists.");
            } else {
                minioClient.makeBucket(bucketName);
                minioClient.setBucketPolicy(bucketName, "", PolicyType.READ_ONLY);
            }
            InputStream inputStream = multipartFile.getInputStream();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String fileName = uuid + "_" + originFileName;
            minioClient.putObject(bucketName, fileName, inputStream, "application/octet-stream");
            return new FileDTO(endpoint, originFileName, fileName);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }
}

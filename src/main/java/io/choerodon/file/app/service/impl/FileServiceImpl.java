package io.choerodon.file.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.file.api.dto.BucketCreateResultDTO;
import io.choerodon.file.api.dto.FileDTO;
import io.choerodon.file.app.service.FileService;
import io.choerodon.file.infra.config.FileClient;
import io.choerodon.file.infra.exception.ApplicationTransferException;
import io.choerodon.file.infra.exception.FileUploadException;
import io.choerodon.file.infra.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HuangFuqiang@choerodon.io
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);


    @Value("${spring.application.name: file-service}")
    private String applicationName;

    private FileClient fileClient;

    public FileServiceImpl(FileClient fileClient) {
        this.fileClient = fileClient;
    }

    public void setFileClient(FileClient fileClient) {
        this.fileClient = fileClient;
    }

    @Override
    public String uploadFile(String bucketName, String originFileName, MultipartFile multipartFile) {
        bucketName = fileClient.getBucketName(bucketName);
        if (!fileClient.doesBucketExist(bucketName)) {
            fileClient.makeBucket(bucketName);
        }
        String fileName = fileClient.putObject(bucketName, originFileName, multipartFile);
        return fileClient.getObjectUrl(bucketName, fileName);
    }

    @Override
    public void deleteFile(String bucketName, String url) {
        bucketName = fileClient.getBucketName(bucketName);
        String fileName = null;
        try {
            String prefixUrl = fileClient.getPrefixUrl(bucketName);
            int prefixUrlSize = prefixUrl.length();
            fileName = url.substring(prefixUrlSize);
            if (StringUtils.isEmpty(fileName)
                    || fileClient.getObjectUrl(bucketName, fileName) == null) {
                throw new FileUploadException("error.file.notExist");
            }
            fileClient.removeObject(bucketName, fileName);
        } catch (Exception e) {
            LOGGER.error("delete file from file-service failed, bucketName: {}, fileName: {}", bucketName, fileName);
            throw new FileUploadException("error.file.delete", e);
        }
    }

    @Override
    public FileDTO uploadDocument(String bucketName, String originFileName, MultipartFile multipartFile) {
        bucketName = fileClient.getBucketName(bucketName);
        if (!fileClient.doesBucketExist(bucketName)) {
            fileClient.makeBucket(bucketName);
        }
        String fileName = fileClient.putObject(bucketName, originFileName, multipartFile);
        return new FileDTO(fileClient.getFileClientConfig().getEndpoint(), originFileName, fileName);
    }

    @Override
    public String cutImage(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        try {
            file = ImageUtils.cutImage(file, rotate, axisX, axisY, width, height);
            return uploadFile(fileClient.getBucketName(applicationName), file.getOriginalFilename(), file);
        } catch (Exception e) {
            throw new CommonException("error.cut.and.upload.image", e);
        }
    }


    @Override
    public BucketCreateResultDTO createBucketWithNonePolicy(String bucketName) {
        BucketCreateResultDTO resultDTO = new BucketCreateResultDTO();
        bucketName = fileClient.getBucketName(bucketName);
        resultDTO.setNewlyCreatedOne(!fileClient.doesBucketExist(bucketName));
        if (resultDTO.getNewlyCreatedOne()) {
            //bucket不存在则创建
            fileClient.makeBucketWithNonePolicy(bucketName);
            resultDTO.setCreatedSuccessfully(true);
        } else {
            //bucket存在则判断是否是策略为无的bucket
            resultDTO.setCreatedSuccessfully(fileClient.isBucketPolicyNone(bucketName));
        }
        return resultDTO;
    }

    @Override
    public String presignedGetObject(String bucketName, String url, Integer expires) {
        String fileName = null;
        try {
            // Check bucket
            bucketName = fileClient.getBucketName(bucketName);
            if (!fileClient.doesBucketExist(bucketName)) {
                throw new ApplicationTransferException("error.bucket.not.found");
            }

            // Check file
            String prefixUrl = fileClient.getPrefixUrl(bucketName);
            int prefixUrlSize = prefixUrl.length();
            fileName = url.substring(prefixUrlSize);
            if (StringUtils.isEmpty(fileName) || fileClient.getObjectUrl(bucketName, fileName) == null) {
                throw new ApplicationTransferException("error.file.not.found");
            }

            // Returns an presigned URL
            return fileClient.presignedGetObject(bucketName, fileName, expires);
        } catch (Exception e) {
            LOGGER.error("Failed to get temporary path, bucketName: {}, fileName: {}", bucketName, fileName);
            throw new ApplicationTransferException("error.get.temporary.path", e);
        }
    }

    @Override
    public Map<String, String> presignedGetObjectList(String bucketName, List<String> urls, Integer expires) {
        Map<String, String> resultMap = new HashMap<>();
        urls.forEach(url -> resultMap.put(url, presignedGetObject(bucketName, url, expires)));
        return resultMap;
    }
}

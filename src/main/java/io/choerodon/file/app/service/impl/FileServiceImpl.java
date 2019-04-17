package io.choerodon.file.app.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.file.api.dto.FileDTO;
import io.choerodon.file.app.service.FileService;
import io.choerodon.file.infra.config.FileClient;
import io.choerodon.file.infra.exception.FileUploadException;
import io.choerodon.file.infra.utils.ImageUtils;

/**
 * @author HuangFuqiang@choerodon.io
 */
@Service
public class FileServiceImpl implements FileService {

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
        try {
            String prefixUrl = fileClient.getPrefixUrl(bucketName);
            int prefixUrlSize = prefixUrl.length();
            String fileName = url.substring(prefixUrlSize);
            if (StringUtils.isEmpty(fileName)
                    || fileClient.getObjectUrl(bucketName, fileName) == null) {
                throw new FileUploadException("error.file.notExist");
            }
            fileClient.removeObject(bucketName, fileName);
        } catch (Exception e) {
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

}

package io.choerodon.file.infra.config;

import java.io.InputStream;
import java.util.UUID;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.policy.PolicyType;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.file.infra.exception.FileUploadException;

/**
 * @author dongfan117@gmail.com
 */
public class FileClient {
    private static final String FILE = "file";
    private MinioClient minioClient;
    private AmazonS3 amazonS3;
    private boolean isAwsS3;
    private FileClientConfiguration fileClientConfig;

    public FileClient(FileClientConfiguration fileClientConfig) {
        this.fileClientConfig = fileClientConfig;
        this.isAwsS3 = fileClientConfig.getAwsS3();
    }

    public void initClient(boolean isAwsS3) throws InvalidEndpointException, InvalidPortException {
        if (isAwsS3) {
            initAmazonS3();
        } else {
            initMinioClient();
        }
    }

    public void initClient() throws InvalidEndpointException, InvalidPortException {
        this.initClient(this.isAwsS3);
    }

    private void initAmazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(
                fileClientConfig.getAccessKey(), fileClientConfig.getSecretKey());
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignerOverride("S3SignerType");
        String region = fileClientConfig.getRegion() == null ? fileClientConfig.US_EAST_1 : fileClientConfig.getRegion();
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                fileClientConfig.getEndpoint(),
                                region))
                .withPathStyleAccessEnabled(fileClientConfig.getWithPath())
                .build();

    }

    private void initMinioClient() throws InvalidEndpointException, InvalidPortException {
        if (fileClientConfig.getRegion() != null) {
            this.minioClient = new MinioClient(
                    fileClientConfig.getEndpoint(),
                    fileClientConfig.getAccessKey(),
                    fileClientConfig.getSecretKey(),
                    fileClientConfig.getRegion());
        } else {
            this.minioClient = new MinioClient(
                    fileClientConfig.getEndpoint(),
                    fileClientConfig.getAccessKey(),
                    fileClientConfig.getSecretKey());
        }

    }

    public boolean isAwsS3() {
        return isAwsS3;
    }

    public void setAwsS3(boolean awsS3) {
        isAwsS3 = awsS3;
    }

    public FileClientConfiguration getFileClientConfig() {
        return fileClientConfig;
    }

    public void setFileClientConfig(FileClientConfiguration fileClientConfig) {
        this.fileClientConfig = fileClientConfig;
    }

    public boolean doesBucketExist(String bucketName) {
        try {
            if (isAwsS3) {
                return amazonS3.doesBucketExistV2(bucketName);
            } else {
                return minioClient.bucketExists(bucketName);
            }
        } catch (Exception e) {
            throw new FileUploadException("error.file.bucket.error", e);
        }
    }

    public void makeBucket(String bucketName) {
        try {
            if (isAwsS3) {
                amazonS3.createBucket(bucketName);
            } else {
                minioClient.makeBucket(bucketName);
                minioClient.setBucketPolicy(bucketName, FILE, PolicyType.READ_ONLY);
            }
        } catch (Exception e) {
            throw new FileUploadException("error.file.bucket.error", e);
        }
    }

    public String getObjectUrl(String bucketName, String fileName) {
        String objectUrl = "";
        try {
            if (isAwsS3) {
                objectUrl = this.amazonS3.getUrl(bucketName, fileName).toString();
            } else {
                objectUrl = this.minioClient.getObjectUrl(bucketName, fileName);
            }
        } catch (Exception e) {
            throw new FileUploadException("error.file.notExist", e);
        }
        return objectUrl;
    }

    public String putObject(String bucketName, String originFileName, MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName = FILE + "_" + uuid + "_" + originFileName;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            if (isAwsS3) {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.addUserMetadata("Content-Type", "application/octet-stream");
                amazonS3.putObject(bucketName, fileName, inputStream, objectMetadata);
            } else {
                minioClient.putObject(bucketName, fileName, inputStream, "application/octet-stream");
            }
        } catch (Exception e) {
            throw new FileUploadException("error.file.upload", e);
        }
        return fileName;
    }

    public void removeObject(String bucketName, String objectName) {
        try {
            if (isAwsS3) {
                amazonS3.deleteObject(bucketName, objectName);
            } else {
                minioClient.removeObject(bucketName, objectName);
            }
        } catch (Exception e) {
            throw new FileUploadException("error.file.delete", e);
        }
    }


    public String getPrefixUrl(String bucketName) {
        if (!this.fileClientConfig.getWithPath()) {
            return this.fileClientConfig.getEndpoint() + "/" + bucketName + "/";
        }
        return "";
    }

    public String getBucketName(String bucketName) {
        if (this.fileClientConfig.getAppId() != null) {
            return bucketName + "-" + this.fileClientConfig.getAppId();
        }
        return bucketName;
    }
}

package io.choerodon.file.infra.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dongfan117@gmail.com
 */
@Configuration
public class AwsS3Configuration {
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.region}")
    private String region;

    @Value("${minio.withPath: false}")
    private Boolean withPath;

    @Value("${minio.isAwsS3: false}")
    private Boolean isAwsS3;

    @Bean()
    public AmazonS3 amazonS3() throws InvalidEndpointException, InvalidPortException {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignerOverride("S3SignerType");
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                endpoint, region)).withPathStyleAccessEnabled(withPath)
                .build();
        return s3Client;
    }

    @Bean
    public FileClient fileClient() throws InvalidEndpointException, InvalidPortException {
        FileClientConfiguration fileClientConfig = new FileClientConfiguration(
                endpoint, accessKey, secretKey, region, withPath, isAwsS3
        );
        FileClient fileClient = new FileClient(fileClientConfig);
        fileClient.initClient();
        return fileClient;
    }
}

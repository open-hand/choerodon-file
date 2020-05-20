package io.choerodon.file.infra.config;

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

    @Value("${minio.region:#{null}}")
    private String region;

    @Value("${minio.withPath:false}")
    private Boolean withPath;

    @Value("${minio.isAwsS3:false}")
    private Boolean isAwsS3;

    @Value("${minio.appId:#{null}}")
    private String appId;

    @Bean
    public FileClient fileClient() throws InvalidEndpointException, InvalidPortException {
        FileClientConfiguration fileClientConfig = new FileClientConfiguration(
                endpoint, accessKey, secretKey, region, withPath, isAwsS3
        );
        if (appId != null) {
            fileClientConfig.setAppId(appId);
        }
        FileClient fileClient = new FileClient(fileClientConfig);
        fileClient.initClient();
        return fileClient;
    }
}

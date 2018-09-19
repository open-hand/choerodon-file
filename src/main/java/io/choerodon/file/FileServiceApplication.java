package io.choerodon.file;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

/**
 * @author HuangFuqiang@choerodon.io
 */
@EnableFeignClients("io.choerodon")
@EnableChoerodonResourceServer
@EnableAsync
@EnableEurekaClient
@SpringBootApplication
public class FileServiceApplication {
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class);
    }

    @Bean
    public MinioClient minioClient() throws InvalidEndpointException, InvalidPortException {
        return new MinioClient(endpoint, accessKey, secretKey);
    }
}

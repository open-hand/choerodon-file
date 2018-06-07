package io.choerodon.file;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author HuangFuqiang@choerodon.io
 */
@EnableFeignClients("io.choerodon")
@EnableChoerodonResourceServer
@EnableAsync
@EnableEurekaClient
@SpringBootApplication
public class FileServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class);
    }
}

package io.choerodon.file;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/4/16.
 * Email: fuqianghuang01@gmail.com
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

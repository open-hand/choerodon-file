package io.choerodon.file;

import io.choerodon.actuator.metadata.IMetadataDriver;
import io.choerodon.actuator.metadata.impl.BaseMetadataDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
@SpringBootApplication(scanBasePackages = {
        "io.choerodon.file",
        "io.choerodon.actuator.endpoint",
})
public class FileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class);
    }

    /**
     * 因为io.choerodon.actuator必须要数据库连接
     * 自动注入exclude io.choerodon.actuator.ActuatorAutoConfiguration
     * 但需要IMetadataDriver bean，所以自定义一个bean
     * 但是DataSource为空，会导致访问localhost:9090/choerodon/metadata空指针异常
     * 如果io.choerodon.actuator拆分移除了jdbc依赖可以删掉改代码
     * @return
     */
    @Bean
    public IMetadataDriver metadataDriver() {
        return new BaseMetadataDriver(null);
    }
}

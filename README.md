# File Service

`file-service`基于一个minio服务创建，它可以提供文件的上传与下载服务。

## 特性

- 在完成上传一个文件到服务器后，会返回一个http url。
- 在完成删除一个文件后，不会返回任何信息。

## 准备

- io.minio

在开启服务前，你需要在 `application.yml`文件中配置minio服务`endpoint`, `accessKey` and `secretKey`。

例：

```yml
minio:
  endpoint: http:127.0.0.1:8888/minio
  accessKey: choerodon
  secretKey: 123456
```

## 配置文件
application.yml
```application.yml
spring:
  servlet:
    multipart:
      max-file-size: 30MB
      max-request-size: 30MB
eureka:
  instance:
    preferIpAddress: true
    leaseRenewalIntervalInSeconds: 1
    leaseExpirationDurationInSeconds: 3
  client:
    serviceUrl:
      defaultZone: http://localhost:8000/eureka/
    registryFetchIntervalSeconds: 1
hystrix:
  shareSecurityContext: true
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 40000
          timeout:
            enabled: false
ribbon:
  ConnectTimeout: 10000
  ReadTimeout: 30000
feign:
  hystrix:
    enabled: true
minio:
  endpoint: http://127.0.0.1:8888
  accessKey: choerodon
  secretKey: 123456
  isAwsS3: false
  withPath: false
```
bootstrap.yml
```bootstrap.yml
server:
  port: 9090
spring:
  application:
    name: file-service
  cloud:
    config:
      uri: http://127.0.0.1:8010/
      enabled: false
      fail-fast: true
      retry:
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.5
management:
  endpoint:
    health:
      show-details: ALWAYS
  server:
    port: 9091
  endpoints:
    web:
      exposure:
        include: '*'
```

## 安装和启动步骤
 
  * 需要服务：
  `oauth-server`,`api-gateway`,`manager-service`,本地环境需要 `eureka-server`，线上环境需要使用 `go-register-server`

  * [构建镜像服务](https://github.com/minio/minio)

  * 你需要像这样在`api-gateway`中配置路由信息：
   ```java
       zuul:
         addHostHeader: true
         routes:

           file:
             path: /file/**
             serviceId: file-service
   ```

  然后你能够使用feign调用`file-service`。

  * 启动项目:
  项目根目录下运行 `mvn spring-boot:run` 命令，或者在本地集成环境中运行 `SpringBoot` 启动类 `io.choerodon.file.FileServiceApplication`


## 链接

* [更新日志](./CHANGELOG.zh-CN.md)

## 如何贡献

欢迎参与我们的项目! 欲知更多信息请关注 [贡献说明](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)。
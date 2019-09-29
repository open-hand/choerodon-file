# File Service

Choerodon File Service 基于一个minio服务创建，它可以提供文件的上传与下载服务。

## 特性

- 在完成上传一个文件到服务器后，会返回一个http url。
- 在完成删除一个文件后，不会返回任何信息。

## 服务配置

- `application.yml`

  ```yaml
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

- `bootstrap.yml`

  ```yaml
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

## 准备

- [构建minio镜像](https://github.com/minio/minio)
- 配置minio服务

在开启服务前，你需要在 `application.yml`文件中配置minio服务`endpoint`, `accessKey` and `secretKey`。

示例如下：

```yaml
minio:
  endpoint: http://127.0.0.1:8888/minio
  accessKey: choerodon
  secretKey: 123456
```
## 环境需求

- 该项目是一个 Eureka Client 项目，启动后需要注册到 `EurekaServer`，本地环境需要 `eureka-server`，线上环境需要使用 `go-register-server`

## 安装和启动步骤
 
- 运行 `eureka-server`，[代码库地址](https://code.choerodon.com.cn/choerodon-framework/eureka-server.git)。

- 启动项目，项目根目录下执行如下命令：

  ```sh
   mvn spring-boot:run
  ```
  
## 更新日志

* [更新日志](./CHANGELOG.zh-CN.md)


## 如何参与

欢迎参与我们的项目，了解更多有关如何[参与贡献](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)的信息。
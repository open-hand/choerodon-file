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

## 安装和启动步骤
 
  * 需要服务： `register-service`,`oauth-service`,`api-gateway`,`gateway-helper`,`config-service`,`manager-service`。
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
   
  * 下面这个例子展示了一个典型的用于运行Spring Boot应用的Maven命令：
  
  ```java
    mvn spring-boot:run
  ```


## 链接

* [更新日志](./CHANGELOG.zh-CN.md)

## 如何贡献

欢迎提出想法! 欲知更多信息请关注 [贡献说明](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)。
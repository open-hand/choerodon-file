# File Service

The `file-service` is built on minio server, we can use it to upload and delete files.

## Feature

- After uploading the file to the server, a http url will be returned.
- There will be nothing to return after deleting file.

## Requirements

Before starting this server, you shoud config the minio server `endpoint`, `accessKey` and `secretKey` in the `application.yml`.

For example:

```yml
minio:
  endpoint: http:127.0.0.1:8888/minio
  accessKey: choerodon
  secretKey: 123456
```

## Installation and Getting Started
 
  * `register-service`,`oauth-service`,`api-gateway`,`gateway-helper`,`config-service`,`manager-service` is required.
  * [Build the minio server](https://github.com/minio/minio)
  * You need to config the route info on `api-gateway` like this:
   ```java
       zuul:
         addHostHeader: true
         routes:
          
           file:
             path: /file/**
             serviceId: file-service
   ```
  
  Then you can use feign to invoke the `file-service`.
  
  * The following example shows a typical Maven command to run a Spring Boot application: 
  
  ```java
    mvn spring-boot:run
  ```

## Dependencies

- io.minio

## Links

* [Change Log](./CHANGELOG.zh-CN.md)


Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.
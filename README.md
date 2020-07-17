## 使用说明

### 概述

文件服务为HZERO系统提供简单易用的文件存储功能，具备对接多种云对象存储服务的能力且易于拓展，同时支持服务器ftp协议文件上传，支持大文件断点续传、文件预览、word在线编辑、pdf水印

## 服务配置 

- `服务配置参数`
```
# 文件上传大小限制
spring.servlet.multipart.max-file-size: 30MB
spring.servlet.multipart.max-request-size: 30MB

# 租户允许的最大存储容量,String类型,单位允许MB和KB,   默认值 10240MB  
hzero.file.maxCapacitySize

# 文件授权url的有效访问时间,Long类型,默认值300L
hzero.file.defaultExpires

# 华为、百度跨域配置 List<String>  若不设置，表示允许所有跨域
hzero.file.origins

# 对象存储忽略证书验证，默认值false
hzero.file.ignoreCertCheck

# 分片上传阈值,超过时开启分片上传, 默认值5242880  (5M)
hzero.file.shardingThreshold

# 分片大小, 默认值1048576  (1M)
hzero.file.defaultSharedSize

# 分片页面需要使用的网关地址， 示例：http://hzerodevb.saas.hand-china.com/hfle
hzero.file.gatewayPath

# 文件预览的方式 String类型 允许的值： aspose  kkFileView  onlyOffice
hzero.file.previewType

# kkFileView的文件预览地址，previewType为kkFileView时需要指定
hzero.file.kkFileViewUrl
```
- `数据初始化`

在`src/main/resources/script/db`下已有数据库初始化excel文件与groovy脚本示例，
此模板在部署时有一个初始化数据的阶段，所以部署此模板创建好的应用之前，应该去数据库中
创建一个数据库。并且部署时需要将对应的数据库名称填写正确


部署时须有三处需要填写数据库，如下所示
```yml
preJob:
  preConfig:
    configFile: application-default.yml
    mysql:
      host: 192.168.12.175
      port: 3306
      database: manager_service #manager service对应的数据库
      username: root
      password: choerodon
  preInitDB:
    mysql:
      host: 192.168.12.175
      port: 3306
      database: demo_service #初始化数据对应的数据库
      username: root
      password: choerodon

deployment:
  managementPort: 8091

env:
  open:
    EUREKA_DEFAULT_ZONE: http://register-server.io-choerodon:8000/eureka/
    #服务启动时对应的数据库
    SPRING_DATASOURCE_URL: jdbc:mysql://localhost/demo_service?useUnicode=true&characterEncoding=utf-8&useSSL=false
    SPRING_DATASOURCE_USERNAME: root
    SPRING_DATASOURCE_PASSWORD: choerodon
```

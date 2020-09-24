# choerodon-file
文件管理

## Introduction
文件管理，为平台提供文件存储服务。此服务依赖[hzero-file](https://github.com/open-hand/hzero-file.git)未进行特性化开发；与`hzero-file`不同在于`choerodon-file`支持MINIO。

## Documentation
- 更多详情请参考`hzero-file`[中文文档](http://open.hand-china.com/document-center/doc/application/10029/10161?doc_id=4451)

## Features
- 文件存储配置：对象存储配置，支持多种云服务
- 文件上传配置：租户存储容量及指定目录存储文件格式限制
- 文件汇总查询：记录所有上传的文件
- 服务器上传配置：服务器上传配置，支持FTP/SFTP协议
- 文件水印配置：文件的水印配置，支持文字水印和图片水印

## Architecture

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/2fb5eb72740f4022b49edbe16669c24d/20200713104656.png)

## Dependencies


* 服务依赖

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-file-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

## Data initialization

- 创建数据库，本地创建 `hzero_file` 数据库和默认用户，示例如下：

  ```sql
  CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
  CREATE DATABASE hzero_file DEFAULT CHARACTER SET utf8;
  GRANT ALL PRIVILEGES ON hzero_file.* TO choerodon@'%';
  FLUSH PRIVILEGES;
  ```

- 初始化 `hzero_file` 数据库，运行项目根目录下的 `init-database.sh`，该脚本默认初始化数据库的地址为 `localhost`，若有变更需要修改脚本文件

  ```sh
  sh init-database.sh
  ```
  
## Changelog

- [更新日志](./CHANGELOG.zh-CN.md)


## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, CHOERODON
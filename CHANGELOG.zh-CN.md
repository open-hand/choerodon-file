# Changelog

这个项目的所有显著变化都将被记录在这个文件中。

## [0.16.0] - 2019-04-19

### 更新

- 升级基础镜像为0.8.0

## [0.15.0] - 2019-03-19

### 更新

- 升级`spring boot`版本为`2.0.6`。
- 升级`spring cloud`版本为`Finchley.SR2`。

### 修改

- 修改ci文件

## [0.13.0] - 2019-01-08

### 新增

- 新增可裁剪图片的通用接口

### 修改

- 升级`choerodon-starter`依赖版本为`0.9.0.RELEASE`。


## [0.12.0] - 2018-12-12

### 修改

- 文件服务如果发生服务内部错误，不在抛CommonException(HttpStatus=200)，而是抛FileUploadException(HttpStatus=500)，客户端调用的时候需要做try catch操作，自行处理异常

### 移除

- 移除hystrix-stream依赖
- 移除kafka依赖及相关配置

## [0.11.0] - 2018-11-13

- 更新了基础镜像

## [0.10.0] - 2018-09-27

### 修改

- 更新license 
- 修改了ci文件
- 更新了基础镜像
- 修改chart健康检查readinessProbe

### 新增

- 添加单元测试

## [0.9.0] - 2018-08-17

### 修改

- 升级`choerodon-framework-parent`依赖版本为`0.8.0.RELEASE`。
- 升级`choerodon-starter`依赖版本为`0.6.0.RELEASE`。

## [0.8.0] - 2018-07-20

### 修改

- 升级choerodon-starter依赖版本为0.5.4.RELEASE。

## [0.7.0] - 2018-06-22

### 修改

- 升级了chart中dbtool的版本为0.5.2。
- 升级choerodon-starter依赖版本为0.5.3.RELEASE。

### 删除

- 删除了过期的上传和删除接口 

## [0.6.0] - 2018-06-08

### 修改

- 修改了上传和删除文件接口url。
- 接口权限设置成全局层。
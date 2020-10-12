# Choerodon choerodon-file
 文件服务为HZERO系统提供简单易用的文件存储功能，具备对接多种云对象存储服务的能力且易于拓展，同时支持服务器ftp协议文件上传，支持大文件断点续传、文件预览、word在线编辑、pdf水印

## Introduction

## Add Helm chart repository

``` bash    
helm repo add choerodon https://openchart.choerodon.com.cn/choerodon/c7n
helm repo update
```

## Installing the Chart

```bash
$ helm install c7n/choerodon-file --name choerodon-file
```

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`.

## Uninstalling the Chart

```bash
$ helm delete choerodon-file
```

## Configuration

Parameter | Description	| Default
--- |  ---  |  ---  
`replicaCount` | pod运行数量 | `1`
`image.repository` | 镜像库地址 | `registry.choerodon.com.cn/choerodon/choerodon-file`
`image.pullPolicy` | 镜像拉取策略 | `IfNotPresent`
`preJob.timeout` | job超时时间 | `300`
`preJob.image` | job镜像库地址 | `registry.cn-hangzhou.aliyuncs.com/choerodon-tools/dbtool:0.6.4`
`preJob.preInitDB.enabled` | 是否初始hzero_file数据库 | `true`
`preJob.preInitDB.datasource.url` | hzero_file数据库连接地址 | `jdbc:mysql://localhost:3306/hzero_file?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true`
`preJob.preInitDB.datasource.username` | hzero_file数据库用户名 | `choerodon`
`preJob.preInitDB.datasource.password` | hzero_file数据库密码 | `password`
`preJob.preInitDB.datasource.driver` | hzero_file数据库密码 | `com.mysql.jdbc.Driver`
`metrics.path` | 收集应用的指标数据路径 | `/actuator/prometheus`
`metrics.group` | 性能指标应用分组 | `spring-boot`
`logs.parser` | 日志收集格式 | `spring-boot`
`deployment.managementPort` | 服务管理端口 | `8111`
`ingress.enabled` | 是否创建k8s ingress | `false`
`env.open.SPRING_CLOUD_CONFIG_ENABLED` | 是否启用配置中心 | `true`
`env.open.SPRING_CLOUD_CONFIG_URI` | 配置中心地址 | `http://register-server:8000`
`env.open.SPRING_DATASOURCE_URL` | 数据库连接地址 | `jdbc:mysql://localhost/hzero_file?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true`
`env.open.SPRING_DATASOURCE_USERNAME` | 数据库用户名 | `choerodon`
`env.open.SPRING_DATASOURCE_PASSWORD` | 数据库密码 | `password`
`env.open.SPRING_REDIS_HOST` | redis主机地址 | `localhost`
`env.open.SPRING_REDIS_PORT` | redis端口 | `6379`
`env.open.SPRING_REDIS_DATABASE` | redis db | `1`
`env.open.FILE_GATEWAY_URL` | 文件服务地址 | `http://hzeronb.saas.hand-china.com/hfle`
`env.open.MINIO_ENDPOINT` | minio地址 | `http://minio.example.com/minio`
`env.open.MINIO_ACCESSKEY` | minio accessKey | `accessKey`
`env.open.MINIO_SECRETKEY` | MINIO_SECRETKEY | `secretKey`
`env.open.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | 注册服务地址 | `http://register-server.io-choerodon:8000/eureka/`
`service.enabled` | 是否创建k8s service | `false`
`service.type` |  service类型 | `ClusterIP`
`service.port` | service端口 | `8110`
`service.name` | service名称 | `choerodon-file`
`resources.limits` | k8s中容器能使用资源的资源最大值 | `3Gi`
`resources.requests` | k8s中容器使用的最小资源需求 | `2Gi`

### SkyWalking Configuration
Parameter | Description
--- |  --- 
`javaagent` | SkyWalking 代理jar包(添加则开启 SkyWalking，删除则关闭)
`skywalking.agent.application_code` | SkyWalking 应用名称
`skywalking.agent.sample_n_per_3_secs` | SkyWalking 采样率配置
`skywalking.agent.namespace` | SkyWalking 跨进程链路中的header配置
`skywalking.agent.authentication` | SkyWalking 认证token配置
`skywalking.agent.span_limit_per_segment` | SkyWalking 每segment中的最大span数配置
`skywalking.agent.ignore_suffix` | SkyWalking 需要忽略的调用配置
`skywalking.agent.is_open_debugging_class` | SkyWalking 是否保存增强后的字节码文件
`skywalking.collector.backend_service` | SkyWalking OAP 服务地址和端口配置

```bash
$ helm install c7n/choerodon-file \
    --set env.open.SKYWALKING_OPTS="-javaagent:/agent/skywalking-agent.jar -Dskywalking.agent.application_code=choerodon-file  -Dskywalking.agent.sample_n_per_3_secs=-1 -Dskywalking.collector.backend_service=oap.skywalking:11800" \
    --name choerodon-file
```

## 验证部署
```bash
curl -s $(kubectl get po -n c7n-system -l choerodon.io/release=choerodon-file -o jsonpath="{.items[0].status.podIP}"):8111/actuator/health | jq -r .status
```
出现以下类似信息即为成功部署

```bash
UP
```
## 使用说明

### 数据初始化

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

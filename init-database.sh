#!/usr/bin/env bash
MAVEN_LOCAL_REPO=$(cd / && mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
TOOL_GROUP_ID=io.choerodon
TOOL_ARTIFACT_ID=choerodon-tool-liquibase
TOOL_VERSION=0.19.1.RELEASE
TOOL_JAR_PATH=${MAVEN_LOCAL_REPO}/${TOOL_GROUP_ID/\./\/}/${TOOL_ARTIFACT_ID}/${TOOL_VERSION}/${TOOL_ARTIFACT_ID}-${TOOL_VERSION}.jar
mvn org.apache.maven.plugins:maven-dependency-plugin:get \
 -Dartifact=${TOOL_GROUP_ID}:${TOOL_ARTIFACT_ID}:${TOOL_VERSION} \
 -Dtransitive=false

java -Dspring.datasource.url="jdbc:mysql://localhost:3306?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true" \
 -Dspring.datasource.username=root \
 -Dspring.datasource.password=root \
 -Ddata.init=true \
 -Dinstaller.jarPath=target/app.jar \
 -jar ${TOOL_JAR_PATH}

# 多数据源配置添加如下配置，将test改为数据源名称
# -Dspring.datasource.dynamic.datasource.test.driver-class-name="com.mysql.jdbc.Driver" \
# -Dspring.datasource.dynamic.datasource.test.url="jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT" \
# -Dspring.datasource.dynamic.datasource.test.username="root"
# -Dspring.datasource.dynamic.datasource.test.password="root"

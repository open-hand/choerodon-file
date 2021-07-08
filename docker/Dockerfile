FROM registry.cn-shanghai.aliyuncs.com/c7n/javabase:jdk8u282-b08
RUN mkdir /choerodon && chown -R www-data:www-data /choerodon
COPY --chown=www-data:www-data app.jar /choerodon/choerodon-file.jar
WORKDIR /choerodon
USER 33
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /choerodon/choerodon-file.jar
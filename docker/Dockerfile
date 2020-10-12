FROM registry.cn-shanghai.aliyuncs.com/c7n/javabase:0.9.0
WORKDIR /choerodon
COPY app.jar choerodon-file.jar
RUN chown -R www-data:www-data /choerodon
USER 33
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /choerodon/choerodon-file.jar
FROM registry.cn-shanghai.aliyuncs.com/c7n/javabase:0.9.0
WORKDIR /choerodon
COPY app.jar hzero-file.jar
RUN chown -R www-data:www-data /choerodon
USER www-data
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /choerodon/hzero-file.jar
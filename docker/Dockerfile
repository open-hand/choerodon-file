FROM registry.cn-shanghai.aliyuncs.com/choerodon-tools/javabase:0.8.2
COPY app.jar /file-service.jar
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /file-service.jar

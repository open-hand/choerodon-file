FROM registry.cn-shanghai.aliyuncs.com/c7n/javabase:0.9.0
COPY app.jar /file-service.jar
RUN chown -R www-data:www-data /file-service.jar
USER www-data
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /file-service.jar
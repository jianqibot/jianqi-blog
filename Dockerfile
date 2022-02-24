# syntax=docker/dockerfile:1
FROM adoptopenjdk/openjdk11:jre-11.0.9_11.1-alpine
#RUN mkdir /usr/src/my-app
WORKDIR /usr/src/my-app
COPY target/jianqi-blog-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java", "-jar", "jianqi-blog-0.0.1-SNAPSHOT.jar"]


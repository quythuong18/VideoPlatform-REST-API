FROM amazoncorretto:17-alpine-jdk
LABEL maintainer="quythuong"
RUN apk --no-cache add ffmpeg
COPY target/VideoPlatformAPI-0.0.1-SNAPSHOT.jar MainApp.jar
ENTRYPOINT ["java", "-jar", "/MainApp.jar"]
FROM amazoncorretto:17-alpine-jdk
LABEL maintainer="quythuong"
RUN apk --no-cache add ffmpeg
ENV FFMPEG_PATH=/usr/bin/ffmpeg
ENV FFPROBE_PATH=/usr/bin/ffprobe
COPY target/VideoPlatformAPI-0.0.1-SNAPSHOT.jar MainApp.jar
ENTRYPOINT ["java", "-jar", "/MainApp.jar"]
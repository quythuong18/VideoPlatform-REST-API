FROM nvidia/cuda:12.3.1-base-ubuntu22.04
LABEL maintainer="quythuong"
RUN apt update \
    && apt install -y \
    openjdk-17-jdk \
    ffmpeg \
    nvidia-cuda-toolkit

COPY target/VideoPlatformAPI-0.0.1-SNAPSHOT.jar MainApp.jar
ENTRYPOINT ["java", "-jar", "/MainApp.jar"]

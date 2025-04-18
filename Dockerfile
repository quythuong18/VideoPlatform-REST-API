FROM nvidia/cuda:12.3.1-base-ubuntu22.04
LABEL maintainer="quythuong"
RUN apt update \
    && apt install -y  \
    openjdk-17-jdk \
    build-essential \
    pkg-config \
    git \
    curl \
    ca-certificates \
    yasm \
    nasm \
    libx264-dev \
    libx265-dev \
    libnuma-dev \
    libvpx-dev \
    libfdk-aac-dev \
    libmp3lame-dev \
    libopus-dev \
    libass-dev \
    libfreetype6-dev \
    libvorbis-dev \
    libxcb1-dev \
    libxcb-shm0-dev \
    libxcb-xfixes0-dev \
    texinfo \
    zlib1g-dev \
    libunistring-dev \
    libtool \
    autoconf \
    automake \
    cmake \
    wget \
    nvidia-cuda-toolkit \
    && rm -rf /var/lib/apt/lists/*
    # Add this BEFORE building ffmpeg
RUN git clone https://github.com/FFmpeg/nv-codec-headers.git /tmp/nv-codec-headers && \
    cd /tmp/nv-codec-headers && \
    make && \
    make install && \
    rm -rf /tmp/nv-codec-headers
RUN mkdir -p /opt/ffmpeg && \
    cd /opt && \
    git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg && \
    cd ffmpeg && \
    ./configure \
      --enable-nonfree \
      --enable-cuda \
      --enable-cuvid \
      --enable-nvenc \
      --enable-libx264 \
      --enable-libx265 \
      --enable-gpl \
      --enable-libvpx \
      --enable-libfdk-aac \
      --enable-libmp3lame \
      --enable-libopus \
      --enable-libass \
      --enable-libfreetype \
      --extra-cflags=-I/usr/local/cuda/include \
      --extra-ldflags=-L/usr/local/cuda/lib64 \
      --enable-shared \
      --prefix=/usr/local && \
    make -j"$(nproc)" && \
    make install && \
    ldconfig

COPY target/VideoPlatformAPI-0.0.1-SNAPSHOT.jar MainApp.jar
ENTRYPOINT ["java", "-jar", "/MainApp.jar"]

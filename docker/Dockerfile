FROM reactivecircus/android-sdk:latest

MAINTAINER Arunkumar "hi@arunkumar.dev"

RUN apt-get update && apt-get install -y graphviz

ENV GRADLE_USER_HOME=/root/.gradle

COPY ./cache/.gradle /root/.gradle
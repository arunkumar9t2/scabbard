FROM reactivecircus/android-sdk:v2020_04_26-14-11-07

MAINTAINER Arunkumar "hi@arunkumar.dev"

RUN apt-get update && apt-get install -y graphviz

ENV GRADLE_USER_HOME=/root/.gradle

COPY ./cache/.gradle /root/.gradle
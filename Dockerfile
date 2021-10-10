FROM ubuntu:20.04

RUN apt-get -y update
RUN apt-get -y install wget

RUN wget https://download.java.net/java/early_access/loom/2/openjdk-18-loom+2-74_linux-x64_bin.tar.gz
RUN tar xvzf openjdk-18-loom+2-74_linux-x64_bin.tar.gz
RUN rm -rf openjdk-18-loom+2-74_linux-x64_bin.tar.gz

RUN export JAVA_HOME=/jdk-18
RUN export PATH=${JAVA_HOME}/bin:${PATH}

ENV JAVA_HOME=/jdk-18
ENV PATH=${JAVA_HOME}/bin:${PATH}

ADD target/redis-and-loom-1.0-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
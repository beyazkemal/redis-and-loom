FROM alpine:latest

RUN wget https://download.java.net/java/early_access/loom/2/openjdk-18-loom+2-74_linux-x64_bin.tar.gz
RUN tar xvzf openjdk-18-loom+2-74_linux-x64_bin.tar.gz
RUN rm -rf openjdk-18-loom+2-74_linux-x64_bin.tar.gz

ADD target/redis-and-loom-1.0-jar-with-dependencies.jar app.jar

ENTRYPOINT jdk-18/bin/java -jar app.jar
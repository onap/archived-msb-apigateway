FROM openjdk:8.0.72
ADD msb-apigateway*.tar.gz /opt/application

ENV LD_LIBRARY_PATH /lib64
WORKDIR /opt/application/msb-apigateway
ENTRYPOINT exec $PWD/startup4docker.sh
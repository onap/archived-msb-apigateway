FROM alpine:3.5
ADD msb-apigateway*.tar.gz /opt/application

ENV LD_LIBRARY_PATH /lib64
WORKDIR /opt/application/msb-apigateway
ENTRYPOINT exec $PWD/startup4docker.sh
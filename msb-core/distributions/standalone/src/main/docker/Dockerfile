FROM java:8-alpine
ADD msb-standalone-1.0.0-SNAPSHOT-linux64.tar.gz /opt/application
WORKDIR /opt/application
EXPOSE 10080
ENTRYPOINT exec $PWD/startup4docker.sh
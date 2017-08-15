# Dockerfile - msb-apigateway
FROM msb_base:latest
COPY msb-apigateway*.tar.gz /usr/src

RUN tar -xzf /usr/src/msb-apigateway*.tar.gz -C /usr/local --strip-components=1; \
	rm /usr/src/msb-apigateway*.tar.gz
	
WORKDIR /usr/local
ENTRYPOINT exec $PWD/startup4docker.sh

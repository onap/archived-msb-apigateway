# Dockerfile - msb-apigateway
FROM nexus3.onap.org:10003/onap/msb/msb_base:latest
COPY msb-apigateway*.tar.gz /usr/src

RUN tar -xzf /usr/src/msb-apigateway*.tar.gz -C /usr/local --strip-components=1; \
	rm /usr/src/msb-apigateway*.tar.gz
RUN apk add --no-cache shadow sudo && \
    addgroup -g 1000 msb && \
    adduser -D -u 1000 -G msb msb && \
    echo "msb ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/msb && \
    chmod 0440 /etc/sudoers.d/msb && \
    chown -R msb:msb /usr/local
USER msb
	
WORKDIR /usr/local
ENTRYPOINT exec $PWD/startup4docker.sh

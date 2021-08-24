# Dockerfile - msb-base
FROM openresty/openresty:alpine

# Setup proxy variables
ARG http_proxy
ARG https_proxy

ENV http_proxy  ${http_proxy}
ENV https_proxy ${https_proxy}

#install java-1.8-openjdk

ENV LANG C.UTF-8

# add a simple script that can auto-detect the appropriate JAVA_HOME value
# based on whether the JDK or only the JRE is installed
RUN { \
		echo '#!/bin/sh'; \
		echo 'set -e'; \
		echo; \
		echo 'dirname "$(dirname "$(readlink -f "$(which javac || which java)")")"'; \
	} > /usr/local/bin/docker-java-home \
	&& chmod +x /usr/local/bin/docker-java-home
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk
ENV PATH $PATH:/usr/lib/jvm/java-11-openjdk/jre/bin:/usr/lib/jvm/java-11-openjdk/bin

ENV JAVA_VERSION 11.0.9
ENV JAVA_ALPINE_VERSION 11.0.9_p11-r1

RUN set -x \
	&& apk add --no-cache \
		openjdk8="$JAVA_ALPINE_VERSION" \
	&& [ "$JAVA_HOME" = "$(docker-java-home)" ]
	
#install redis

# add our user and group first to make sure their IDs get assigned consistently, regardless of whatever dependencies get added
RUN addgroup -S redis && adduser -S -G redis redis

# grab su-exec for easy step-down from root
RUN apk add --no-cache 'su-exec>=0.2'

ENV REDIS_VERSION 4.0.1
ENV REDIS_DOWNLOAD_URL http://download.redis.io/releases/redis-4.0.1.tar.gz
ENV REDIS_DOWNLOAD_SHA 2049cd6ae9167f258705081a6ef23bb80b7eff9ff3d0d7481e89510f27457591

# for redis-sentinel see: http://redis.io/topics/sentinel
RUN set -ex; \
	\
	apk add --no-cache --virtual .build-deps \
		coreutils \
		gcc \
		linux-headers \
		make \
		musl-dev \
	; \
	\
	wget -O redis.tar.gz "$REDIS_DOWNLOAD_URL"; \
	echo "$REDIS_DOWNLOAD_SHA *redis.tar.gz" | sha256sum -c -; \
	mkdir -p /usr/src/redis; \
	tar -xzf redis.tar.gz -C /usr/src/redis --strip-components=1; \
	rm redis.tar.gz; \
	\
# disable Redis protected mode [1] as it is unnecessary in context of Docker
# (ports are not automatically exposed when running inside Docker, but rather explicitly by specifying -p / -P)
# [1]: https://github.com/antirez/redis/commit/edd4d555df57dc84265fdfb4ef59a4678832f6da
	grep -q '^#define CONFIG_DEFAULT_PROTECTED_MODE 1$' /usr/src/redis/src/server.h; \
	sed -ri 's!^(#define CONFIG_DEFAULT_PROTECTED_MODE) 1$!\1 0!' /usr/src/redis/src/server.h; \
	grep -q '^#define CONFIG_DEFAULT_PROTECTED_MODE 0$' /usr/src/redis/src/server.h; \
# for future reference, we modify this directly in the source instead of just supplying a default configuration flag because apparently "if you specify any argument to redis-server, [it assumes] you are going to specify everything"
# see also https://github.com/docker-library/redis/issues/4#issuecomment-50780840
# (more exactly, this makes sure the default behavior of "save on SIGTERM" stays functional by default)
	\
	make -C /usr/src/redis -j "$(nproc)"; \
	make -C /usr/src/redis install; \
	\
	rm -r /usr/src/redis; \
	mkdir /usr/local/redis; \
	cd /usr/local/bin; \    
    mv redis-server redis-cli  /usr/local/redis; \
	\
	apk del .build-deps



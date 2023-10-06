FROM alpine

RUN apk update && \
    apk upgrade && \
    apk add openjdk11

RUN apk update && \
    apk upgrade && \
    apk add bash less curl jq

ARG TVD_SSL_PASSWORD=123456
ARG APP_HOME=/opt/app
ARG TVD_KEYSTORE_PATH=${APP_HOME}/.ssh
WORKDIR /
RUN keytool -genkeypair -keystore keystore.p12 -storetype PKCS12 -storepass ${TVD_SSL_PASSWORD} -keyalg RSA -keysize 2048 -validity 99999 -dname "CN=generic_web_server_certificate, OU=tvd, O=anindu, L=Pittsburgh, ST=PA, C=SA"

RUN addgroup gws
RUN adduser gws -G gws -D

RUN chown -R gws keystore.p12
RUN mkdir -p ${TVD_KEYSTORE_PATH}
RUN mv keystore.p12 ${TVD_KEYSTORE_PATH}

RUN mkdir -p ${APP_HOME}
RUN chgrp -R gws ${APP_HOME}
RUN chown -R gws ${APP_HOME}
USER gws

ENV TVD_SSL_PASSWORD=${TVD_SSL_PASSWORD}
ENV TVD_KEYSTORE=${TVD_KEYSTORE_PATH}/keystore.p12

WORKDIR $APP_HOME
COPY build/distributions/*.tar generic_web_server.tar
RUN tar -xvf generic_web_server.tar
RUN rm -rf generic_web_server.tar
RUN mv generic_web_server* generic_web_server

WORKDIR $APP_HOME/generic_web_server
EXPOSE 8383

ENTRYPOINT ['sh', '-c', 'bin/generic_web_server']

FROM arm32v7/alpine:3.18

RUN    apk update \
    && apk add --no-cache bash mariadb mariadb-client mariadb-server-utils pwgen \
    && mkdir /scripts \
    && rm -f /var/cache/apk/*

ADD run.sh /scripts/run.sh
RUN    mkdir /docker-entrypoint-initdb.d \
    && mkdir /scripts/pre-exec.d \
    && mkdir /scripts/pre-init.d \
    && chmod -R 755 /scripts

EXPOSE 3306

VOLUME ["/var/lib/mysql"]

ENTRYPOINT ["/scripts/run.sh"]

CMD ["--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"]

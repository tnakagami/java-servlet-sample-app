FROM arm32v7/maven:3-eclipse-temurin-17

COPY ./execute.sh /execute.sh

RUN    mkdir -p /var/maven/ \
    && useradd usermvn -s /bin/bash -d /var/maven \
    && chown usermvn:usermvn -R /var/maven \
    && chmod 755 /execute.sh

CMD ["/execute.sh"]

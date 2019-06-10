FROM clojure:openjdk-11-lein-2.9.1

RUN apt-get update && \
    apt-get install -y apt-transport-https ca-certificates

RUN curl -sL https://deb.nodesource.com/setup_12.x | bash - && \
    apt-get install -y nodejs

RUN curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - && \
    echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list

RUN apt-get update && \
    apt-get install -y yarn

COPY . /tmp

RUN chmod +x bin/build.sh
RUN ./bin/build.sh

RUN mv target/reverse-venia-0.1.0-SNAPSHOT-standalone.jar /srv/reverse-venia.jar && \
    rm -rf /tmp/*

EXPOSE 80

WORKDIR /srv

CMD ["java", "-jar", "/srv/reverse-venia.jar"]
FROM eclipse-temurin:20

WORKDIR /app

# written with build context = javasrc
ADD write-queue-flush/*.java /app/
ADD lib /app/lib
ADD common /app/common

RUN mkdir -p out

RUN javac -cp "lib/*:." *.java

ENV redis_hostname=write-queue
ENV redis_port=6379
ENV cassandra_hostname="10.128.1.28,10.128.2.28,10.128.3.28,10.128.4.28"
ENV cassandra_port=9042
ENV cassandra_keyspace=url_shortener
ENV cassandra_datacenter=datacenter1

CMD java  -cp "lib/*:." WriteQueueFlush $redis_hostname $redis_port $cassandra_hostname $cassandra_port $cassandra_keyspace $cassandra_datacenter > out/server.log 2> out/error.log
#CMD java  -cp "lib/*:." WriteQueueFlush "write-queue" "6390" "10.128.1.28,10.128.2.28,10.128.3.28,10.128.4.28" "9042" "url_shortener" "datacenter1" > out/server.log 2> out/error.log
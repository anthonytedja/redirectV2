FROM eclipse-temurin:20

WORKDIR /app

# written with build context = javasrc
ADD shortner/*.java /app/
ADD shortner/files /app/files
ADD lib /app/lib
ADD common /app/common

RUN mkdir -p out

RUN javac -cp "lib/*:." *.java

ENV is_verbose=true
ENV server_port=8000
ENV num_threads=4
ENV redis_read_master_hostname=read-cache-master
ENV redis_read_slave_hostname=read-cache-slave
ENV redis_write_queue_hostname=write-queue
ENV redis_read_port=6379
ENV redis_write_port=6379
ENV cassandra_hostname="10.128.1.28,10.128.2.28,10.128.3.28,10.128.4.28"
ENV cassandra_port=9042
ENV cassandra_keyspace=url_shortener
ENV cassandra_datacenter=datacenter1

CMD java -cp "lib/*:." URLShortnerOptimized $is_verbose $server_port $num_threads $redis_read_master_hostname $redis_read_slave_hostname $redis_write_queue_hostname $redis_read_port $redis_write_port $cassandra_hostname $cassandra_port $cassandra_keyspace $cassandra_datacenter > out/server.log 2> out/error.log
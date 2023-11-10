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
ENV redis_hostname=redis
ENV redis_port=6379
ENV cassandra_hostname=cassandra
ENV cassandra_port=9042

CMD java -cp "lib/*:." URLShortnerOptimized $is_verbose $server_port $num_threads $redis_hostname $redis_port $cassandra_hostname $cassandra_port > out/server.log 2> out/error.log

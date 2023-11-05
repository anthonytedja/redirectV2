FROM eclipse-temurin:20

ADD *.java /server/
ADD files /server/files
ADD lib /server/lib

RUN javac -cp "lib/*:." *.java

ENV is_verbose=true
ENV server_port=8000
ENV num_threads=4
ENV redis_hostname=redis
ENV redis_port=6379
ENV cassandra_hostname=cassandra

CMD java -cp "lib/*:." URLShortnerOptimized $is_verbose $server_port $num_threads $redis_hostname $redis_port $cassandra_hostname

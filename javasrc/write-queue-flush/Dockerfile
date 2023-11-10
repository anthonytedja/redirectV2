FROM eclipse-temurin:20

WORKDIR /app

# written with build context = javasrc
ADD write-queue-flush/*.java /app/
ADD lib /app/lib
ADD common /app/common

RUN mkdir -p out

RUN javac -cp "lib/*:." *.java

CMD java  -cp "liv/*:." WriteQueueFlush > out/server.log 2> out/error.log
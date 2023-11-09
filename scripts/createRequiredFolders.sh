#!/bin/bash

for host in $(cat hosts); do
	ssh $host "mkdir -p /home/student/cassandra-data /home/student/redis-data /home/student/server-logs"
done

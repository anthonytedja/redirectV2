#!/bin/bash
# Sets up networks for Cassandra
# Hosts are retrieved from the "hosts" file. The first host listed is the master.
# Call from base directory

cassandra_net=cassandra-net
master=$(head -n 1 ./cassandra/hosts)

for host in $(cat ./cassandra/hosts); do
	ssh $host "docker network create $cassandra_net"
done
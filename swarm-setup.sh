#!/bin/bash
docker swarm init --default-addr-pool-mask-length 16 --force-new-cluster --advertise-addr 10.128.1.28
JOIN_TOKEN=$(docker swarm join-token worker -q)
for worker in '10.128.2.28' '10.128.3.28' '10.128.4.28'; do
    ssh $worker "docker swarm join --token $JOIN_TOKEN 10.128.1.28:2377"
done

echo $JOIN_TOKEN > joinToken
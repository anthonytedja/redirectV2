#!/bin/bash
for worker in '10.128.2.28' '10.128.3.28' '10.128.4.28'; do
    ssh $worker "docker swarm leave --force"
done
docker stack rm csc409a2
docker swarm leave --force
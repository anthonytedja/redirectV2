#!/bin/bash
sudo echo '{"insecure-registries": ["10.128.1.28:5000"]}' > /etc/docker/daemon.json
sudo systemctl restart docker

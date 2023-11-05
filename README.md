# CSC409 A2 - URL Shortener

> A lightweight scalable URL Shortener system

## Table of Contents

- [Running The System](#running-the-system)
  - [Initial Setup](#initial-setup)
  - [Configuration](#configuration)
  - [System Startup](#system-startup)
  - [System Shutdown](#system-shutdown)

On the system, fix the `~/.bashrc` file to include the following:

## Running The System

### Initial Setup

OPTIONAL: For convenience, [passwordless ssh](http://www.linuxproblem.org/art_9.html) can be setup between hosts on the VMs.

Create a `data` directory for each host across all VMs:

```bash
mkdir /home/student/data
```

Build the URL server image:

```bash
docker build -t url-server .
```

### Configuration

TODO Dockerfile and compose.yml

### System Startup

Select a host to act as a manager node and start the swarm. Then ssh into hosts and add them to the swarm as necessary:

```bash
docker swarm init --advertise-addr 10.128.1.28
```

Deploy the stack to run launch all the application in the system:

```bash
docker stack deploy -c compose.yml app
```

### System Shutdown

Remove the deployed stack:

```bash
docker stack rm app
```

Visit all hosts containing worker nodes and remove them from the swarm:

```bash
docker swarm init leave --force
```

Visit the host containing the manager node and remove it from the swarm:

```bash
docker swarm init leave --force
```

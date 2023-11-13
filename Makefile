all: setup build push deploy

# startup
setup-db:
	./cassandra/startCluster

setup:
	./startRegistry.sh
	./swarm-setup.sh

build:
	docker-compose build

push:
	docker-compose push

deploy:
	docker stack deploy -c docker-compose.yml csc409a2

# cleanup
kill:
	./swarm-teardown.sh

kill-db:
	./cassandra/stopCluster

clean:
	./nukeandrestart.sh

# monitor
monitor:
	./cassandra/monitor

node-status:
	docker node ls

stack-status:
	docker stack ps csc409a2
all: setup build push deploy

setup:
	./startRegistry.sh

build:
	docker-compose build

push: build
	docker-compose push

deploy: push
	./swarm-setup.sh

kill:
	./swarm-teardown.sh

node-status:
	docker node ls

stack-status:
	docker stack ps csc409a2
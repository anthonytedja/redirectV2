all: build push deploy

build:
	docker-compose build

push: build
	docker-compose push

deploy: push
	docker stack deploy -c docker-compose.yml csc409a2

kill:
	docker stack rm csc409a2
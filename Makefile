.PHONY: build up

build:
	mkdir -p ./tmp
	(cd tmp && git clone --depth=1 https://github.com/eclipse/lyo.trs-client.git && cd lyo.trs-client && git checkout mqtt && mvn clean install)
	(cd tmp && git clone --depth=1 https://github.com/eclipse/lyo.trs-server.git && cd lyo.trs-server && git checkout mqtt && mvn clean install)
	rm -rf ./tmp
	(cd lyo-webapp-parent && mvn clean package)
	(cd deployment 				&& docker-compose build)

up: build
	(cd deployment 				&& docker-compose up)

up-quick:
	(cd deployment 				&& docker-compose build)
	(cd deployment 				&& docker-compose up)

swarm-restart:
	(cd deployment 				&& docker-compose build)
	(cd deployment 				&& docker swarm init) | true
	(cd deployment 				&& docker stack rm scott) | true
	(cd deployment 				&& docker stack deploy -c docker-compose.yml scott)
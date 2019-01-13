FROM maven:3-jdk-8

RUN git clone --depth=1 --branch=mqtt https://github.com/eclipse/lyo.trs-client.git && cd lyo.trs-client && mvn clean install -Dmaven.test.skip=true -B -q
RUN git clone --depth=1 --branch=mqtt-jaxrs_1.1 https://github.com/eclipse/lyo.trs-server.git && cd lyo.trs-server && mvn clean install -Dmaven.test.skip=true -B -q

WORKDIR /build/app

COPY ./lyo-webapp-parent/pom.xml ./lyo-webapp-parent/pom.xml
COPY ./webapp-twin/pom.xml ./webapp-twin/pom.xml
COPY ./webapp-executor/pom.xml ./webapp-executor/pom.xml
COPY ./webapp-whc/pom.xml ./webapp-whc/pom.xml
COPY ./domain-pddl/pom.xml ./domain-pddl/pom.xml
RUN mvn -f lyo-webapp-parent/pom.xml dependency:resolve -B -q || true
RUN mvn -f lyo-webapp-parent/pom.xml dependency:go-offline -B -q || true

COPY . .
RUN mvn -f lyo-webapp-parent/pom.xml package -B --no-snapshot-updates

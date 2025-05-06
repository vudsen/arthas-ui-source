FROM rastasheep/ubuntu-sshd:18.04

ENV JAVA_HOME /opt/java
ENV JAVA_VERSION "openlogic-openjdk-jre-21.0.6+7-linux-x64"
WORKDIR /opt

#ADD "https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/21.0.7+6/$JAVA_VERSION.tar.gz" /opt
COPY "$JAVA_VERSION.tar.gz" /opt
COPY launch.sh /opt/launch.sh
COPY math-game.jar /opt/math-game.jar

CMD ["sh", "launch.sh"]
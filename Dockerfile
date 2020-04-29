FROM jboss/wildfly:18.0.1.Final

ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

COPY --chown=jboss:jboss wildfly /opt/jboss/wildfly
COPY --chown=jboss:jboss handycrab-ear/target/handycrab-ear-1.0-SNAPSHOT.ear /opt/jboss/wildfly/standalone/deployments/service.ear



CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

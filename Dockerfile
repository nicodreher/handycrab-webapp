FROM jboss/wildfly:18.0.1.Final

RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent

COPY handycrab-ear/target/handycrab-ear-1.0-SNAPSHOT.ear /opt/jboss/wildfly/standalone/deployments/service.ear
COPY wildfly /opt/jboss/wildfly

USER root
RUN chmod -R +x /opt/jboss/wildfly/

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

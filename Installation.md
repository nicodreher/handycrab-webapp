# Installation
Die Webapp und das Backend wurden für den Java-EE Application Server Wildfly erstellt.
Zum Ausführen der Anwendung wird eine MongoDB benötigt.

**Installation MongoDB:** https://docs.mongodb.com/manual/installation/

# Projektstruktur
Das Projekt gliedert sich in vier Module:
* **handycrab-api:** Schnittstellen und Datentypen, die vom Server-Modul verwendet werden
* **handycrab-server:** Enterprise Java-Beans, REST-Services
* **handycrab-webapp:** React App für Browser
* **handycrab-ear:** Finales Enterprise Archive Paket

# Projekt bauen
Gebaut wird das Handycrab Projekt mit maven.\
Im Root-Verzeichnis des Projektes muss dazu folgender Befehl aufgerufen werden:
    mvn -Dmaven.test.skip=true compile package
Nach dem erfolgreichen Build wird im */handycrab-ear/target/* Ordner die EAR *handycrab-ear-1.0-SNAPSHOT.ear* erstellt. Diese kann anschließend in einem Wildfly Server deployed werden.

# Projekt Installation
Die aktuelle Version aus dem Repository wird im Bewertungszeitraum unter https://handycrab.nico-dreher.de/ bereitgesellt.\
REST-Anfragen können mit einem REST-Client an die Basis-URL https://handycrab.nico-dreher.de/rest gestellt werden. Die verfügbaren Services sind in der README.md (https://git.nico-dreher.de/University/mobile-applications/webapp/blob/master/README.md) augelistet

Zur Installation stehen drei Wege zur Verfügung:
* Auf einem Wildfly Server
* Mit Docker
* In einem Kubernetes Cluster

## Wildfly Installation:
* Installation MongoDB: https://docs.mongodb.com/manual/installation/
* Wildlfy Herunterladen: https://wildfly.org/downloads/
* ZIP Entpacken
* Entpackter Ordner wird im Folgenden als *Installationsverzeichnis* bezeichnet
* Den Inhalt des */wildfly* Ordners aus dem Projektverzeichnis in das Installationsverzeichnis einfügen
* MongoDB Treiber herunterladen: https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/3.12.2/mongo-java-driver-3.12.2.jar 
* MongoDB Treiber im Insallationsverzeichnis unter modules/system/layers/base/org/mongodb/main/mongo-java-driver-3.12.2.jar platzieren
* Die gebaute *handycrab-ear-1.0-SNAPSHOT.ear* im Installationsverzeichnis unter /standalone/deployments/ platzieren
* Im System müssen folgende Umgebungsvariablen festgelegt werden
* **mongo_connection** Der MongoDB Connection String: https://docs.mongodb.com/manual/reference/connection-string/
* **mongo_database** Die Datenbank, in der die Collections erstellt werden. (z.B.: Handycrab)
* **pictures_baseurl** Die Basisurl unter der die hochgeladenen Bilder aufgerufen werden können. (z.B.:http://localhost/rest/pictures)
* Nachdem die vorherigen Schritte ausgeführt wurden, kann der Wildfly Server mit einem Standalone Skript aus dem /bin Ordner gestartet werden. (standalone.bat, standalone.sh, standalone.ps)

## Installation mit Docker
* https://docs.docker.com/engine/install/
**MongoDB:**

ToDo Finish!



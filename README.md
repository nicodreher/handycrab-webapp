![HandyCrab- Logo](https://git.nico-dreher.de/University/mobile-applications/webapp/raw/master/handycrab-webapp/src/main/webapp/images/logo/logo.jpeg?inline=false)
# HandyCrab
HandyCrab ist eine mobile Applikation zum Erfassen von Barrieren in der Öffentlichkeit. Mithilfe der App können Menschen mit eingeschränkter Mobilität Barrieren fotografieren und beschreiben. Andere Nutzer können die Barrieren dadurch vorzeitig erkennen und Lösungen beitragen. Durch die vorhandenen Lösungen soll es Menschen mit eingeschränkter Mobilität ermöglicht werden, Barrieren durch die Lösungsvorschlägen effizient zu überwinden. 

Bilder in handycrab-server/test/resources/pictures/images sind von Pixabay.com

## Links zum Projekt
* **Web-Applikation:** https://handycrab.nico-dreher.de/
* **REST-API Basis URL:** https://handycrab.nico-dreher.de/rest/
* **JavaDocs (Backend):** https://handycrab.nico-dreher.de/javadoc/
* **Repository:** https://git.nico-dreher.de/University/mobile-applications/webapp

## Bereitstellung
Die Webapp und das Backend wurden für den Java-EE Application Server Wildfly erstellt. Zur Datenhaltung wird die MongoDB verwendet.

Die aktuelle Version aus dem Repository wird im Bewertungszeitraum unter https://handycrab.nico-dreher.de/ bereitgesellt.\
REST-Anfragen können mit einem REST-Client an die Basis-URL https://handycrab.nico-dreher.de/rest gestellt werden. Die verfügbaren Services sind in der README.md (https://git.nico-dreher.de/University/mobile-applications/webapp/blob/master/README.md) aufgelistet

### Projekt compilieren
Gebaut wird das Handycrab Projekt mit maven.\
Im Root-Verzeichnis des Projektes muss dazu folgender Befehl aufgerufen werden:

    mvn -Dmaven.test.skip=true compile package

Die gebaute App befindet sich anschließend unter */handycrab-ear/target/handycrab-ear-1.0-SNAPSHOT.ear*


## Projektstruktur
Das Projekt gliedert sich in vier Module:
* **handycrab-api:** Schnittstellen und Datentypen, die vom Server-Modul verwendet werden
* **handycrab-server:** Enterprise Java-Beans, REST-Services
* **handycrab-webapp:** React App für Browser
* **handycrab-ear:** Finales Enterprise Archive Paket

## Projektstatus (Backend)
### Must have Anforderungen
| Arbeitspaket | Status |
| --- | --- |
| CI/CD aufsetzen | Abgeschlossen |
| MongoDB | Abgeschlossen |
| MongoDB API | Abgeschlossen |
| Aufsetzung der REST-API | Abgeschlossen |
| Benutzerverwaltung | Abgeschlossen |
| Bilderverwaltung | Abgeschlossen |
| Barrierenverwaltung | Abgeschlossen |
### Should have Anforderungen
| Arbeitspaket | Status |
| --- | --- |
| Registrierung über Google Konto | Offen |
| Erweiterung der Barrier-API | Abgeschlossen |
| Löschungen beantragen | Umgesetzt in separaten Branch - Automatische Tests erfolreich - Manuelle Tests stehen aus |
### Nice to have Anforderungen
| Arbeitspaket | Status |
| --- | --- |
| GeoFencing von Barrieren (Löschungen) | Abgeschlossen |
| Kommentarfunktion | Abgeschlossen |

## Sonstiges
### Löschungen beantragen 
Es ist in dem folgenden Branch umgesetzt:\
https://git.nico-dreher.de/University/mobile-applications/webapp/tree/DeleteBarrier

Wenn ein Nutzer eine fremde Barrier löschen möchte, kann er einen Antrag auf Lösung stellen. Dazu wird einfach ein Upvote an den */barriers/votedelete* Service gesendet. Falls das der erste Vote in den letzten 48 Stunden ist, wird ein neues Voting gestartet. Das Voting geht 48 Stunden und Nutzer können in diesem Zeitraum für eine Löschung Up oder Downvoten. Wenn mindestens 66% der Benutzer für eine Löschung gestimmt haben, wird die Barriere gelöscht. Anderenfalls das Voting beendet und die Stimmen zurückgesetzt.

# REST-API Dokumentation
## Datentypen:

### enum Vote:
0: NONE\
1: UP\
2: DOWN

### User:
_id: ObjectId\
username: string\
email: string

### Solution:
_id: ObjectId\
text: string\
userId: ObjectId\
upvotes: int\
downvotes: int\
vote: Vote

### Barrier:
_id: ObjectId\
userId: ObjectId\
title: string\
longitude: double\
latitude: double\
picturePath: url\
comments: Comment[]\
description: string\
postcode: string\
solutions: Solution[]\
upvotes: int\
downvotes: int\
vote: Vote

### Comment:
comment: String\
userId: ObjectId
## Return-Codes: 
200 - OK (Immer möglich)\
400 - Bad Request (Immer möglich)\
401 - Unauthorized (Immer möglich)\
404 - Not Found (Immer möglich)\
500 - Interner Server Error (Immer möglich)\          
Alles andere muss explizit definiert sein
### Error:
Fehler, die von dem Backend erkannt werden können: 
    
    {"errorCode": <Siehe Fehlertypen>}
Java-Fehler, die vom Backend nicht erwartet und abgefangen werden (z.B.: NullPointerException, IOException): 
    
    {"exception": {"name": "KlassenNamen (z.B.: NullPointerException)", "message": "Java Exception Nachricht"}} 
HTTP-Status Code: 500
### Fehlertypen

| errorCode | Return Code | Text |
--- | --- | ---
1 | 400 | Unvollständige Anfrage (Immer möglich)
2 | 401 | Unauthorized
3 | 400 | E-Mail schon verwendet
4 | 400 | Username schon verwendet
5 | 400 | Ungültige E-Mail
6 | 400 | Ungültiger Login
7 | 404 | User not Found
8 | 400 | Invalid longitude/latitude
9 | 404 | Barrier not Found
10 | 400 | Invalid UserId
11| 404 | Solution not Found
12 | 400 | Invalid Username
13 | 400 | Invalid Password
14 | 400 | Picture To Big
15 | 400 | Invalid Picture Format
16 | 404 | Picture Not Found
17 | 400 | Invalid JSON (Immer möglich)
18 | 400 | Invalid ObjectId
19 | 500 | MongoDB Fehler (Immer möglich)
## REST-Schnittstellen
### Registrierung:
    .../users/register POST
    Login: false
    {email, username, password, createToken?} -> {User}
    SESSION-COOKIE TOKEN-COOKIE
    ErrorCodes: 1, 3, 4, 5, 12, 13
### Login:
    .../users/login POST
    Login: false
    {login(email|username), password, createToken?} -> {User}
    SESSION-COOKIE TOKEN-COOKIE
    ErrorCodes: 1, 6
### CurrentUser:
    .../users/currentuser GET
    Login: true
    {} -> {User}
### Logout:
    .../users/logout POST
    Login: true (Unchecked)
    {} -> EmptyBody
    ErrorCodes: -
### Get-username
    .../users/name GET
    QueryPrams:
        id: ObjectId (Optional)
    Login: false
    {_id} -> {result: string}
    ErrorCodes: 7, 18

### Barriers-Get:
    .../barriers/get GET
    Login: true
    QueryParams (optional):
        longitude: Double, latitude: Double, radius: Integer (m), toDelete: boolean(optional Standard: false) 
        _id: ObjectId 
        postcode: String 
        
    {longitude, latitude, radius (m), toDelete: boolean (optional Standard: false)} -> [Barrier]
    {_id} -> {Barrier}
    {postcode} - [Barrier]
    {} - [Barrier] //Barriers des aktuell angemeldeten Benutzers
    ErrorCodes: 1, 18

### Barrier-Add:
    .../barriers/add POST
    Login: true
    {title, longitude, latitude, picture? (Base64), description?, postcode, solution?} -> {Barrier}
    ErrorCodes: 8, 14, 15

### Barrier-Modify:
    .../barriers/modify PUT
    {_id (BarrierId), title?, picture? (Base64)?, description?} -> {Barrier}
    ErrorCodes: 1, 14, 15, 18
### Barrier-Vote: 
    .../barriers/vote PUT
    {_id (BarrierId), vote: Vote} -> {Barrier}
    ErrorCodes: 9, 18

### Barrier-Solution:
    .../barriers/solution POST
    {_id (BarrierId), solution: Solution} -> {Barrier}
    ErrorCodes: 1, 9, 18

### Barrier-Solution-Vote: 
    .../barriers/solutions/vote PUT
    {_id (SolutionId), vote: Vote} -> {Barrier}
    ErrorCodes: 1, 11, 18
    
### Barrier-delete:
    .../barriers/delete DELETE
    {_id} -> true
    ErrorCodes: 1, 9, 10, 18
    
### Pictures-Get:
    .../pictures/<objectId> GET
    () -> Binary Picture as image/jpeg or image/png
    ErrorCodes: 16, 18
    
### Barrier-comment:
    ../barriers/comment POST
    {_id (BarrierId), comment: String} -> {Barrier}
    ErrorCodes: 1, 9, 18
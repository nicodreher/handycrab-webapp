# HandyCrap
Bilder in handycrab-server/test/resources/pictures/images sind von Pixabay.com
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
delete: boolean\
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
    
### Barrier-markForDelete:
    ../barriers/mark DELETE
    {_id (BarrierId)} -> true if marked
    ErrorCodes: 1, 9, 18
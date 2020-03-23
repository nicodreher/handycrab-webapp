# HandyCrap
## Datentypen:
### enum Vote:
0: NONE\
1: UP\
2: DOWN

### User:
id: uuid\
username: string\
email: string

### Solution:
id: uuid\
text: string\
userId: uuid\
upvotes: int\
downvotes: int\
vote: Vote

### Barrier:
id: uuid\
userId: uuid\
title: string\
longitude: double\
latitude: double\
picture: url\
description: string\
postcode: string\
solution: Solution[]\
upvotes: int\
downvotes: int\
vote: Vote\

## Return-Codes: 
200 - OK (Immer möglich)\
400 - Bad Request (Immer möglich)\
401 - Unauthorized (Immer möglich)\
404 - Not Found (Immer möglich)\
500 - Interner Server Error (Immer möglich)\          
Alles Ander Explizit definiert sein\
### Error:
{errorCode} 1-N\
Definiert für jede Anfrage
## REST-Schnittstellen
### Registrierung:
    .../users/register POST
    Login: false
    {email, username, password} -> {result: User}
    400 - errorCode 1: Unvollständige Anfrage
    400 - errorCode 2: E-Mail schon verwendet
    400 - errorCode 3: Username schon verwendet
    400 - errorCode 4: Ungültige E-Mail
    SESSION-COOKIE
### Login:
    .../users/login POST
    Login: false
    {email|username, password} -> {result: User}
    400 - errorCode 1: Unvollständige Anfrage
    400 - errorCode 2: Ungültiges Login
    SESSION-COOKIE
### Logout:
    .../users/logout POST
    Login: true
    {} -> {}
### Get-username
    .../users/name GET
    Login: false
    {id} -> {result: string}
    400 - errorCode 1: Unvollständige Anfrage
    404 - errorCode 2: User not Found

### Barriers-Get:
    .../barriers/get GET
    Login: true
    {longitude, latitude, radius (m)} -> {result: [Barrier]}
    {id} -> {result: Barrier}
    {postcode} - {result: [Barrier]}
    400 - errorCode 1: Unvollständige Anfrage

### Barrier-Add:
    .../barriers/add POST
    Login: true
    {title, longitude, latitude, picture? (Base64), description?, postcode, solution?} -> {result: barrier}
    400 - errorCode 1: Unvollständige Anfrage
    400 - errorCode 2: Invalid longitude/latitude

### Barrier-Modify:
    .../barriers/modify PUT
    {id (BarrierId), title?, picture? (Base64)?, description?} -> {result: Barrier}
    400 - errorCode 1: Unvollständige Anfrage

### Barrier-Vote: 
    .../barriers/vote PUT
    {id (BarrierId), vote: Vote}
    400 - errorCode 1: Unvollständige Anfrage
    404 - errorCode 2: Barrier not Found

### Barrier-Solution:
    .../barriers/solution POST
    {id (BarrierId), solution: Solution} -> {result: Barrier}
    400 - errorCode 1: Unvollständige Anfrage
    400 - errorCode 2: Invalid UserId
    404 - errorCode 3: Barrier not Found

### Barrier-Solution-Vote: 
    .../barriers/solutions/vote PUT
    {id (SolutionId), vote: Vote}
    400 - errorCode 1: Unvollständige Anfrage
    404 - errorCode 2: Solution not Found
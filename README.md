# HandyCrap
## Datentypen:
### enum Vote:
0: NONE\
1: UP\
2: DOWN

### User:
_id: uuid\
username: string\
email: string

### Solution:
_id: uuid\
text: string\
userId: uuid\
upvotes: int\
downvotes: int\
vote: Vote

### Barrier:
_id: uuid\
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
{errorCode}
### Fehlertypen

| errorCode | Return Code | Text |
--- | --- | ---
1 | 400 | Unvollständige Anfrage
2 | 401 | Unauthorizized
3 | 400 | E-Mail schon verwendet
4 | 400 | Username schon verwendet
5 | 400 | Ungültige E-Mail
6 | 400 | Ungültiger Login
7 | 404 | User not Found
8 | 400 | Invalid longitude/latitude
9 | 404 | Barrier not Found
10 | 400 | Invalid UserId
11| 404 | Solution not Found

## REST-Schnittstellen
### Registrierung:
    .../users/register POST
    Login: false
    {email, username, password} -> {result: User}
    SESSION-COOKIE
    ErrorCodes: 1, 3, 4, 5
### Login:
    .../users/login POST
    Login: false
    {email|username, password} -> {result: User}
    SESSION-COOKIE
    ErrorCodes: 1, 6
### Logout:
    .../users/logout POST
    Login: true
    {} -> {}
    ErrorCodes: -
### Get-username
    .../users/name GET
    Login: false
    {_id} -> {result: string}
    ErrorCodes: 7

### Barriers-Get:
    .../barriers/get GET
    Login: true
    {longitude, latitude, radius (m)} -> {result: [Barrier]}
    {_id} -> {result: Barrier}
    {postcode} - {result: [Barrier]}
    ErrorCodes: 1

### Barrier-Add:
    .../barriers/add POST
    Login: true
    {title, longitude, latitude, picture? (Base64), description?, postcode, solution?} -> {result: barrier}
    ErrorCodes: 8

### Barrier-Modify:
    .../barriers/modify PUT
    {_id (BarrierId), title?, picture? (Base64)?, description?} -> {result: Barrier}
    ErrorCodes: 1
### Barrier-Vote: 
    .../barriers/vote PUT
    {_id (BarrierId), vote: Vote}
    ErrorCodes: 9

### Barrier-Solution:
    .../barriers/solution POST
    {_id (BarrierId), solution: Solution} -> {result: Barrier}
    ErrorCodes: 1, 9, 10

### Barrier-Solution-Vote: 
    .../barriers/solutions/vote PUT
    {_id (SolutionId), vote: Vote}
    ErrorCodes: 1, 11
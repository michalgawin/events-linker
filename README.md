# events-linker
Application finds the closest holidays which occurs in the same time in two countries.

## sample use
GET http://localhost:8080/events?fromDate=2022-09-04&cc1=pl&cc2=at

## requirements
To make it working is required token `eventlinker.security.google.token` which can be set in application.properties and published Google calendars.


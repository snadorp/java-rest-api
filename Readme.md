# REST Transaction API

## Intro
This project is based on Java Play 2.2.6.

Available endpoits:

    GET     /transactionservice/transaction/:id
    PUT     /transactionservice/transaction/:id
    GET     /transactionservice/types/:type
    GET     /transactionservice/sum/:id

## Running
You can run the project via `sbt run`.

## Testing
Unit and integration tests can be triggert via `sbt test`.

## Deploying
To build a production ready jar run `sbt clean:stage`.

## Build Status
[![Build Status](https://travis-ci.org/snadorp/java-rest-api.svg)](https://travis-ci.org/snadorp/java-rest-api)

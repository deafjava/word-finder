# Literature Word Researcher

A REST for Literature Research Service

## Purpose

Show the capabilities of Clojure, with Liquibase. Database with SQL and NoSQL.

## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/) to see the document (still incomplete, TODO)
4. Run your app's tests with `lein test`. Read the tests at test/bras_cubas/service_test.clj. (still incomplete)


## Configuration

1. Have Docker installed
2. Run `docker run --name clojure-literature-mysql -e MYSQL_DATABASE=literature -e MYSQL_ROOT_PASSWORD=d4t4b4s3 -p 3307:3306 -d mysql:5.7.27`
3. Run `docker run --name clojure-dynamodb -p 8000:8000 -d amazon/dynamodb-local`


#!/bin/bash

curl -X POST -H "Content-Type: application/json" \
-d '{"schema": "{\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"favoriteNumber\",\"type\":\"int\"},{\"name\":\"favoriteColor\",\"type\":\"string\"}],\"name\":\"User\",\"namespace\":\"io.confluent.avro\",\"type\":\"record\"}"}' \
"http://localhost:8081/subjects/io.confluent.avro.User/versions"
#!/bin/bash
mvn package && clear && java -jar target/emjetty-examples-servlet-container-1.0.0-SNAPSHOT-jar-with-dependencies.jar

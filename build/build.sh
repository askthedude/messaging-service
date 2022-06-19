#!/usr/bin/env bash

cd ..
./gradlew clean shadowJar
java -Dvertx-config-path=app/src/main/resources/config.json -jar app/build/libs/app-all.jar
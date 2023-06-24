#!/bin/sh
nim changelog/bumpVersion.nims
git add gradle.properties
git commit -m "bump version"
gradle modrinth

patch gradle.properties -i patches/gradle.properties.patch
patch src/main/resources/fabric.mod.json -i patches/fabric.mod.json.patch
patch src/main/java/io/github/offbeat_stuff/zombie_apocalypse/ArmorTrimHander.java -i patches/ArmorTrimHander.java.patch
gradle modrinth
git restore gradle.properties
git restore src/main/resources/fabric.mod.json
git restore src/main/java/io/github/offbeat_stuff/zombie_apocalypse/ArmorTrimHander.java

#!/bin/sh
nim changelog/bumpVersion.nims
git add gradle.properties
git commit -m "bump Version"
gradle modrinth

patch gradle.properties -i patches/gradle.properties.patch
patch src/main/resources/fabric.mod.json -i patches/fabric.mod.json.patch
patch src/main/java/io/github/offbeat_stuff/zombie_apocalypse/ArmorTrimHander.java -i patches/ArmorTrimHander.java.patch
gradle modrinth
patch gradle.properties -i patches/gradle.properties.patch --reverse
patch src/main/resources/fabric.mod.json -i patches/fabric.mod.json.patch --reverse
patch src/main/java/io/github/offbeat_stuff/zombie_apocalypse/ArmorTrimHander.java -i patches/ArmorTrimHander.java.patch --reverse
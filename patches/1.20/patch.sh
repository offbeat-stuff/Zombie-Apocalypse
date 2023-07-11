#!/bin/sh
PATCH_HOME="patches/1.20/"

patch src/main/resources/fabric.mod.json -i $PATCH_HOME/fabric.mod.json.patch
patch src/main/java/io/github/offbeat_stuff/zombie_apocalypse/ArmorTrimHandler.java -i $PATCH_HOME/ArmorTrimHandler.java.patch
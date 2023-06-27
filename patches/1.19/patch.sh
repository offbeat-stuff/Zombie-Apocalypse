#!/bin/sh
rm src/main/java/io/github/offbeat_stuff/zombie_apocalypse/ArmorTrimHandler.java

PATCH_HOME="patches/1.19/"
JAVA_SRC="src/main/java/io/github/offbeat_stuff/zombie_apocalypse"

runPatch () {
    patch $JAVA_SRC/$2/$1 -i $PATCH_HOME/$1.patch
}

patch gradle.properties -i $PATCH_HOME/gradle.properties.patch
patch src/main/resources/fabric.mod.json -i $PATCH_HOME/fabric.mod.json.patch
runPatch PotionEffectHandler.java .
runPatch ArmorHandler.java config
runPatch Common.java config
runPatch Config.java config
runPatch ConfigHandler.java config
runPatch SpawnHandler.java config
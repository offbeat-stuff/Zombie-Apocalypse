rm src/main/java/io/github/offbeat_stuff/zombie_apocalypse/ArmorTrimHandler.java

PATCH_HOME="patches/1.19/"

patch gradle.properties -i $PATCH_HOME/gradle.properties.patch
patch src/main/resources/fabric.mod.json -i $PATCH_HOME/fabric.mod.json.patch
patch src/main/java/io/github/offbeat_stuff/zombie_apocalypse/config/ArmorHandler.java -i $PATCH_HOME/ArmorHandler.java.patch
patch src/main/java/io/github/offbeat_stuff/zombie_apocalypse/config/Config.java -i $PATCH_HOME/Config.java.patch
patch src/main/java/io/github/offbeat_stuff/zombie_apocalypse/config/ConfigHandler.java -i $PATCH_HOME/ConfigHandler.java.patch
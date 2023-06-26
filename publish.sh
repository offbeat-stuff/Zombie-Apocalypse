#!/bin/sh
gradle modrinth

patches/1.19/patch.sh
gradle modrinth
git restore gradle.properties
git restore src/main

patches/1.20/patch.sh
gradle modrinth
git restore gradle.properties
git restore src/main
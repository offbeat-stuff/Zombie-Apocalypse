#!/bin/sh
nim changelog/bumpVersion.nims
git add gradle.properties
git commit -m "bump Version"
gradle modrinth
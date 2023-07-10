#!/bin/sh
gradle $1

runTask () {
    patches/$1/patch.sh
    gradle $2
    git restore gradle.properties
    git restore src/main
}

runTask 1.19 $1
runTask 1.20 $1
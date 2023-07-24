#!/bin/bash
cd src/
find -iname "*.java" | xargs clang-format -i

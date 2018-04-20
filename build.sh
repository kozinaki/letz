#!/bin/bash
mkdir bin
javac src/main/java/net/kozinaki/common/Letz.java -d bin
jar -cfe letz.jar net.kozinaki.common.Letz -C bin .
mkdir build && mv letz.jar build
rm -r bin

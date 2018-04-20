#!/bin/bash
mkdir bin
javac src/main/java/net/kozinaki/common/Letz.java -d bin
jar -cfe letz.jar net.kozinaki.common.Letz -C bin .
if [ ! -d "build" ]; then
	mkdir build 
fi
mv letz.jar build
rm -r bin

#!/bin/bash
mkdir bin
javac src/main/java/net/kozinaki/common/Letz.java -d bin
jar -cfe letz.jar net.kozinaki.common.Letz -C bin .
if [ ! -d "build" ]; then
	mkdir build 
fi
echo -e '#!/bin/bash\njava -jar '$1'letz.jar $1' >> letz.sh
chmod +x letz.sh
mv letz.sh build
mv letz.jar build
if [ $2 ]
    then cp build/letz.sh $2 && cp build/letz.jar $1
fi
rm -r bin

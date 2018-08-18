# letz
Build automation tool for java project. Tool must be launch from root directory of project. 
Project must content property file **xyz.properties**.

Example
```
sources=src/main/java
resources=src/main/resources
class=com.example.HelloWorld
build=build/bin
log=false

```

For build tool
```
./build.sh
```
Also there are two argument keys:  
**~/home/user/myJars/** - where letz.jar could store. This useful for letz.sh script for launching letz.jar.  
**~/home/user/bin/** - where letz.sh could store for launching from anywhere.  
For example
```
./build.sh ~/home/user/myJars/ ~/home/user/bin/
```
Thats work if **/home/user/bin/** or **~/bin/** added to $PATH or another environment path.  

For launch tool
```
java -jar letz.jar
```
or
```
letz.sh
```

After building there is another file - **letz.sh** that contains 

There are three actions - **compile, run, clean**

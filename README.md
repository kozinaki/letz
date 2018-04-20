# letz
Build automation tool for java project. Tool must be launch from root directory of project. 
Project must content ini file **xyz.ini**.

Exmaple
```
sources=src/main/java
resources=src/main/resources
class=com.example.HelloWorld
build=build/bin

```

For build tool
```
./build.sh

```
For launch tool
```
java -jar letz.jar

```
Tool have 3 actions - **compile, run, clean**

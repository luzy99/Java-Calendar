javac -d ./bin -classpath ./src/sqlite-jdbc-3.27.2.1.jar ./src/client/*.java
java  -classpath "./bin;./src/sqlite-jdbc-3.27.2.1.jar" client.MainWindow
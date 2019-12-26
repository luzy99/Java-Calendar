javac -d ./bin -classpath ./src/sqlite-jdbc-3.27.2.1.jar ./src/server/*.java
java -classpath "./bin;./src/sqlite-jdbc-3.27.2.1.jar" server.CalendarServer
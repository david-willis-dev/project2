# PLPproj1
NOTE: Our grammar is named pascal.g4, we were having refactoring issues and couldn't rename it to delphi.g4

Our project is able to handle basic IO, constructors, destructors, objects, and encapsulation.

To run all the test files we created an Interpreter.java file to run through all the pascal files and run them. To compile the code run the following command.

`javac -cp ".:[PATH TO ANTLR JAR]"  --enable-preview --source 23 Interpreter.java
` 

To run the code, use the following command:

`java -cp ".:[PATH TO ANTLR JAR]"  --enable-preview Interpreter`

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class Interpreter {
    public static void main(String[] args) throws IOException {
//        runTest("./test_files/helloworldtest.pas");
//        runTest("./test_files/readAndEcho.pas");
//        runTest("./test_files/classTest.pas");
//        runTest("./test_files/encapsulationTest.pas");
//        runTest("./test_files/forLoop.pas");
//        runTest("./test_files/hardForLoop.pas");
//        runTest("./test_files/whileLoop.pas");
        runTest("./test_files/functionTest.pas");
    }

    public static void runTest(String filePath) throws IOException {
        System.out.println("--------Running test file: " + filePath + "--------");
        CharStream in = CharStreams.fromFileName(filePath);
        delphiLexer lexer = new delphiLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        delphiParser parser = new delphiParser(tokens);
        NewVisitor visitor = new NewVisitor();
        visitor.visit(parser.program());
    }
}

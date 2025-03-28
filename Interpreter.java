import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class Interpreter {
    public static void main(String[] args) throws IOException {
        //runTest("./helloworldtest.pas");
        //runTest("./readAndEcho.pas");
        //runTest("./classTest.pas");
//        runTest("./encapsulationTest.pas");
        runTest("./forLoop.pas");


    }

    public static void runTest(String filePath) throws IOException {
        System.out.println("--------Running test file: " + filePath + "--------");
        CharStream in = CharStreams.fromFileName(filePath);
        pascalLexer lexer = new pascalLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        pascalParser parser = new pascalParser(tokens);
        NewVisitor visitor = new NewVisitor();
        visitor.visit(parser.program());
    }
}

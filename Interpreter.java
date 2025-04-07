import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static java.io.IO.*;

public class Interpreter {
    public static void main(String[] args) throws IOException {
        runTest("./test_files/helloworldtest.pas");
        runTest("./test_files/readAndEcho.pas", "./test_files/readAndEcho.in", "./test_files/readAndEcho.out");
        runTest("./test_files/classTest.pas", "./test_files/classTest.in", "./test_files/classTest.out");
        runTest("./test_files/encapsulationTest.pas");
        runTest("./test_files/forLoop.pas");
        runTest("./test_files/whileLoop.pas", "./test_files/whileLoop.in", "./test_files/whileLoop.out");
        runTest("./test_files/hardForLoop.pas");
        runTest("./test_files/functionTest.pas");
        //manualTest("./test_files/procedureTest.pas");
    }

    public static void runTest(String filePath, String inputPath, String outputPath) throws IOException {
        System.out.println("--------Running test file: " + filePath + "--------");
        CharStream in = CharStreams.fromFileName(filePath);
        delphiLexer lexer = new delphiLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        delphiParser parser = new delphiParser(tokens);
        NewVisitor visitor = new NewVisitor();

        //Get program input
        File inputFile = new File(inputPath);
        Scanner inputScanner = new Scanner(inputFile);
        String input = inputScanner.useDelimiter("\\Z").next();
        inputScanner.close();

        //Get expected output
        File outputFile = new File(outputPath);
        Scanner outputScanner = new Scanner(outputFile);
        String expectedOutput = outputScanner.useDelimiter("\\Z").next();
        outputScanner.close();

        System.out.print("Expected Output: ");
        System.out.println(expectedOutput);

        // Set input for the Pascal program
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        System.out.print("Actual output  : ");

        // Redirect console output to a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream oldOut = System.out;
        System.setOut(ps);

        visitor.visit(parser.program());

        System.out.flush();
        System.setOut(oldOut);

        String actualOutput = baos.toString();
        System.out.print(actualOutput);

        System.setIn(System.in);
    }
    public static void runTest(String filePath) throws IOException {
        System.out.println("--------Running test file: " + filePath + "--------");
        CharStream in = CharStreams.fromFileName(filePath);
        delphiLexer lexer = new delphiLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        delphiParser parser = new delphiParser(tokens);
        NewVisitor visitor = new NewVisitor();
        String expectedOutputFile = filePath.substring(0, filePath.length() - 3) + "out";
        String expectedOutput = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(expectedOutputFile)));
        System.out.print("Actual Output  : ");
        visitor.visit(parser.program());
        System.out.println("Expected Output: " + expectedOutput);
    }

    public static void manualTest(String filePath) throws IOException {
        System.out.println("--------Running test file: " + filePath + "--------");
        CharStream in = CharStreams.fromFileName(filePath);
        delphiLexer lexer = new delphiLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        delphiParser parser = new delphiParser(tokens);
        NewVisitor visitor = new NewVisitor();
        visitor.visit(parser.program());
    }
}

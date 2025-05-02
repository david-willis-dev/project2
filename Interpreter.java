import org.antlr.v4.runtime.*;
import java.io.*;
import java.util.*;

public class Interpreter {
    public static void main(String[] args) throws IOException {
        // working
        runTest("./test_files/helloworldtest.pas", "llvmOut/HelloWorld.ll");
        runTest("./test_files/forLoop.pas", "llvmOut/ForLoop.ll");
        runTest("./test_files/whileLoop.pas", "./test_files/whileLoop.in", "./test_files/whileLoop.out", "llvmOut/While.ll");
        runTest("./test_files/hardForLoop.pas", "llvmOut/continue.ll");
        runTest("./test_files/readAndEcho.pas", "./test_files/readAndEcho.in", "./test_files/readAndEcho.out", "llvmOut/echo.ll");
        runTest("./test_files/functionTest.pas", "llvmOut/function.ll");
        manualTest("./test_files/procedureTest.pas", "llvmOut/procedure.ll");


        checkFiles("./llvmOut/HelloWorld.ll", "./llvmOut/HelloWorld.out");
        checkFiles("./llvmOut/ForLoop.ll", "./llvmOut/ForLoop.out");
        checkFiles("./llvmOut/While.ll", "./llvmOut/While.out");
        checkFiles("./llvmOut/continue.ll", "./llvmOut/continue.out");
        checkFiles("./llvmOut/echo.ll", "./llvmOut/echo.out");
        checkFiles("./llvmOut/procedure.ll", "./llvmOut/procedure.out");
        // not working
//        runTest("./test_files/classTest.pas", "./test_files/classTest.in", "./test_files/classTest.out");
//        runTest("./test_files/encapsulationTest.pas");
    }

    public static void checkFiles(String generatedPath, String expectedPath) throws FileNotFoundException {
        //Get generated output
        File outputFile = new File(generatedPath);
        Scanner outputScanner = new Scanner(outputFile);
        String expectedOutput = outputScanner.useDelimiter("\\Z").next();
        outputScanner.close();

        //Get expected output
        File expectedOutputFile = new File(expectedPath);
        Scanner expectedOutputScanner = new Scanner(expectedOutputFile);
        String generatedOutput = expectedOutputScanner.useDelimiter("\\Z").next();
        expectedOutputScanner.close();

        //Compare
        if (expectedOutput.equals(generatedOutput)) {
            System.out.println("Test for " + generatedPath + " passed!");
        } else {
            System.out.println("Test for " + generatedPath + " failed!");
        }
    }

    public static void runTest(String filePath, String inputPath, String outputPath, String LLVMFile) throws IOException {
        System.out.println("--------Running test file: " + filePath + "--------");
        FileWriter file = new FileWriter(LLVMFile);
        file.write("define dso_local noundef i32 @main() #1 {\n");
        file.close();

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
        visitor.setLLVMFile(LLVMFile);
        visitor.visit(parser.program());

        System.out.flush();
        System.setOut(oldOut);

        String actualOutput = baos.toString();
        System.out.print(actualOutput);

        System.setIn(System.in);

        file = new FileWriter(LLVMFile, true);
        file.write("\n  ret i32 1 \n}\n declare i32 @printf(ptr noundef, ...) #1\nattributes #1 = { \"frame-pointer\"=\"all\" \"no-trapping-math\"=\"true\" \"stack-protector-buffer-size\"=\"8\" \"target-cpu\"=\"x86-64\" \"target-features\"=\"+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87\" \"tune-cpu\"=\"generic\" }");
        file.close();

    }
    public static void runTest(String filePath, String llvmOutput) throws IOException {
        FileWriter file = new FileWriter(llvmOutput);
        file.write("define dso_local noundef i32 @main() #1 {\n");
        file.close();

        System.out.println("--------Running test file: " + filePath + "--------");
        CharStream in = CharStreams.fromFileName(filePath);
        delphiLexer lexer = new delphiLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        delphiParser parser = new delphiParser(tokens);
        NewVisitor visitor = new NewVisitor();
        String expectedOutputFile = filePath.substring(0, filePath.length() - 3) + "out";
        String expectedOutput = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(expectedOutputFile)));
        System.out.print("Actual Output  : ");
        visitor.setLLVMFile(llvmOutput);
        visitor.visit(parser.program());

        file = new FileWriter(llvmOutput, true);
        file.write("\n  ret i32 1 \n}\n declare i32 @printf(ptr noundef, ...) #1\nattributes #1 = { \"frame-pointer\"=\"all\" \"no-trapping-math\"=\"true\" \"stack-protector-buffer-size\"=\"8\" \"target-cpu\"=\"x86-64\" \"target-features\"=\"+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87\" \"tune-cpu\"=\"generic\" }");
        file.close();

        System.out.println("Expected Output: " + expectedOutput);
    }

    public static void manualTest(String filePath, String outputLLVM) throws IOException {
        System.out.println("--------Running test file: " + filePath + "--------");
        FileWriter file = new FileWriter(outputLLVM);
        file.write("define dso_local noundef i32 @main() #1 {\n");
        file.close();

        CharStream in = CharStreams.fromFileName(filePath);
        delphiLexer lexer = new delphiLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        delphiParser parser = new delphiParser(tokens);
        NewVisitor visitor = new NewVisitor();
        visitor.setLLVMFile(outputLLVM);
        visitor.visit(parser.program());

        file = new FileWriter(outputLLVM, true);
        file.write("\n  ret i32 1 \n}\n declare i32 @printf(ptr noundef, ...) #1\nattributes #1 = { \"frame-pointer\"=\"all\" \"no-trapping-math\"=\"true\" \"stack-protector-buffer-size\"=\"8\" \"target-cpu\"=\"x86-64\" \"target-features\"=\"+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87\" \"tune-cpu\"=\"generic\" }");
        file.close();
    }
}

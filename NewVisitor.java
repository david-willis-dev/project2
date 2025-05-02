import java.beans.Expression;
import java.io.*;

import static java.io.IO.*;

import java.util.*;

public class NewVisitor extends delphiBaseVisitor<String> {
    // Basic Vars
    private Map<String, Integer> varTracker = new HashMap<>();
    private boolean continueLoop = false;
    private boolean breakLoop = false;

    // Func Vars
    private Map<String, delphiParser.FormalParameterListContext> funcPars = new HashMap<>();
    private Map<String, delphiParser.BlockContext> funcBlock = new HashMap<>();
    private Map<String, String> funcRet = new HashMap<>();

    // Scope Vars
    private Stack<Map<String, Integer>> scopeStack = new Stack<>();

    // Class Vars
    private String currentClass = "";
    private boolean isPublic = true;
    private FileWriter file;
    private int prtCount = 1;

    private void writeToFile(String writeString) {
        try {
            file = new FileWriter("output.ll", true);
            file.write(writeString);
            file.close();
        } catch (IOException e) {
            System.out.println("COULDN'T OPEN FILE");
        }
    }

    private void prependFile(String writeString) {

        try {
            FileInputStream file = new FileInputStream("output.ll");
            byte[] contents = file.readAllBytes();
            file.close();

            try (FileOutputStream fos = new FileOutputStream("output.ll")) {
                fos.write(writeString.getBytes());
                fos.write(contents);
                prtCount++;
            }

        } catch (IOException e) {
            System.out.println("COULDN'T OPEN FILE");
        }

    }


    @Override
    public String visitProgram(delphiParser.ProgramContext ctx) {
//        println("Visited Program:\n" + ctx.getText() + "\n");
        return this.visitBlock(ctx.block());
    }
//PROGRAMHelloWorld;BEGINWRITELN('Hello, World!');END.<EOF>

    @Override
    public String visitBlock(delphiParser.BlockContext ctx) {
//        println("Visited Block:\n" + ctx.getText() + "\n");
        String str = "";
        for(int i = 0; i < ctx.typeDefinitionPart().size(); i++) {
            str += this.visitTypeDefinitionPart(ctx.typeDefinitionPart().get(i));
        }
        for(int i = 0; i < ctx.classDefinitionPart().size(); i++) {
            str += this.visitClassDefinitionPart(ctx.classDefinitionPart().get(i));
        }
        for(int i = 0; i < ctx.variableDeclarationPart().size(); i++) {
            str += this.visitVariableDeclarationPart(ctx.variableDeclarationPart().get(i));
        }
        for(int i = 0; i < ctx.procedureAndFunctionDeclarationPart().size(); i++) {
            str += this.visitProcedureAndFunctionDeclarationPart(ctx.procedureAndFunctionDeclarationPart().get(i));
        }

        str += this.visitCompoundStatement(ctx.compoundStatement());
        return str;
    }

    @Override
    public String visitVariableDeclarationPart(delphiParser.VariableDeclarationPartContext ctx) {
//        println("Visited VariableDeclarationPart:\n" + ctx.getText() + "\n");
        String str = "";
        for (int i = 0; i < ctx.variableDeclaration().size(); i++) {
            str += this.visitVariableDeclaration(ctx.variableDeclaration().get(i));
        }
        return str;
    }

    @Override
    public String visitVariableDeclaration(delphiParser.VariableDeclarationContext ctx) {
        for (int i = 0; i < ctx.identifierList().identifier().size(); i++) {
            String varName = ctx.identifierList().identifier(i).getText();
//            print("Visited VariableDeclaration, varname: " + varName);
            varTracker.put(varName, Integer.MIN_VALUE); //Default initialization
            writeToFile("%" + varName + " = alloca i32, align 4\n");
        }
        //Note, if a non-variable integer is initialized using visitVariableDeclaration, that will have to be implemented here, for now it only works with ints.
        return ctx.identifierList().identifier(0).getText();
    }


    @Override
    public String visitClassDefinitionPart(delphiParser.ClassDefinitionPartContext ctx) {
        String str = "";
        if (ctx.privateOrPublic(0) != null) {
             isPublic = ctx.privateOrPublic(0).getText().equals("public");
        }
        str += this.visitTypeDefinitionPart(ctx.typeDefinitionPart());
        for(int i = 0; i < ctx.classVariableDefinition().size(); i++) {
            str += this.visitClassVariableDefinition(ctx.classVariableDefinition().get(i));
        }
        for(int i = 0; i < ctx.classProcedureAndFunctionDeclarationPart().size(); i++) {
            str += this.visitClassProcedureAndFunctionDeclarationPart(ctx.classProcedureAndFunctionDeclarationPart().get(i));
        }

        return str;
    }
//    typeExampleClass=classprivatekey:integer;end;
//BEGINWRITELN('Hello, World!');END
    @Override
    public String visitTypeDefinitionPart(delphiParser.TypeDefinitionPartContext ctx) {
        //println("Visited TypeDefinitionPart:\n" + ctx.getText() + "\n");
        String str = "";
        for(int i = 0; i < ctx.typeDefinition().size(); i++) {
            str += this.visitTypeDefinition(ctx.typeDefinition().get(i));
            currentClass = ctx.typeDefinition().get(i).identifier(0).getText();
            //println("Current Class Set as: " + currentClass);
        }
        return str;
    }

    @Override
    public String visitClassVariableDefinition(delphiParser.ClassVariableDefinitionContext ctx) {
        //println("Visited ClassVariableDefinition:\n" + ctx.getText() + "\n");
        String str = "";
        String identifier = ctx.identifier(0).getText();
        if(!currentClass.equals("") ) {
            identifier = currentClass + "::" + identifier;
        }
        if(!isPublic) {
            //println("Put " + identifier + " in varTracker as Private");
            varTracker.put(identifier, Integer.MIN_VALUE);
        }
        return str;
    }

    @Override
    public String visitCompoundStatement(delphiParser.CompoundStatementContext ctx) {
//        println("Visited CompoundStatement:\n" + ctx.getText() + "\n");
        return this.visitStatements(ctx.statements());
    }
    //BEGINWRITELN('Hello, World!');END

    @Override
    public String visitStatements(delphiParser.StatementsContext ctx) {
//        println("Visited Statements:\n" + ctx.getText() + "\n");
        String returnStr = "";
        for (int i = 0; i < ctx.statement().size(); i++) {
            if (ctx.statement() != null) {
//                println("Visited Statement #" + i + ": " + ctx.statement().get(i).getText());
                returnStr += this.visitStatement(ctx.statement().get(i));
                if (continueLoop) {
                    continueLoop = false;
                    break;
                }
            }
        }
        return returnStr;
    }
    //WRITELN('Hello, World!');

    @Override
    public String visitStatement(delphiParser.StatementContext ctx) {
        String text = ctx.getText();
//        println("Visited Statement:\n" + ctx.getText() + "\n");
        return this.visitUnlabelledStatement(ctx.unlabelledStatement());
    }

    //WRITELN('Hello, World!')
    public String visitUnlabelledStatement(delphiParser.UnlabelledStatementContext ctx) {
        //println("Visited UnlabelledStatement:\n" + ctx.getText() + "\n");
        if (ctx.simpleStatement() != null) {
            return this.visitSimpleStatement(ctx.simpleStatement());
        } if (ctx.getText().contains("begin") && ctx.getText().contains("end")) {
            return this.visitStructuredStatement(ctx.structuredStatement());
        } else {
            return this.visitStructuredStatement(ctx.structuredStatement());
        }
    }

    //WRITELN('Hello, World!')
    @Override
    public String visitSimpleStatement(delphiParser.SimpleStatementContext ctx) {
        //println("Visited SimpleStatement: \n" + ctx.getText() + "\n");
        if (ctx.assignmentStatement() instanceof delphiParser.AssignmentStatementContext) {
            return this.visitAssignmentStatement(ctx.assignmentStatement());
        }
        if (ctx.procedureStatement() != null) {
            return this.visitProcedureStatement(ctx.procedureStatement());
        }
        if (ctx.classFuncStatement() != null) {
            return this.visitClassFuncStatement(ctx.classFuncStatement());
        }
        if (ctx.gotoStatement() != null) {
            return this.visitGotoStatement(ctx.gotoStatement());
        }
        if (ctx.emptyStatement_() != null) {
            return this.visitEmptyStatement_(ctx.emptyStatement_());
        }
        return ctx.getText();
    }
//WRITELN('Hello, World!')


    @Override
    public String visitProcedureStatement(delphiParser.ProcedureStatementContext ctx) {
        //println("Visited ProcedureStatement:\n" + ctx.getText() + "\n");
        String command = ctx.identifier().getText().toLowerCase();
        String parameter = "";
        if (ctx.parameterList() != null) {
            parameter = ctx.parameterList().getText().replaceAll("^['\"]|['\"]$", "");
        }
        String strToWrite = "";
        if (command.contains("write")) {

            // Replace variable name with values
            for (Iterator<delphiParser.ActualParameterContext> p = ctx.parameterList().actualParameter().iterator(); p.hasNext();) {
                delphiParser.ActualParameterContext param = p.next();
                String strToPrint = param.getText();

                if (varTracker.containsKey(strToPrint)) {
                    strToWrite += varTracker.get(strToPrint);
                } else {
                    strToWrite += strToPrint.replaceAll("^['\"]|['\"]$", "");
                }
            }

            String writeStr = strToWrite.replaceAll("^['\"]|['\"]$", "");
//            if (varTracker.containsKey(parameter)) {
//                writeStr = varTracker.get(parameter).toString();
//            } else {
//                writeStr = parameter;
//            }


            int strLen = writeStr.length() + 1;
            if (command.contains("ln")) {
                println(writeStr);
                writeStr += "\\0A";
                strLen += 1;
            } else {
                print(writeStr);
            }

            prependFile("@.str" + prtCount + " = private unnamed_addr constant [" + strLen + " x i8] c\"" + writeStr + "\\00\", align 1\n");
            writeToFile("%" + prtCount + " = call i32 (ptr, ...) @printf(ptr noundef @.str" + (prtCount - 1) + ")\n");

        }
        if (command.equalsIgnoreCase("readln") || command.equalsIgnoreCase("read")) {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                varTracker.put(parameter, scanner.nextInt());
            }
        }
        if (command.equalsIgnoreCase("break")) {
            breakLoop = true;
            return "";
        }
        if (command.equalsIgnoreCase("continue")) {
            continueLoop = true;
            return "";
        }
        return strToWrite;
    }

    //WRITELN('Hello, World!')
    @Override
    public String visitParameterList(delphiParser.ParameterListContext ctx) {
        //println("Visited ParameterList:\n" + ctx.getText() + "\n");
        String str = "";
        for (int i = 0; i < ctx.actualParameter().size(); i++) {
            if (ctx.actualParameter() != null) {
                //println("Visited ActualParameter #" + i);
                str += this.visitActualParameter(ctx.actualParameter().get(i));
            }
        }
        return str;
    }

    //'Hello, World!'
    //No need to worry about parameterwidth
    @Override
    public String visitActualParameter(delphiParser.ActualParameterContext ctx) {
        //println("Visited ActualParameter:\n" + ctx.getText() + "\n");
        return this.visitExpression(ctx.expression());
    }

    //'Hello, World!'
    public String visitExpression(delphiParser.ExpressionContext ctx) {
        //println("Visited Expression:\n" + ctx.getText() + "\n");
        return this.visitSimpleExpression(ctx.simpleExpression());
    }

    //'Hello, World!'
    @Override
    public String visitSimpleExpression(delphiParser.SimpleExpressionContext ctx) {
        //println("Visited SimpleExpression: " + ctx.getText() + "\n");
        if (ctx.additiveoperator() != null) {
            return this.preformMath(ctx);
        }

        return this.visitTerm(ctx.term());
    }

    //Visited SimpleExpression: 'Hello, World!'
    @Override
    public String visitTerm(delphiParser.TermContext ctx) {
        //println("Visited Term:" + ctx.getText() + "\n");
        return this.visitSignedFactor(ctx.signedFactor());
    }

    //Visited Term:'Hello, World!'
    @Override
    public String visitSignedFactor(delphiParser.SignedFactorContext ctx) {
        //println("Visited SignedFactor:" + ctx.getText() + "\n");
        return this.visitFactor(ctx.factor());
    }

    //Visited SignedFactor:'Hello, World!'
    @Override
    public String visitFactor(delphiParser.FactorContext ctx) {
//        println("Visited Factor:" + ctx.getText() + "\n");
        //TODO Implement variable, expression, functionDesignator, unsignedConstant, set_, NOT factor, and bool_
        if (ctx.unsignedConstant() != null)
            return this.visitUnsignedConstant(ctx.unsignedConstant());
        if (ctx.variable() != null)
            return this.visitVariable(ctx.variable());
        if (ctx.expression() != null)
            return this.visitExpression(ctx.expression());
        if (ctx.functionDesignator() != null)
            return this.visitFunctionDesignator(ctx.functionDesignator());
        if (ctx.set_() != null)
            return this.visitSet_(ctx.set_());
        if (ctx.bool_() != null)
            return this.visitBool_(ctx.bool_());
        return ctx.getText() + "POSSIBLE ERROR IN visitFactor\n";
    }

    public String visitVariable(delphiParser.VariableContext ctx) {
        //println("Visited Variable:" + ctx.getText() + "\n");
        return ctx.getText();
    }

    //    Visited Factor:'Hello, World!'
    @Override
    public String visitUnsignedConstant(delphiParser.UnsignedConstantContext ctx) {
        //println("Visited UnsignedConstant:" + ctx.getText() + "\n");
        return this.visitString(ctx.string());
    }

    //Visited UnsignedConstant:'Hello, World!'
    @Override
    public String visitString(delphiParser.StringContext ctx) {
        //println("Visited String:" + ctx.getText() + "\n");
        return ctx.getText();
    }


    @Override
    public String visitStructuredStatement(delphiParser.StructuredStatementContext ctx) {
//        println("Visited Structured Statement: " + ctx.getText());

        if (ctx.repetetiveStatement() != null) {
            return this.visitRepetetiveStatement(ctx.repetetiveStatement());
        } else if (ctx.compoundStatement() != null) {
            return this.visitCompoundStatement(ctx.compoundStatement());
        }

        return ctx.getText();
    }

    @Override
    public String visitRepetetiveStatement(delphiParser.RepetetiveStatementContext ctx) {
//        println("Visited RepetetiveStatement: " + ctx.getText());
        if (ctx.forStatement() != null) {
            return this.visitForStatement(ctx.forStatement());
        } else if (ctx.whileStatement() != null) {
            return this.visitWhileStatement(ctx.whileStatement());
        }

        return ctx.getText();
    }

    @Override
    public String visitForStatement(delphiParser.ForStatementContext ctx) {
        String varName = ctx.identifier().getText();
        String forList = ctx.forList().getText();
        int startVal = Integer.parseInt(forList.substring(0, forList.indexOf("to")));
        int endVal = Integer.parseInt(forList.substring(forList.indexOf("to") + 2, forList.length()));
//        println("FOR SATEMENT: " + ctx.DO().getText());

        for (int i = startVal; i <= endVal; i++) {
            varTracker.put(varName, i);
            writeToFile("store i32 " + i + ", ptr %" + varName + ", align 4\n");
            this.visitStatement(ctx.statement());
            if (breakLoop) {
                breakLoop = false;
                break;
            }
        }

        return "";
    }

    @Override
    public String visitWhileStatement(delphiParser.WhileStatementContext ctx) {
        String varName = ctx.expression().simpleExpression().getText();
        String endValue = ctx.expression().expression().simpleExpression().getText();
        String curVal = String.valueOf(varTracker.get(varName));

        while (!Objects.equals(curVal, endValue)) {
            writeToFile("store i32 " + curVal + ", ptr %" + varName + ", align 4\n");
            this.visitStatement(ctx.statement());
            if (breakLoop) {
                breakLoop = false;
                break;
            }
            curVal = String.valueOf(varTracker.get(varName));
        }

        return "";
    }


    @Override
    public String visitAssignmentStatement(delphiParser.AssignmentStatementContext ctx) {
        //println("Visited AssignmentStatement:\n" + ctx.getText() + "\n");
        String identifier = ctx.variable().getText();
        String classLessIdentifier = identifier.substring(identifier.lastIndexOf('.') + 1);

        //println("\tIdentifier = " + identifier + "\n\tValue = " + value + "\n\tClassLessIdentifier = " + classLessIdentifier);
        try {
            if(varTracker.containsKey(currentClass + "::" + classLessIdentifier) && varTracker.get(currentClass + "::" +  classLessIdentifier) == Integer.MIN_VALUE) {
                throw new Exception("Tried to visit a private field");
            }
            if (ctx.expression().getText().matches("-?\\d+")) {
                varTracker.put(ctx.variable().getText(), Integer.parseInt(ctx.expression().getText()));
                writeToFile("store i32 " + ctx.expression().getText() + ", ptr %" + ctx.variable().getText() + ", align 4\n");
            } else if (ctx.expression().getText().contains("+") || ctx.expression().getText().contains("-")) {
                int varVal = Integer.parseInt(this.visitExpression(ctx.expression()));
                varTracker.put(identifier, varVal);
                writeToFile("store i32 " + this.visitExpression(ctx.expression()) + ", ptr %" + ctx.variable().getText() + ", align 4\n");
            } else if (ctx.expression().getText().contains("(") || ctx.expression().getText().contains(")")) {
                varTracker.put(identifier, Integer.parseInt(this.visitExpression(ctx.expression())));
            } else if (varTracker.containsKey(ctx.expression().getText())) {
                varTracker.put(identifier, varTracker.get(ctx.expression().getText()));
                writeToFile("store i32 " + varTracker.get(ctx.expression().getText()) + ", ptr %" + ctx.variable().getText() + ", align 4\n");

            }


            } catch (Exception e) {
            println("Hey! You can't access that variable here. tried to access: " + identifier + " from out of its appropriate context. Encapsulation successfully protected :)");
        }

        return ctx.getText();
    }

    @Override
    public String visitClassFuncStatement(delphiParser.ClassFuncStatementContext ctx) { //TODO: implement
        return ctx.getText();
    }

    @Override
    public String visitGotoStatement(delphiParser.GotoStatementContext ctx) { //TODO: implement got
        return ctx.getText();
    }

    @Override
    public String visitEmptyStatement_(delphiParser.EmptyStatement_Context ctx) { //TODO: implement
        return ctx.getText();
    }

    @Override
    public String visitFunctionDeclaration(delphiParser.FunctionDeclarationContext ctx) {
        String funcName = ctx.identifier().getFirst().getText();
        String returnType = ctx.typeIdentifier().getText();
        delphiParser.BlockContext block = ctx.block();
        delphiParser.FormalParameterListContext formalParameterList = ctx.formalParameterList();

        this.funcRet.put(funcName, returnType);
        this.funcBlock.put(funcName, block);
        this.funcPars.put(funcName, formalParameterList);

        return ctx.getText();
    }

    @Override
    public  String visitFunctionDesignator(delphiParser.FunctionDesignatorContext ctx) {
        String funcName = ctx.identifier().getText();
        enterFunc(funcName, ctx.parameterList());
        this.visitBlock(this.funcBlock.get(funcName));
        String funcVal = String.valueOf(varTracker.get(funcName));
        this.varTracker = this.scopeStack.pop();
        return funcVal;
    }


    void enterFunc(String funcName, delphiParser.ParameterListContext ctx) {
        delphiParser.FormalParameterListContext paramList = this.funcPars.get(funcName);
        List<Integer> arguments = new ArrayList<>();
        List<delphiParser.ActualParameterContext> actualParams = ctx.actualParameter();

        for (delphiParser.ActualParameterContext actualParameterContext : actualParams) {
            int varVal = getVarOrVal(actualParameterContext.expression().simpleExpression().term());
            arguments.add(varVal);
        }

        this.scopeStack.add(this.varTracker);
        this.varTracker = new HashMap<>();
        this.varTracker.put(funcName, Integer.MIN_VALUE);

        int i = 0;
        for (delphiParser.FormalParameterSectionContext paramSec: paramList.formalParameterSection()) {
            for (delphiParser.IdentifierContext paramName: paramSec.parameterGroup().identifierList().identifier()) {
                varTracker.put(paramName.getText(), arguments.get(i));
                i += 1;
            }
        }


    }


    String preformMath(delphiParser.SimpleExpressionContext ctx) {

        int firstVal = getVarOrVal(ctx.term());

        delphiParser.SimpleExpressionContext expr = ctx.simpleExpression();

        int secondVal;

        if (expr.additiveoperator() != null) {
            secondVal = Integer.parseInt(preformMath(expr));
        } else {
            secondVal = getVarOrVal(expr.term());
        }

        int val;
        if (ctx.additiveoperator().getText().equals("+")) {
            val = firstVal + secondVal;
        } else {
            val = firstVal - secondVal;
        }

        // Hack way of doing it but write the evaluated val to a temp var, then overwrite og variable val
        return String.valueOf(val);
    }

    int getVarOrVal(delphiParser.TermContext ctx) {
        delphiParser.FactorContext factor = ctx.signedFactor().factor();
        int val;

        if (factor.variable() != null) {
            String varName = factor.variable().getText();
            val = varTracker.get(varName);
        } else {
            val = Integer.parseInt(factor.unsignedConstant().getText());
        }

        return val;
    }
}

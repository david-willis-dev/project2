import static java.io.IO.*;
import java.util.*;

public class NewVisitor extends pascalBaseVisitor<String> {
    private Map<String, Integer> varTracker = new HashMap<>();
    private boolean isPublic = true;
    private String currentClass = "";

    @Override
    public String visitProgram(pascalParser.ProgramContext ctx) {
        //println("Visited Program:\n" + ctx.getText() + "\n");
        return this.visitBlock(ctx.block());
    }
//PROGRAMHelloWorld;BEGINWRITELN('Hello, World!');END.<EOF>

    @Override
    public String visitBlock(pascalParser.BlockContext ctx) {
        //println("Visited Block:\n" + ctx.getText() + "\n");
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
        str += this.visitCompoundStatement(ctx.compoundStatement());
        return str;
    }

    @Override
    public String visitVariableDeclarationPart(pascalParser.VariableDeclarationPartContext ctx) {
        println("Visited VariableDeclarationPart:\n" + ctx.getText() + "\n");
        String str = "";
        for (int i = 0; i < ctx.variableDeclaration().size(); i++) {
            str += this.visitVariableDeclaration(ctx.variableDeclaration().get(i));
        }
        return str;
    }

    @Override
    public String visitVariableDeclaration(pascalParser.VariableDeclarationContext ctx) {
        print("Visited VariableDeclaration: ");
        String str = ctx.identifierList().identifier(0).getText();
        println(str);
        //Note, if a non-variable integer is initialized using visitVariableDeclaration, that will have to be implemented here, for now it only works with ints.
        varTracker.put(str, Integer.MIN_VALUE); //Default initialization
        return str;
    }


    @Override
    public String visitClassDefinitionPart(pascalParser.ClassDefinitionPartContext ctx) {
        //println("Visited ClassDefinitionPart:\n" + ctx.getText() + "\n");
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
    public String visitTypeDefinitionPart(pascalParser.TypeDefinitionPartContext ctx) {
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
    public String visitClassVariableDefinition(pascalParser.ClassVariableDefinitionContext ctx) {
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
    public String visitCompoundStatement(pascalParser.CompoundStatementContext ctx) {
        //println("Visited CompoundStatement:\n" + ctx.getText() + "\n");
        return this.visitStatements(ctx.statements());
    }
    //BEGINWRITELN('Hello, World!');END

    @Override
    public String visitStatements(pascalParser.StatementsContext ctx) {
        //println("Visited Statements:\n" + ctx.getText() + "\n");
        String returnStr = "";
        for (int i = 0; i < ctx.statement().size(); i++) {
            if (ctx.statement() != null) {
                //println("Visited Statement #" + i + "\n");
                returnStr += this.visitStatement(ctx.statement().get(i));
            }
        }
        return returnStr;
    }
    //WRITELN('Hello, World!');

    @Override
    public String visitStatement(pascalParser.StatementContext ctx) {
        //println("Visited Statement:\n" + ctx.getText() + "\n");
        return this.visitUnlabelledStatement(ctx.unlabelledStatement());
    }

    //WRITELN('Hello, World!')
    public String visitUnlabelledStatement(pascalParser.UnlabelledStatementContext ctx) {
        //println("Visited UnlabelledStatement:\n" + ctx.getText() + "\n");
        if (ctx.simpleStatement() != null) {
            return this.visitSimpleStatement(ctx.simpleStatement());
        }
        return this.visitStructuredStatement(ctx.structuredStatement());
    }

    //WRITELN('Hello, World!')
    @Override
    public String visitSimpleStatement(pascalParser.SimpleStatementContext ctx) {
        //println("Visited SimpleStatement: \n" + ctx.getText() + "\n");
        if (ctx.assignmentStatement() instanceof pascalParser.AssignmentStatementContext) {
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
    public String visitProcedureStatement(pascalParser.ProcedureStatementContext ctx) {
        //println("Visited ProcedureStatement:\n" + ctx.getText() + "\n");
        String command = ctx.identifier().getText().toLowerCase();
        String parameter = ctx.parameterList().getText().replaceAll("^['\"]|['\"]$", "");
        String str = this.visitParameterList(ctx.parameterList());
        if (command.contains("write")) {
            String writeStr;
            if (varTracker.containsKey(parameter)) {
                writeStr = varTracker.get(parameter).toString();
            } else {
                writeStr = parameter;
            }
            if (command.contains("ln")) {
                println(writeStr);
            } else {
                print(writeStr);
            }
        }
        if (command.equalsIgnoreCase("readln") || command.equalsIgnoreCase("read")) {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                varTracker.put(parameter, scanner.nextInt());
            }
        }
        return str;
    }

    //WRITELN('Hello, World!')
    @Override
    public String visitParameterList(pascalParser.ParameterListContext ctx) {
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
    public String visitActualParameter(pascalParser.ActualParameterContext ctx) {
        //println("Visited ActualParameter:\n" + ctx.getText() + "\n");
        return this.visitExpression(ctx.expression());
    }

    //'Hello, World!'
    public String visitExpression(pascalParser.ExpressionContext ctx) {
        //println("Visited Expression:\n" + ctx.getText() + "\n");
        return this.visitSimpleExpression(ctx.simpleExpression());
    }

    //'Hello, World!'
    @Override
    public String visitSimpleExpression(pascalParser.SimpleExpressionContext ctx) {
        //println("Visited SimpleExpression: " + ctx.getText() + "\n");
        return this.visitTerm(ctx.term());
    }

    //Visited SimpleExpression: 'Hello, World!'
    @Override
    public String visitTerm(pascalParser.TermContext ctx) {
        //println("Visited Term:" + ctx.getText() + "\n");
        return this.visitSignedFactor(ctx.signedFactor());
    }

    //Visited Term:'Hello, World!'
    @Override
    public String visitSignedFactor(pascalParser.SignedFactorContext ctx) {
        //println("Visited SignedFactor:" + ctx.getText() + "\n");
        return this.visitFactor(ctx.factor());
    }

    //Visited SignedFactor:'Hello, World!'
    @Override
    public String visitFactor(pascalParser.FactorContext ctx) {
        //println("Visited Factor:" + ctx.getText() + "\n");
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

    public String visitVariable(pascalParser.VariableContext ctx) {
        //println("Visited Variable:" + ctx.getText() + "\n");
        return ctx.getText();
    }

    //    Visited Factor:'Hello, World!'
    @Override
    public String visitUnsignedConstant(pascalParser.UnsignedConstantContext ctx) {
        //println("Visited UnsignedConstant:" + ctx.getText() + "\n");
        return this.visitString(ctx.string());
    }

    //Visited UnsignedConstant:'Hello, World!'
    @Override
    public String visitString(pascalParser.StringContext ctx) {
        //println("Visited String:" + ctx.getText() + "\n");
        return ctx.getText();
    }


    @Override
    public String visitStructuredStatement(pascalParser.StructuredStatementContext ctx) {
        println("Visited Structured Statement: " + ctx.getText());
        return this.visitRepetetiveStatement(ctx.repetetiveStatement());
    }

    @Override
    public String visitRepetetiveStatement(pascalParser.RepetetiveStatementContext ctx) {
        println("Visited RepetetiveStatement: " + ctx.getText());
        return this.visitForStatement(ctx.forStatement());
    }

    @Override
    public String visitForStatement(pascalParser.ForStatementContext ctx) {
//        Implement Tomorrow
        return "";
    }


    @Override
    public String visitAssignmentStatement(pascalParser.AssignmentStatementContext ctx) {
        //println("Visited AssignmentStatement:\n" + ctx.getText() + "\n");
        String identifier = ctx.variable().getText();
        String classLessIdentifier = identifier.substring(identifier.lastIndexOf('.') + 1);
        String value = ctx.expression().getText();
        //println("\tIdentifier = " + identifier + "\n\tValue = " + value + "\n\tClassLessIdentifier = " + classLessIdentifier);
        try {
            if(varTracker.containsKey(currentClass + "::" + classLessIdentifier) && varTracker.get(currentClass + "::" +  classLessIdentifier) == Integer.MIN_VALUE) {
                throw new Exception("Tried to visit a private field");
            }
            if(ctx.expression().getText().matches("-?\\d+")) {
                varTracker.put(ctx.variable().getText(), Integer.parseInt(ctx.expression().getText()));
            }

            } catch (Exception e) {
            println("Hey! You can't access that variable here. tried to access: " + identifier + " from out of its appropriate context. Encapsulation successfully protected :)");
        }

        return ctx.getText();
    }

    @Override
    public String visitClassFuncStatement(pascalParser.ClassFuncStatementContext ctx) { //TODO: implement
        return ctx.getText();
    }

    @Override
    public String visitGotoStatement(pascalParser.GotoStatementContext ctx) { //TODO: implement got
        return ctx.getText();
    }

    @Override
    public String visitEmptyStatement_(pascalParser.EmptyStatement_Context ctx) { //TODO: implement
        return ctx.getText();
    }
}

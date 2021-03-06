/*
  The statementLanguage programming language
  Copyright 2016 Eric J. Deiman

  This file is part of the statementLanguage programming language.
  The statementLanguage programming language is free software: you can redistribute it
  and/ormodify it under the terms of the GNU General Public License as published by the
  Free Software Foundation, either version 3 of the License, or (at your option) any
  later version.
  
  The statementLanguage programming language is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with the
  statementLanguage programming language. If not, see <https://www.gnu.org/licenses/>
*/

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import parser.*;
import common.RuntimeError;
import common.RuntimeType;

public class StmntInterpreter extends StmntBaseVisitor<InterpValue> {

    public Environment environment = new Environment();
    public Map<String, FuncData> functionNameSpace = new HashMap<String, FuncData>();

    @Override
    public InterpValue visitProg(StmntParser.ProgContext ctx) {
        InterpValue answer = iIntergerZero;

        for(StmntParser.FuncDeclContext fctx : ctx.funcDecl()) {
            visit(fctx);
        }

        for(StmntParser.StatementContext sctx : ctx.statement()) {
            answer = visit(sctx);
        }

        return answer;
    }

    @Override
    public InterpValue visitFuncDecl(StmntParser.FuncDeclContext ctx) {
        FuncData func = new FuncData(ctx.ID(), ctx.block());
        if(functionNameSpace.containsKey(func.getInternalName())) {
            throw new RuntimeError("attempt to redefine function " + func.getName() +
                                   " near " + ctx.getStart().getLine() + ":" +
                                   ctx.getStart().getCharPositionInLine());
        }
        functionNameSpace.put(func.getInternalName(), func);

        return iStringNull;
    }

    @Override
    public InterpValue visitPrintStmnt(StmntParser.PrintStmntContext ctx) {
        InterpValue answer = iStringNull;

        for(StmntParser.ExpressionContext ectx : ctx.expression()) {
            answer = visit(ectx);
            if(answer == null) {
                answer = iStringNull;
            }
            System.out.print(answer.toString());
            System.out.print(" ");
        }

        System.out.println();

        return answer;
    }

    @Override
    public InterpValue visitAssign(StmntParser.AssignContext ctx) {
        InterpValue value = visit(ctx.expression());
        String name = ctx.ID().getText();

        environment.put(name, value);

        return value;
    }

    @Override
    public InterpValue visitIfStmnt(StmntParser.IfStmntContext ctx) {
        InterpValue answer = iIntergerZero;
        List<StmntParser.IfBlockContext> conditions = ctx.ifBlock();

        Boolean evaluated = false;

        for(StmntParser.IfBlockContext ifBlock : conditions) {
            InterpValue result = visit(ifBlock.test);
            expectType(RuntimeType.iBoolean, result, ctx.getStart());
            if((Boolean)result.getValue()) {
                evaluated = true;
                answer = visit(ifBlock.body);
                break;
            }
        }

        if(!evaluated && ctx.block() != null) {
            answer = visit(ctx.block());
        }

        return answer;
    }

    @Override
    public InterpValue visitWhileStmnt(StmntParser.WhileStmntContext ctx) {
        InterpValue answer = iIntergerZero;

        while(true) {
            InterpValue result = visit(ctx.test);
            expectType(RuntimeType.iBoolean, result, ctx.getStart());
            if((Boolean)result.getValue()) {
                answer = visit(ctx.body);
            }
            else {
                break;
            }
        }

        return answer;
    }

    @Override
    public InterpValue visitReturnStmnt(StmntParser.ReturnStmntContext ctx) {
        InterpValue answer = visit(ctx.expression());
        seenReturn = true;
        answer.setFromReturn(true);
        return answer;
    }

    @Override
    public InterpValue visitBlock(StmntParser.BlockContext ctx) {
        InterpValue answer = iIntergerZero;
        environment.beginScope();
        for(StmntParser.StatementContext sctx : ctx.statement()) {
            answer = visit(sctx);
            if(answer.getFromReturn()) {
                break;
            }
        }
        environment.endScope();
        return answer;
    }

    @Override
    public InterpValue visitFuncCall(StmntParser.FuncCallContext ctx) {
        String name = ctx.ID().getText();
        List<InterpValue> arguments = new Vector<InterpValue>();
        for(StmntParser.ExpressionContext exp : ctx.expression()) {
            arguments.add(visit(exp));
        }

        String internalName = String.format("%s\\%d", name, arguments.size());
        if(!functionNameSpace.containsKey(internalName)) {
            throw new RuntimeError("cannot find function named " + name);
        }

        FuncData fun = functionNameSpace.get(internalName);


        List<String> params = fun.getParameters();

        if(arguments.size() != params.size()) {
            Integer line = ctx.getStart().getLine();
            Integer pos = ctx.getStart().getCharPositionInLine();
            throw new RuntimeError("attempt to call " + name + " with wrong parameter" +
                                   " count at " + line + ":" + pos +
                                   ".  Expected " + params.size() + " got " +
                                   arguments.size());
        }

        InterpValue answer = iStringNull;

        environment.beginScope();

        for(int i = 0; i < params.size(); i++) {
            environment.putShadow(params.get(i), arguments.get(i));
        }

        seenReturn = false;
        answer = visit(fun.getBody());

        if(!seenReturn) {
            throw new RuntimeError("function " + name + " has no return statement");
        }

        seenReturn = false;

        environment.endScope();

        return answer;
    }

    @Override
    public InterpValue visitArithGroup(StmntParser.ArithGroupContext ctx) {
        return visit(ctx.arithExp());
    }

    @Override
    public InterpValue visitPower(StmntParser.PowerContext ctx) {
        InterpValue left = visit(ctx.left);
        InterpValue right = visit(ctx.right);
        expectTypes(RuntimeType.iInteger, left, right, ctx.getStart());
        return left.doMath("^", right);
    }

    @Override
    public InterpValue visitMult(StmntParser.MultContext ctx) {
        InterpValue left = visit(ctx.left);
        InterpValue right = visit(ctx.right);
        String op = ctx.op.getText();
        expectTypes(RuntimeType.iInteger, left, right, ctx.getStart());
        return left.doMath(op, right);
    }

    @Override
    public InterpValue visitAdd(StmntParser.AddContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        String op = ctx.op.getText();
        expectTypes(RuntimeType.iInteger, ileft, iright, ctx.getStart());
        return ileft.doMath(op, iright);
    }

    @Override
    public InterpValue visitNumber(StmntParser.NumberContext ctx) {
        Integer answer = Integer.valueOf(ctx.NUMBER().getText().replace("_", ""));
        return new InterpValue(RuntimeType.iInteger, answer);
    }

    @Override
    public InterpValue visitId(StmntParser.IdContext ctx) {
        String name = ctx.ID().getText();
        return environment.get(name);
    }

    @Override
    public InterpValue visitStringExp(StmntParser.StringExpContext ctx)  {
        String value = ctx.STRING().getText();
        return new InterpValue(RuntimeType.iString, value);
    }

    @Override
    public InterpValue visitLogicNot(StmntParser.LogicNotContext ctx) {
        InterpValue result = visit(ctx.logicExp());
        expectType(RuntimeType.iBoolean, result, ctx.getStart());
        return result.doLogic("not", null);
    }

    @Override
    public InterpValue visitLogicAnd(StmntParser.LogicAndContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        expectTypes(RuntimeType.iBoolean, ileft, iright, ctx.getStart());
        return ileft.doLogic("and", iright);
    }

    @Override
    public InterpValue visitLogicOr(StmntParser.LogicOrContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        expectTypes(RuntimeType.iBoolean, ileft, iright, ctx.getStart());
        return ileft.doLogic("or", iright);
    }

    @Override
    public InterpValue visitLogicId(StmntParser.LogicIdContext ctx) {
        String name = ctx.ID().getText();
        return environment.get(name);
    }

    @Override
    public InterpValue visitLitTrue(StmntParser.LitTrueContext ctx) {
        return new InterpValue(RuntimeType.iBoolean, true);
    }

    @Override
    public InterpValue visitLitFalse(StmntParser.LitFalseContext ctx) {
        return new InterpValue(RuntimeType.iBoolean, false);
    }

    @Override
    public InterpValue visitIntRelExp(StmntParser.IntRelExpContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        String op = ctx.op.getText();

        if(ileft.getType() == RuntimeType.iString &&
           iright.getType() == RuntimeType.iString ) {
            return ileft.doStringRel(op, iright);
        }

        expectTypes(RuntimeType.iInteger, ileft, iright, ctx.getStart());
        return ileft.doIntRel(op, iright);
    }

    @Override
    public InterpValue visitStringRelExp(StmntParser.StringRelExpContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        String op = ctx.op.getText();
        expectTypes(RuntimeType.iString, ileft, iright, ctx.getStart());
        return ileft.doStringRel(op, iright);
    }

    //----------------------------------------------------------------------------

    private RuntimeError runtimeError(Token token, String message) {
        return new RuntimeError("runtime error near " +
                         token.getLine() + ":" +
                         (token.getCharPositionInLine()) +
                         "  " + message);
    }

    private Boolean expectTypes(RuntimeType type, InterpValue left, InterpValue right,
                                Token where) {
        RuntimeType leftType = left.getType();
        RuntimeType rightType = right.getType();

        if(leftType != type || rightType != type) {
            String message = "expected 2 values of " + makeRuntimeTypeString(type) +
                ", but got " + makeTypeMismatchMessage(left, right);
            throw runtimeError(where, message);
        }

        return true;
    }

    private Boolean expectType(RuntimeType type, InterpValue left, Token where) {
        RuntimeType leftType = left.getType();

        if(leftType != type) {
            String message = "expected value of type " + makeRuntimeTypeString(type) +
                ", but got " + makeRuntimeTypeString(leftType);
            throw runtimeError(where, message);
        }

        return true;
    }

    private String makeRuntimeTypeString(RuntimeType iType) {
        switch(iType) {
        case iInteger:
            return "integer";

        case iString:
            return "string";

        case iBoolean:
            return "boolean";
        }

        return "";
    }

    private String makeTypeMismatchMessage(InterpValue left, InterpValue right) {
        String leftType = makeRuntimeTypeString(left.getType());
        String rightType = makeRuntimeTypeString(right.getType());

        return leftType + " " + rightType;
    }

    private InterpValue iIntergerZero = new InterpValue(RuntimeType.iInteger, 0);
    private InterpValue iStringNull = new InterpValue(RuntimeType.iString, "\"<null>\"");
    private Boolean seenReturn = false;
}

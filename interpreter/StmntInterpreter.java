import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import parser.*;

public class StmntInterpreter extends StmntBaseVisitor<InterpValue> {

    public Map<String, InterpValue> environment = new HashMap<String,InterpValue>();

    @Override
    public InterpValue visitProg(StmntParser.ProgContext ctx) {
        InterpValue answer = new InterpValue(InterpType.iInteger, 0);
        for(StmntParser.StatementContext sctx : ctx.statement()) {
            answer = visit(sctx);
        }

        return answer;
    }

    @Override
    public InterpValue visitPrintExp(StmntParser.PrintExpContext ctx) {
        InterpValue answer = null;

        for(StmntParser.ExpressionContext ectx : ctx.expression()) {
            answer = visit(ectx);
            System.out.print(answer.toString());
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
        InterpValue answer = null;
        List<StmntParser.IfBlockContext> conditions = ctx.ifBlock();

        Boolean evaluated = false;

        for(StmntParser.IfBlockContext ifBlock : conditions) {
            InterpValue result = visit(ifBlock.test);
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
    public InterpValue visitBlock(StmntParser.BlockContext ctx) {
        InterpValue answer = new InterpValue(InterpType.iInteger, 0);
        for(StmntParser.StatementContext sctx : ctx.statement()) {
            answer = visit(sctx);
        }

        return answer;
    }
        
    @Override
    public InterpValue visitArithE(StmntParser.ArithEContext ctx) {
        return visit(ctx.arithExp());
    }

    @Override
    public InterpValue visitStringE(StmntParser.StringEContext ctx) {
        return visit(ctx.stringExp());
    }

    @Override
    public InterpValue visitLogicE(StmntParser.LogicEContext ctx) {
        return visit(ctx.logicExp());
    }

    @Override
    public InterpValue visitArithGroup(StmntParser.ArithGroupContext ctx) {
        return visit(ctx.arithExp());
    }

    @Override
    public InterpValue visitPower(StmntParser.PowerContext ctx) {
        InterpValue left = visit(ctx.left);
        InterpValue right = visit(ctx.right);
        try {
            expectTypes(InterpType.iInteger, left, right);
        }
        catch(Error er) {
            throw runtimeError(ctx.getStart(), er.getMessage());
        }
        return left.doMath("^", right);
    }

    @Override
    public InterpValue visitMult(StmntParser.MultContext ctx) {
        InterpValue left = visit(ctx.left);
        InterpValue right = visit(ctx.right);
        String op = ctx.op.getText();
        try {
            expectTypes(InterpType.iInteger, left, right);
        }
        catch(Error er) {
            throw runtimeError(ctx.getStart(), er.getMessage());
        }
        return left.doMath(op, right);
    }

    @Override
    public InterpValue visitAdd(StmntParser.AddContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        String op = ctx.op.getText();
        try {
            expectTypes(InterpType.iInteger, ileft, iright);
        }
        catch(Error er) {
            throw runtimeError(ctx.getStart(), er.getMessage());
        }
        return ileft.doMath(op, iright);
    }

    @Override
    public InterpValue visitNumber(StmntParser.NumberContext ctx) {
        Integer answer = Integer.valueOf(ctx.NUMBER().getText());
        return new InterpValue(InterpType.iInteger, answer);
    }

    @Override
    public InterpValue visitId(StmntParser.IdContext ctx) {
        String name = ctx.ID().getText();
        return environment.get(name);
    }

    @Override
    public InterpValue visitStringExp(StmntParser.StringExpContext ctx)  {
        String value = ctx.STRING().getText();
        return new InterpValue(InterpType.iString, value);
    }

    @Override
    public InterpValue visitLogicGroup(StmntParser.LogicGroupContext ctx ) {
        return visit(ctx.logicExp());
    }

    @Override
    public InterpValue visitLogicNot(StmntParser.LogicNotContext ctx) {
        InterpValue result = visit(ctx.logicExp());
        try {
            expectType(InterpType.iBoolean, result);
        }
        catch(Error er) {
            throw runtimeError(ctx.getStart(), er.getMessage());
        }
        return result.doLogic("not", null);
    }

    @Override
    public InterpValue visitLogicAnd(StmntParser.LogicAndContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        try {
            expectTypes(InterpType.iBoolean, ileft, iright);
        }
        catch(Error er) {
            throw runtimeError(ctx.getStart(), er.getMessage());
        }
        return ileft.doLogic("and", iright);
    }

    @Override
    public InterpValue visitLogicOr(StmntParser.LogicOrContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        try {
            expectTypes(InterpType.iBoolean, ileft, iright);
        }
        catch(Error er) {
            throw runtimeError(ctx.getStart(), er.getMessage());
        }
        return ileft.doLogic("or", iright);
    }

    @Override
    public InterpValue visitLogicIntRel(StmntParser.LogicIntRelContext ctx) {
        return visit(ctx.intRelExp());
    }

    @Override
    public InterpValue visitLogicStringRel(StmntParser.LogicStringRelContext ctx) {
        return visit(ctx.stringRelExp());
    }

    @Override
    public InterpValue visitLogicLit(StmntParser.LogicLitContext ctx) {
        return visit(ctx.boolLit());
    }

    @Override
    public InterpValue visitLogicId(StmntParser.LogicIdContext ctx) {
        String name = ctx.ID().getText();
        return environment.get(name);
    }

    @Override
    public InterpValue visitLitTrue(StmntParser.LitTrueContext ctx) {
        return new InterpValue(InterpType.iBoolean, true);
    }

    @Override
    public InterpValue visitLitFalse(StmntParser.LitFalseContext ctx) {
        return new InterpValue(InterpType.iBoolean, false);
    }

    @Override
    public InterpValue visitIntRelExp(StmntParser.IntRelExpContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        String op = ctx.op.getText();

        if(ileft.getType() == InterpType.iString &&
           iright.getType() == InterpType.iString ) {
            return ileft.doStringRel(op, iright);
        }

        try {
            expectTypes(InterpType.iInteger, ileft, iright);
        }
        catch(Error er) {
            throw runtimeError(ctx.getStart(), er.getMessage());
        }
        return ileft.doIntRel(op, iright);
    }

    @Override
    public InterpValue visitStringRelExp(StmntParser.StringRelExpContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);
        String op = ctx.op.getText();
        try {
            expectTypes(InterpType.iString, ileft, iright);
        }
        catch(Error er) {
            throw runtimeError(ctx.getStart(), er.getMessage());
        }
        return ileft.doStringRel(op, iright);
    }

    //----------------------------------------------------------------------------

    private Error runtimeError(Token token, String message) {
        return new Error("runtime error near " +
                         token.getLine() + ":" +
                         (token.getCharPositionInLine() + 1 ) +
                         "  " + message);
    }

    private Boolean expectTypes(InterpType type, InterpValue left, InterpValue right) {
        InterpType leftType = left.getType();
        InterpType rightType = right.getType();

        if(leftType != type || rightType != type) {
            String message = "expected 2 values of " + makeInterpTypeString(type) +
                ", but got " + makeTypeMismatchMessage(left, right);
            throw new Error(message);
        }

        return true;
    }

    private Boolean expectType(InterpType type, InterpValue left) {
        InterpType leftType = left.getType();

        if(leftType != type) {
            String message = "expected value of type " + makeInterpTypeString(type) +
                ", but got " + makeInterpTypeString(leftType);
            throw new Error(message);
        }

        return true;
    }

    private String makeInterpTypeString(InterpType iType) {
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
        String leftType = makeInterpTypeString(left.getType());
        String rightType = makeInterpTypeString(right.getType());

        return leftType + " " + rightType;
    }
}

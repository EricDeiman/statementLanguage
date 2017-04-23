import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;

import common.ByteCodes;
import common.CodeBuffer;
import common.RuntimeType;

import parser.*;

public class Compile extends StmntBaseVisitor<Integer> {
    public Compile() {
        code = new CodeBuffer();
    }

    @Override
    public Integer visitProg(StmntParser.ProgContext ctx) {
        Integer answer = 0;
        for(StmntParser.StatementContext sctx : ctx.statement()) {
            answer = visit(sctx);
        }
        code.writeByte(ByteCodes.Halt.ordinal());
        return answer;
    }

    @Override
    public Integer visitPrintStmnt(StmntParser.PrintStmntContext ctx) {
        Integer answer = 0;

        for(StmntParser.ExpressionContext ectx : ctx.expression()) {
            answer = visit(ectx);
        }

        code.writeByte(ByteCodes.Print.ordinal());

        return answer;
    }

    @Override
    public Integer visitAssign(StmntParser.AssignContext ctx) {
        Integer value = visit(ctx.expression());
        String name = ctx.ID().getText();

        return value;
    }

    @Override
    public Integer visitIfStmnt(StmntParser.IfStmntContext ctx) {
        Integer answer = 0;
        List<StmntParser.IfBlockContext> conditions = ctx.ifBlock();

        for(StmntParser.IfBlockContext ifBlock : conditions) {
            Integer result = visit(ifBlock.test);
                answer = visit(ifBlock.body);
            }

        if(ctx.block() != null) {
            answer = visit(ctx.block());
        }

        return answer;
    }

    @Override
    public Integer visitWhileStmnt(StmntParser.WhileStmntContext ctx) {
        Integer answer = 0;

        Integer result = visit(ctx.test);
        answer = visit(ctx.body);

        return answer;
    }

    @Override
    public Integer visitBlock(StmntParser.BlockContext ctx) {
        Integer answer = 0;
        //environment.beginScope();
        for(StmntParser.StatementContext sctx : ctx.statement()) {
            answer = visit(sctx);
        }
        //environment.endScope();
        return answer;
    }

    @Override
    public Integer visitArithE(StmntParser.ArithEContext ctx) {
        return visit(ctx.arithExp());
    }

    @Override
    public Integer visitStringE(StmntParser.StringEContext ctx) {
        return visit(ctx.stringExp());
    }

    @Override
    public Integer visitLogicE(StmntParser.LogicEContext ctx) {
        return visit(ctx.logicExp());
    }

    @Override
    public Integer visitArithGroup(StmntParser.ArithGroupContext ctx) {
        return visit(ctx.arithExp());
    }

    @Override
    public Integer visitPower(StmntParser.PowerContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        code.writeByte(ByteCodes.Pow.ordinal());
        return 0;
    }

    @Override
    public Integer visitMult(StmntParser.MultContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        String op = ctx.op.getText();
        if(op.equals("*")) {
            code.writeByte(ByteCodes.Mul.ordinal());
        }
        else if(op.equals("div")) {
            code.writeByte(ByteCodes.Div.ordinal());
        }
        else {
            code.writeByte(ByteCodes.Rem.ordinal());
        }

        return 0;
    }

    @Override
    public Integer visitAdd(StmntParser.AddContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        String op = ctx.op.getText();
        if(op.equals("+")) {
            code.writeByte(ByteCodes.Add.ordinal());
        }
        else {
            code.writeByte(ByteCodes.Sub.ordinal());
        }
        return 0;
    }

    @Override
    public Integer visitNumber(StmntParser.NumberContext ctx) {
        Integer answer = Integer.valueOf(ctx.NUMBER().getText());
        code.writeByte(ByteCodes.Push.ordinal()).writeByte(RuntimeType.iInteger.ordinal())
            .writeInteger(answer);
        return answer;
    }

    // @Override
    // public Integer visitId(StmntParser.IdContext ctx) {
    //     String name = ctx.ID().getText();
    //     return environment.get(name);
    // }

    // @Override
    // public Integer visitStringExp(StmntParser.StringExpContext ctx)  {
    //     String value = ctx.STRING().getText();
    //     return new Integer(InterpType.iString, value);
    // }

    @Override
    public Integer visitLogicGroup(StmntParser.LogicGroupContext ctx ) {
        return visit(ctx.logicExp());
    }

    @Override
    public Integer visitLogicNot(StmntParser.LogicNotContext ctx) {
        visit(ctx.logicExp());
        code.writeByte(ByteCodes.Not.ordinal());
        return 0;
    }

    @Override
    public Integer visitLogicAnd(StmntParser.LogicAndContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        code.writeByte(ByteCodes.And.ordinal());
        return 0;
    }

    @Override
    public Integer visitLogicOr(StmntParser.LogicOrContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        code.writeByte(ByteCodes.Or.ordinal());
        return 0;
    }

    @Override
    public Integer visitLogicIntRel(StmntParser.LogicIntRelContext ctx) {
        return visit(ctx.intRelExp());
    }

    @Override
    public Integer visitLogicStringRel(StmntParser.LogicStringRelContext ctx) {
        return visit(ctx.stringRelExp());
    }

    @Override
    public Integer visitLogicLit(StmntParser.LogicLitContext ctx) {
        return visit(ctx.boolLit());
    }

    @Override
    public Integer visitLogicId(StmntParser.LogicIdContext ctx) {
        String name = ctx.ID().getText();
        return 0;
    }

    @Override
    public Integer visitLitTrue(StmntParser.LitTrueContext ctx) {
        code.writeByte(ByteCodes.Push.ordinal()).writeByte(RuntimeType.iBoolean.ordinal())
            .writeInteger(1);
        return 0;
    }

    @Override
    public Integer visitLitFalse(StmntParser.LitFalseContext ctx) {
        code.writeByte(ByteCodes.Push.ordinal()).writeByte(RuntimeType.iBoolean.ordinal())
            .writeInteger(0);
        return 0;
    }

    @Override
    public Integer visitIntRelExp(StmntParser.IntRelExpContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        String op = ctx.op.getText();
        switch(op) {
        case "<":
            code.writeByte(ByteCodes.Lt.ordinal());
            break;
        case "<=":
            code.writeByte(ByteCodes.Lte.ordinal());
            break;
        case "?=":
            code.writeByte(ByteCodes.Eq.ordinal());
            break;
        case "!=":
            code.writeByte(ByteCodes.Neq.ordinal());
            break;
        case ">=":
            code.writeByte(ByteCodes.Gte.ordinal());
            break;
        case ">":
            code.writeByte(ByteCodes.Gt.ordinal());
        }
        return 0;
    }

    @Override
    public Integer visitStringRelExp(StmntParser.StringRelExpContext ctx) {
        Integer ileft = visit(ctx.left);
        Integer iright = visit(ctx.right);
        String op = ctx.op.getText();
        return 0;
    }

    public void writeCodeTo(String fileName) {
        code.writeTo(fileName);
    }

    private CodeBuffer code;
}
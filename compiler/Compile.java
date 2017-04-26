import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.Vector;
import java.util.HashMap;

import common.ByteCodes;
import common.CodeBuffer;
import common.RuntimeType;
import common.Labeller;
import common.FixUp;

import parser.*;

public class Compile extends StmntBaseVisitor<Integer> {
    public Compile(Vector<String> names) {
        code = new CodeBuffer();
        where = new HashMap<String, Integer>();
        fixups = new FixUp();
        mutables = names;
        stringPool = new HashMap<String, String>();
        labelMaker = new Labeller();
    }

    @Override
    public Integer visitProg(StmntParser.ProgContext ctx) {
        Integer answer = 0;

        for(Integer i = 0; i < mutables.size(); i++) {
            where.put(mutables.elementAt(i), i * 2);
            code.writeByte(ByteCodes.Push)
                .writeByte(RuntimeType.iInteger)
                .writeInteger(0);
        }

        for(StmntParser.StatementContext sctx : ctx.statement()) {
            answer = visit(sctx);
        }
        code.writeByte(ByteCodes.Halt);

        // Dump the string pool past the end of the executable code
        for(String key : stringPool.keySet()) {
            where.put(key, code.getFinger());
            code.writeString(stringPool.get(key));
            code.writeByte(0); // zero terminate strings in the image
        }

        fixups.doFixups(where, code);

        return answer;
    }

    @Override
    public Integer visitPrintStmnt(StmntParser.PrintStmntContext ctx) {
        Integer answer = 0;

        for(StmntParser.ExpressionContext ectx : ctx.expression()) {
            answer = visit(ectx);
            code.writeByte(ByteCodes.Print);
        }

        code.writeByte(ByteCodes.PrtLn);

        return answer;
    }

    @Override
    public Integer visitAssign(StmntParser.AssignContext ctx) {
        Integer value = visit(ctx.expression());
        String name = ctx.ID().getText();

        code.writeByte(ByteCodes.Move);
        fixups.addFixup(name, code.getFinger());
        code.writeInteger(0);

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
        code.writeByte(ByteCodes.Pow);
        return 0;
    }

    @Override
    public Integer visitMult(StmntParser.MultContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        String op = ctx.op.getText();
        if(op.equals("*")) {
            code.writeByte(ByteCodes.Mul);
        }
        else if(op.equals("div")) {
            code.writeByte(ByteCodes.Div);
        }
        else {
            code.writeByte(ByteCodes.Rem);
        }

        return 0;
    }

    @Override
    public Integer visitAdd(StmntParser.AddContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        String op = ctx.op.getText();
        if(op.equals("+")) {
            code.writeByte(ByteCodes.Add);
        }
        else {
            code.writeByte(ByteCodes.Sub);
        }
        return 0;
    }

    @Override
    public Integer visitNumber(StmntParser.NumberContext ctx) {
        Integer answer = Integer.valueOf(ctx.NUMBER().getText().replace("_", ""));
        code.writeByte(ByteCodes.Push).writeByte(RuntimeType.iInteger)
            .writeInteger(answer);
        return answer;
    }

    @Override
    public Integer visitId(StmntParser.IdContext ctx) {
        String name = ctx.ID().getText();

        code.writeByte(ByteCodes.Copy);
        fixups.addFixup(name, code.getFinger());
        code.writeInteger(0);

        return 0;
    }

    @Override
    public Integer visitStringExp(StmntParser.StringExpContext ctx)  {
        String value = ctx.STRING().getText();
        value = value.substring(1);
        value = value.substring(0, value.length() - 1);
        value = value.intern();

        String label = null;
        if(!stringPool.containsValue(value)) {
            label = labelMaker.make("string");
            stringPool.put(label, value);
        }
        else {
            for(String key : stringPool.keySet()) {
                if(stringPool.get(key) == value) {
                    label = key;
                }
            }
        }

        code.writeByte(ByteCodes.Push).writeByte(RuntimeType.iString);
        fixups.addFixup(label, code.getFinger());
        code.writeInteger(0);

        return 0;
    }

    @Override
    public Integer visitLogicGroup(StmntParser.LogicGroupContext ctx ) {
        return visit(ctx.logicExp());
    }

    @Override
    public Integer visitLogicNot(StmntParser.LogicNotContext ctx) {
        visit(ctx.logicExp());
        code.writeByte(ByteCodes.Not);
        return 0;
    }

    @Override
    public Integer visitLogicAnd(StmntParser.LogicAndContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        code.writeByte(ByteCodes.And);
        return 0;
    }

    @Override
    public Integer visitLogicOr(StmntParser.LogicOrContext ctx) {
        visit(ctx.left);
        visit(ctx.right);
        code.writeByte(ByteCodes.Or);
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
        code.writeByte(ByteCodes.Push).writeByte(RuntimeType.iBoolean)
            .writeInteger(1);
        return 0;
    }

    @Override
    public Integer visitLitFalse(StmntParser.LitFalseContext ctx) {
        code.writeByte(ByteCodes.Push).writeByte(RuntimeType.iBoolean)
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
            code.writeByte(ByteCodes.Lt);
            break;
        case "<=":
            code.writeByte(ByteCodes.Lte);
            break;
        case "?=":
            code.writeByte(ByteCodes.Eq);
            break;
        case "!=":
            code.writeByte(ByteCodes.Neq);
            break;
        case ">=":
            code.writeByte(ByteCodes.Gte);
            break;
        case ">":
            code.writeByte(ByteCodes.Gt);
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
    private Vector<String> mutables;
    private HashMap<String, Integer> where;
    private FixUp fixups;
    private HashMap<String, String> stringPool;
    private Labeller labelMaker;
}

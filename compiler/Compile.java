import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.Vector;
import java.util.HashMap;

import common.ByteCodes;
import common.CodeBuffer;
import common.RuntimeType;
import common.Labeller;
import common.BackPatch;

import parser.*;

public class Compile extends StmntBaseVisitor<Integer> {
    public Compile(Vector<String> names) {
        code = new CodeBuffer();
        where = new HashMap<String, Integer>();
        backPatches = new BackPatch();
        mutables = names;
        stringPool = new HashMap<String, String>();
        labelMaker = new Labeller();
    }

    @Override
    public Integer visitProg(StmntParser.ProgContext ctx) {
        Integer answer = 0;

        code.writeByte(ByteCodes.Locals).writeInteger(mutables.size() * 2);
        for(Integer i = 0; i < mutables.size(); i++) {
            where.put(mutables.elementAt(i), (i * 2) + 1);
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

        backPatches.doBackPatches(where, code);

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
        backPatches.addBackPatch(name, code.getFinger());
        code.writeInteger(0);

        return value;
    }

    @Override
    public Integer visitIfStmnt(StmntParser.IfStmntContext ctx) {

        List<StmntParser.IfBlockContext> conditions = ctx.ifBlock();
        Boolean hasElse = ctx.block() != null;
        Integer elseIfCount = conditions.size() - 1;

        String ifEnd = labelMaker.make("ifEnd");
        Vector<String> elseIfLabels = new Vector<String>();
        for(int i = 0; i < elseIfCount; i++) {
            elseIfLabels.add(labelMaker.make("elseIfBegin"));
        }
        String elseBegin = labelMaker.make("elseBegin");

        Integer index = 0;
        for(StmntParser.IfBlockContext ifBlock : conditions) {
            String jmpOnTestFalse = ifEnd;

            if(hasElse) {
                jmpOnTestFalse  = elseBegin;
            }

            if(index < elseIfCount) {
                jmpOnTestFalse = elseIfLabels.get(index);
            }

            if(index > 0) {
                where.put(elseIfLabels.get(index - 1), code.getFinger());
            }

            visit(ifBlock.test);
            code.writeByte(ByteCodes.JmpF);
            backPatches.addBackPatch(jmpOnTestFalse, code.getFinger());
            code.writeInteger(0);

            visit(ifBlock.body);
            if(hasElse || (index < elseIfCount)) {
                code.writeByte(ByteCodes.Jmp);
                backPatches.addBackPatch(ifEnd, code.getFinger());
                code.writeInteger(0);
            }

            index++;
        }

        if(hasElse) {
            where.put(elseBegin, code.getFinger());

            visit(ctx.block());
        }

        where.put(ifEnd, code.getFinger());

        return 0;
    }

    @Override
    public Integer visitWhileStmnt(StmntParser.WhileStmntContext ctx) {

        String whileBegin = labelMaker.make("whileBegin");
        String whileEnd = labelMaker.make("whileEnd");

        where.put(whileBegin, code.getFinger());

        visit(ctx.test);
        code.writeByte(ByteCodes.JmpF);
        backPatches.addBackPatch(whileEnd, code.getFinger());
        code.writeInteger(0);

        visit(ctx.body);
        code.writeByte(ByteCodes.Jmp);
        backPatches.addBackPatch(whileBegin, code.getFinger());
        code.writeInteger(0);

        where.put(whileEnd, code.getFinger());

        return 0;
    }

    @Override
    public Integer visitBlock(StmntParser.BlockContext ctx) {
        //environment.beginScope();
        code.writeByte(ByteCodes.Enter);
        for(StmntParser.StatementContext sctx : ctx.statement()) {
            visit(sctx);
        }
        code.writeByte(ByteCodes.Exit);
        //environment.endScope();
        return 0;
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
        backPatches.addBackPatch(name, code.getFinger());
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
        backPatches.addBackPatch(label, code.getFinger());
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
    private BackPatch backPatches;
    private HashMap<String, String> stringPool;
    private Labeller labelMaker;
}

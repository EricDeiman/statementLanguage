import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import common.BackPatch;
import common.ByteCodes;
import common.CodeBuffer;
import common.Labeller;
import common.LookupPair;
import common.RuntimeError;
import common.RuntimeType;
import common.Scope;

import parser.*;

public class Compile extends StmntBaseVisitor<Integer> {
    public Compile(ParseTreeProperty<Scope> scopes) {
        code = new CodeBuffer();
        where = new HashMap<String, Integer>();
        backPatches = new BackPatch();
        this.scopes = scopes;
        stringPool = new HashMap<String, String>();
        labelMaker = new Labeller();
        functionNameSpace = new HashMap<String, FuncMeta>();
    }

    @Override
    public Integer visitProg(StmntParser.ProgContext ctx) {
        Integer answer = 0;
        currentScope = scopes.get(ctx);

        // signature
        code.writeByte('s').writeByte('t').writeByte('m').writeByte('n').writeByte('t');

        // major version minor version
        code.writeByte(0).writeByte(1);

        String startHere = labelMaker.make("main");
        backPatches.addBackPatch(startHere, code.getFinger());
        code.writeInteger(0);

        for(StmntParser.FuncDeclContext fctx : ctx.funcDecl()) {
            visit(fctx);
        }

        where.put(startHere, code.getFinger());

        Vector<String> mutables = currentScope.getNames();
        if(mutables.size() > 0)  {
            code.writeByte(ByteCodes.Locals).writeInteger(mutables.size() * 2);
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
    public Integer visitFuncDecl(StmntParser.FuncDeclContext ctx) {
        FuncMeta fun = new FuncMeta(labelMaker, ctx.ID());
        if(functionNameSpace.containsKey(fun.getInternalName())) {
            throw new RuntimeError("attempt to redefine function " + fun.getName() +
                                   " near " + ctx.getStart().getLine() + ":" +
                                   ctx.getStart().getCharPositionInLine());
        }
        functionNameSpace.put(fun.getInternalName(), fun);
        where.put(fun.getLabel(), code.getFinger());

        currentScope = scopes.get(ctx);

        for(String name : fun.getParameters()) {
            currentScope.putShadow(name);
        }

        visit(ctx.block());
        currentScope = currentScope.getParent();
        code.writeByte(ByteCodes.Return);

        return 0;
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

        LookupPair location = currentScope.get(name);
        code.writeByte(ByteCodes.Move).writeInteger(location.frames)
            .writeInteger(location.offset * 2);

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
        currentScope = scopes.get(ctx);
        code.writeByte(ByteCodes.Enter);

        Vector<String> mutables = currentScope.getNames();
        if(mutables.size() > 0)  {
            code.writeByte(ByteCodes.Locals).writeInteger(mutables.size() * 2);
        }

        for(StmntParser.StatementContext sctx : ctx.statement()) {
            visit(sctx);
        }
        code.writeByte(ByteCodes.Exit);
        currentScope = currentScope.getParent();
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
    public Integer visitFuncCall(StmntParser.FuncCallContext ctx) {
        String name = ctx.ID().getText();
        List<StmntParser.ExpressionContext> args = ctx.expression();
        String internalName = String.format("%s\\%d", name, args.size());

        if(!functionNameSpace.containsKey(internalName)) {
            throw new RuntimeError("cannot find function named " + name);
        }

        FuncMeta fun = functionNameSpace.get(internalName);

        Integer parametersSize = fun.getParameters().size();
        if(args.size() != parametersSize) {
            Integer line = ctx.getStart().getLine();
            Integer pos = ctx.getStart().getCharPositionInLine();
            throw new RuntimeError("attempt to call " + name + " with wrong parameter" +
                                   " count at " + line + ":" + pos +
                                   ".  Expected " + parametersSize + " got " +
                                   args.size());
        }

        String functionReturn = labelMaker.make("functionReturn");

        // push return instruction pointer
        code.writeByte(ByteCodes.Push).writeByte(RuntimeType.iInteger);
        backPatches.addBackPatch(functionReturn, code.getFinger());
        code.writeInteger(0);

        code.writeByte(ByteCodes.Enter);

        for(StmntParser.ExpressionContext arg : args) {
            visit(arg);
        }

        code.writeByte(ByteCodes.Call);
        backPatches.addBackPatch(fun.getLabel(), code.getFinger());
        code.writeInteger(0);

        where.put(functionReturn, code.getFinger());
        code.writeByte(ByteCodes.Exit);
        code.writeByte(ByteCodes.Pop); // get rid of return instruction pointer

        return 0;
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

        LookupPair location = currentScope.get(name);
        code.writeByte(ByteCodes.Copy).writeInteger(location.frames)
            .writeInteger(location.offset * 2);

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

        LookupPair here = currentScope.get(name);
        code.writeByte(ByteCodes.Copy).writeInteger(here.frames)
            .writeInteger(here.offset * 2);

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

    public void writeCodeTo(String fileName) {
        code.writeTo(fileName);
    }

    private CodeBuffer code;
    private ParseTreeProperty<Scope> scopes;
    private HashMap<String, Integer> where;
    private BackPatch backPatches;
    private HashMap<String, String> stringPool;
    private Labeller labelMaker;
    private Scope currentScope;
    private Map<String, FuncMeta> functionNameSpace;
}

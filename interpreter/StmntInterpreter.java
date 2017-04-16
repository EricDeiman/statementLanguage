import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Map;
import java.util.HashMap;

import parser.*;

public class StmntInterpreter extends StmntBaseVisitor<Integer> {

    public Map<String, Integer> environment = new HashMap<String,Integer>();

    @Override
    public Integer visitProg(StmntParser.ProgContext ctx) {
        Integer answer = 0;
        for(StmntParser.StatementContext sctx : ctx.statement()) {
            answer = visit(sctx);
        }

        return answer;
    }

    @Override
    public Integer visitPrint(StmntParser.PrintContext ctx) {
        Integer answer = visit(ctx.expression());
        System.out.println("The answer is " + answer);

        return answer;
    }

    @Override
    public Integer visitAssign(StmntParser.AssignContext ctx) {
        Integer value = visit(ctx.expression());
        String name = ctx.ID().getText();

        environment.put(name, value);

        return value;
    }

    @Override
    public Integer visitArith(StmntParser.ArithContext ctx) {
        return visit(ctx.arithExp());
    }

    @Override
    public Integer visitArithGroup(StmntParser.ArithGroupContext ctx) {
        return visit(ctx.arithExp());
    }

    @Override
    public Integer visitPower(StmntParser.PowerContext ctx) {
        Integer base = visit(ctx.left);
        Integer power = visit(ctx.right);
        Double answer = Math.pow(base, power);

        return answer.intValue();
    }

    @Override
    public Integer visitMult(StmntParser.MultContext ctx) {
        Integer left = visit(ctx.left);
        Integer right = visit(ctx.right);
        String op = ctx.op.getText();

        if(op.equals("*")) {
            return left * right;
        }
        else {
            return left / right;
        }
    }

    @Override
    public Integer visitAdd(StmntParser.AddContext ctx) {
        Integer left = visit(ctx.left);
        Integer right = visit(ctx.right);
        String op = ctx.op.getText();

        if(op.equals("+")) {
            return left + right;
        }
        else {
            return left - right;
        }
    }

    @Override
    public Integer visitNumber(StmntParser.NumberContext ctx) {
        return Integer.valueOf(ctx.NUMBER().getText());
    }

    @Override
    public Integer visitId(StmntParser.IdContext ctx) {
        String name = ctx.ID().getText();

        return environment.get(name);
    }
}

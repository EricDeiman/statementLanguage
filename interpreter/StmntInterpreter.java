import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Map;
import java.util.HashMap;

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

            switch(answer.getType()){
            case iInteger:
                System.out.print((Integer)answer.getValue());
                break;

            case iString:
                String say = (String)answer.getValue();
                say = say.replace("\"", "");
                System.out.print(say);
                break;

            default:
                System.out.println("error: cannot print this expression " +
                                   answer.getValue());
            }
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
    public InterpValue visitExpression(StmntParser.ExpressionContext ctx) {
        if(ctx.arithExp() != null) {
            return visit(ctx.arithExp());
        }
        if (ctx.stringExp() != null) {
            return visit(ctx.stringExp());
        }
        return null;
    }

    @Override
    public InterpValue visitStringExp(StmntParser.StringExpContext ctx)  {
        String value = ctx.STRING().getText();
        return new InterpValue(InterpType.iString, value);
    }

    @Override
    public InterpValue visitArithGroup(StmntParser.ArithGroupContext ctx) {
        return visit(ctx.arithExp());
    }

    @Override
    public InterpValue visitPower(StmntParser.PowerContext ctx) {
        InterpValue left = visit(ctx.left);
        InterpValue right = visit(ctx.right);

        expectTypes(InterpType.iInteger, left, right);

        Integer base = (Integer)left.getValue();
        Integer power = (Integer)right.getValue();
        Double answer = Math.pow(base, power);

        return new InterpValue(InterpType.iInteger, answer.intValue());
    }

    @Override
    public InterpValue visitMult(StmntParser.MultContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);

        expectTypes(InterpType.iInteger, ileft, iright);

        Integer left = (Integer)ileft.getValue();
        Integer right = (Integer)iright.getValue();
        String op = ctx.op.getText();

        Integer answer;

        if(op.equals("*")) {
            answer = left * right;
        }
        else if(op.equals("div")) {
            answer = left / right;
        }
        else {
            answer = left % right;
        }

        return new InterpValue(InterpType.iInteger, answer);
    }

    @Override
    public InterpValue visitAdd(StmntParser.AddContext ctx) {
        InterpValue ileft = visit(ctx.left);
        InterpValue iright = visit(ctx.right);

        expectTypes(InterpType.iInteger, ileft, iright);

        Integer left = (Integer)ileft.getValue();
        Integer right = (Integer)iright.getValue();
        String op = ctx.op.getText();

        Integer answer;

        if(op.equals("+")) {
            answer = left + right;
        }
        else {
            answer = left - right;
        }

        return new InterpValue(InterpType.iInteger, answer);
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

    //----------------------------------------------------------------------------

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

    private String makeInterpTypeString(InterpType iType) {
        switch(iType) {
        case iInteger:
            return "integer";

        case iString:
            return "string";
        }

        return "";
    }

    private String makeTypeMismatchMessage(InterpValue left, InterpValue right) {
        String leftType = makeInterpTypeString(left.getType());
        String rightType = makeInterpTypeString(right.getType());

        return leftType + " " + rightType;
    }
}

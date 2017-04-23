import java.io.InputStream;
import java.util.Stack;

import common.ByteCodes;
import common.CodeBuffer;
import common.RuntimeError;
import common.RuntimeType;

public class AbsMach {

    public AbsMach(String fileName) {
        code = new CodeBuffer();
        code.readFrom(fileName);
        stack = new Stack<Integer>();
        runtimeTypeCache = RuntimeType.values();
        byteCodesCache = ByteCodes.values();
    }

    public AbsMach(InputStream in) {
        code = new CodeBuffer();
        code.readFrom(in);
        stack = new Stack<Integer>();
        runtimeTypeCache = RuntimeType.values();
        byteCodesCache = ByteCodes.values();
    }

    public Integer go(Trace trace) {
        loop:
        while(true) {
            trace.preInstruction(code, stack);
            switch(byteCodesCache[code.readByte()]) {
            case Halt:
                break loop;
            case Push:
                leftType = runtimeTypeCache[code.readByte()];
                leftValue = code.readInteger();
                stack.push(leftType.ordinal());
                stack.push(leftValue);
                break;
            case Pop:
                stack.pop();  // pop the value
                stack.pop();  // pop the type
                break;
            case Add:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "add");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue + rightValue);
                break;
            case Sub:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "subtract");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue - rightValue);
                break;
            case Mul:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "multiply");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue * rightValue);
                break;
            case Div:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "'div'");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue / rightValue);
                break;
            case Rem:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "'rem'");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue % rightValue);
                break;
            case Pow:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "'pow'");

                Double danswer = Math.pow(leftValue, rightValue);
                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(danswer.intValue());
                break;
            case Lt:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "'<'");

                pushBoolean(leftValue < rightValue);
                break;
            case Lte:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "'<='");

                pushBoolean(leftValue <= rightValue);
                break;
            case Eq:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "?='");
                if(rightType != RuntimeType.iInteger || leftType != RuntimeType.iInteger) {
                    throw new RuntimeError("attempting to '?=' types of " +
                                           rightType.name() + " and " + leftType.name());
                }
                pushBoolean(leftValue == rightValue);
                break;
            case Neq:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "'!='");

                pushBoolean(leftValue != rightValue);
                break;
            case Gte:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "'>='");

                pushBoolean(leftValue >= rightValue);
                break;
            case Gt:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType, "'>'");

                pushBoolean(leftValue > rightValue);
                break;
            case And:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iBoolean, leftType, rightType, "'and'");

                pushBoolean(leftValue == 1 && rightValue == 1);
                break;
            case Or:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iBoolean, leftType, rightType, "'or'");

                pushBoolean(leftValue == 1 || rightValue == 1);
                break;
            case Not:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];

                expectType(RuntimeType.iBoolean, rightType, "'not'");

                pushBoolean(!(rightValue == 1));
                break;
            case Print:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                switch(rightType) {
                case iInteger:
                    System.out.println(rightValue);
                    break;
                case iBoolean:
                    System.out.println(rightValue == 1);
                    break;
                default:
                    System.out.println("don't now how to print type " + rightType);
                    break;
                }
                break;
            default:
                break;
            }
            trace.postInstruction(code, stack);
        }
        return stack.size();
    }

    public static void main(String[] args) {
        if(args.length != 1 && args.length != 2) {
            System.err.println("program needs to be called with a statement language " +
                               "object file.");
            return;
        }

        Trace tracer = new EmptyTrace();
        int fileArg = 0;

        if(args.length == 2) {
            switch(args[0]) {
            case "-print":
                tracer = new PrintTrace(System.out);
                fileArg = 1;
                break;
            default:
                fileArg = 1;
                break;
            }
        }

        AbsMach am = new AbsMach(args[fileArg]);
        am.go(tracer);
    }

    // ------------------------------------------------------------------------------

    private void pushBoolean(Boolean result) {
        stack.push(RuntimeType.iBoolean.ordinal());
        if(result) {
            stack.push(1);
        }
        else {
            stack.push(0);
        }
    }

    private Boolean expectTypes(RuntimeType expect, RuntimeType left, RuntimeType right,
                        String op) {
        if(left != expect || right != expect) {
            throw new RuntimeError("attempting to " + op + " types of " + left.name() +
                                   " and " + right.name());
        }

        return true;
    }

    private Boolean expectType(RuntimeType expect, RuntimeType left, String op) {
        if(left != expect) {
            throw new RuntimeError("attempting to " + op + " a type of " + left.name());
        }

        return true;
    }

    private CodeBuffer code;
    private Stack<Integer> stack;
    private RuntimeType leftType;
    private Integer leftValue;
    private RuntimeType rightType;
    private Integer rightValue;
    private RuntimeType[] runtimeTypeCache;
    private ByteCodes[] byteCodesCache;
}

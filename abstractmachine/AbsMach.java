import java.io.InputStream;
import java.io.File;
import java.io.PrintStream;
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

    public Integer go(Trace trace, Executor exec) {
        exec.doIt((ignore) ->
                  {
                      stack.push(1);
                      frameBase = 1;
                  }
                  );

        trace.preProgram(code, stack);
        
        loop:
        while(true) {
            trace.preInstruction(code, stack);
            switch(byteCodesCache[code.readByte()]) {
            case Halt:
                break loop;
            case Push:
                leftType = runtimeTypeCache[code.readByte()];
                leftValue = code.readInteger();
                exec.doIt((ignore) ->
                          {
                              stack.push(leftType.ordinal());
                              stack.push(leftValue);
                          }
                          );
                break;
            case Pop:
                exec.doIt((ignore) ->
                          {
                              stack.pop();  // pop the value
                              stack.pop();  // pop the type
                          }
                          );
                break;
            case Add:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "add");

                              stack.push(RuntimeType.iInteger.ordinal());
                              stack.push(leftValue + rightValue);
                          }
                          );
                break;
            case Sub:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "subtract");

                              stack.push(RuntimeType.iInteger.ordinal());
                              stack.push(leftValue - rightValue);
                          }
                          );
                break;
            case Mul:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "multiply");

                              stack.push(RuntimeType.iInteger.ordinal());
                              stack.push(leftValue * rightValue);
                          }
                          );
                break;
            case Div:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "'div'");

                              stack.push(RuntimeType.iInteger.ordinal());
                              stack.push(leftValue / rightValue);
                          }
                          );
                break;
            case Rem:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "'rem'");

                              stack.push(RuntimeType.iInteger.ordinal());
                              stack.push(leftValue % rightValue);
                          }
                          );
                break;
            case Pow:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "'pow'");

                              Double danswer = Math.pow(leftValue, rightValue);
                              stack.push(RuntimeType.iInteger.ordinal());
                              stack.push(danswer.intValue());
                          }
                          );
                break;
            case Lt:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "'<'");

                              pushBoolean(leftValue < rightValue);
                          }
                          );
                break;
            case Lte:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "'<='");

                              pushBoolean(leftValue <= rightValue);
                          }
                          );
                break;
            case Eq:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "?='");

                              pushBoolean(leftValue == rightValue);
                          }
                          );
                break;
            case Neq:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "'!='");

                              pushBoolean(leftValue != rightValue);
                          }
                          );
                break;
            case Gte:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "'>='");

                              pushBoolean(leftValue >= rightValue);
                          }
                          );
                break;
            case Gt:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iInteger, leftType, rightType,
                                          "'>'");

                              pushBoolean(leftValue > rightValue);
                          }
                          );
                break;
            case And:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iBoolean, leftType, rightType,
                                          "'and'");

                              pushBoolean(leftValue == 1 && rightValue == 1);
                          }
                          );
                break;
            case Or:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              leftValue = stack.pop();
                              leftType = runtimeTypeCache[stack.pop()];

                              expectTypes(RuntimeType.iBoolean, leftType, rightType,
                                          "'or'");

                              pushBoolean(leftValue == 1 || rightValue == 1);
                          }
                          );
                break;
            case Not:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];

                              expectType(RuntimeType.iBoolean, rightType, "'not'");

                              pushBoolean(!(rightValue == 1));
                          }
                          );
                break;
            case Print:
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              switch(rightType) {
                              case iInteger:
                                  System.out.print(rightValue);
                                  break;
                              case iBoolean:
                                  System.out.print(rightValue == 1);
                                  break;
                              case iString:
                                  StringBuilder sb = new StringBuilder();
                                  while(code.getByte(rightValue) != 0) {
                                      sb.append((char)code.getByte(rightValue++));
                                  }
                                  System.out.print(sb.toString());
                                  break;
                              default:
                                  System.err.println("don't now how to print type " +
                                                     rightType);
                                  break;
                              }
                              System.out.print(" ");
                          }
                          );
                break;
            case Move:
                leftValue = code.readInteger();
                exec.doIt((ignore) ->
                          {
                              stack.setElementAt(stack.pop(), leftValue + 1); // value
                              stack.setElementAt(stack.pop(), leftValue);     // type
                          }
                          );
                break;
            case Copy:
                leftValue = code.readInteger();
                exec.doIt((ignore) ->
                          {
                              stack.push(stack.elementAt(leftValue));      // type
                              stack.push(stack.elementAt(leftValue + 1));  // value
                          }
                          );
                break;
            case PrtLn:
                exec.doIt((ignore) ->
                          {
                              System.out.println();
                          }
                          );
                break;
            case Jmp:
                leftValue = code.readInteger();
                exec.doIt((ignore) ->
                          {
                              code.setFinger(leftValue);
                          }
                          );
                break;
            case JmpT:
                leftValue = code.readInteger();
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              expectType(RuntimeType.iBoolean, rightType, "test");
                              if(rightValue == 1) {
                                  code.setFinger(leftValue);
                              }
                          }
                          );
                break;
            case JmpF:
                leftValue = code.readInteger();
                exec.doIt((ignore) ->
                          {
                              rightValue = stack.pop();
                              rightType = runtimeTypeCache[stack.pop()];
                              expectType(RuntimeType.iBoolean, rightType, "test");
                              if(rightValue == 0) {
                                  code.setFinger(leftValue);
                              }
                          }
                          );
                break;
            case Enter:
                exec.doIt ((ignore) ->
                           {
                               stack.push(frameBase);
                               frameBase = stack.size();
                           }
                           );
                break;
            case Exit:
                exec.doIt((ignore) ->
                          {
                              while(stack.size() > frameBase) {
                                  stack.pop();
                              }
                              frameBase = stack.pop();
                          }
                          );
                break;
            case Locals:
                leftValue = code.readInteger();
                exec.doIt((ignore) ->
                          {
                              while(leftValue > 0) {
                                  stack.push(0);
                                  leftValue--;
                              }
                          }
                          );
                break;
            default:
                break;
            }
            trace.postInstruction(code, stack);
        }
        trace.postProgram(code, stack);
        return stack.size();
    }

    public static void main(String[] args) throws Exception {
        if(args.length != 1 && args.length != 2 && args.length != 3) {
            System.err.println("program needs to be called with a statement language " +
                               "object file.");
            return;
        }

        Trace tracer = new EmptyTrace();
        int fileArg = 0;
        Executor exec = new Exec();

        if(args.length == 2 || args.length == 3) {
            switch(args[0]) {
            case "-print":
                tracer = new PrintTrace(System.out);
                fileArg = 1;
                break;
            case "-disasm":
                tracer = new PrintTrace(System.out);
                exec = new NoOp();
                fileArg = 1;
                break;
            case "-dump":
                tracer = new PrintTrace(new PrintStream(new File(args[1])));
                fileArg = 2;
                break;
            default:
                fileArg = 1;
                break;
            }
        }

        AbsMach am = new AbsMach(args[fileArg]);
        am.go(tracer, exec);
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
    private Integer frameBase;
}

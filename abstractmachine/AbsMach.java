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

        String signature = "stmnt";
        for(int i = 0; i < signature.length(); i++) {
            if(code.getByte(i) != signature.charAt(i)) {
                throw new RuntimeError("invalid signature in " + fileName);
            }
        }

        Integer majorVer = code.getByte(signature.length());
        if( majorVer != 0) {
            throw new RuntimeError("cannot run major version " + majorVer );
        }

        Integer minorVer = code.getByte(signature.length() + 1);
        if( minorVer != 1) {
            throw new RuntimeError("cannot run minor version " + minorVer );
        }

        code.setFinger(code.getInteger(signature.length() + 2));
    }

    public AbsMach(InputStream in) {
        code = new CodeBuffer();
        code.readFrom(in);
        stack = new Stack<Integer>();
        runtimeTypeCache = RuntimeType.values();
        byteCodesCache = ByteCodes.values();

        String signature = "stmnt";
        for(int i = 0; i < signature.length(); i++) {
            if(code.getByte(i) != signature.charAt(i)) {
                throw new RuntimeError("invalid signature in stream");
            }
        }

        Integer majorVer = code.getByte(signature.length());
        if( majorVer != 0) {
            throw new RuntimeError("cannot run major version " + majorVer );
        }

        Integer minorVer = code.getByte(signature.length() + 1);
        if( minorVer != 0) {
            throw new RuntimeError("cannot run minor version " + minorVer );
        }

        code.setFinger(code.getInteger(signature.length() + 2));
    }

    public Integer go(Trace trace) {
        stack.push(1);
        frameBase = 1;
        Integer returnValue = 0;
        Integer returnType = 0;

        trace.preProgram(code, stack, frameBase);

        loop:
        while(true) {
            trace.preInstruction(code, stack, frameBase);
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

                expectTypes(RuntimeType.iInteger, leftType, rightType,
                            "add");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue + rightValue);
                break;
            case Sub:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType,
                            "subtract");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue - rightValue);
                break;
            case Mul:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType,
                            "multiply");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue * rightValue);
                break;
            case Div:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType,
                            "'div'");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue / rightValue);
                break;
            case Rem:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType,
                            "'rem'");

                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(leftValue % rightValue);
                break;
            case Pow:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iInteger, leftType, rightType,
                            "'pow'");

                Double danswer = Math.pow(leftValue, rightValue);
                stack.push(RuntimeType.iInteger.ordinal());
                stack.push(danswer.intValue());
                break;
            case Lt:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                if(leftType == RuntimeType.iString &&
                   rightType == RuntimeType.iString) {
                    String leftString = getString(leftValue);
                    String rightString = getString(rightValue);
                    pushBoolean(leftString.compareTo(rightString) < 0);
                }
                else {
                    expectTypes(RuntimeType.iInteger, leftType, rightType,
                                "'<'");
                    pushBoolean(leftValue < rightValue);
                }
                break;
            case Lte:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                if(leftType == RuntimeType.iString &&
                   rightType == RuntimeType.iString) {
                    String leftString = getString(leftValue);
                    String rightString = getString(rightValue);
                    pushBoolean(leftString.compareTo(rightString) <= 0);
                }
                else {
                    expectTypes(RuntimeType.iInteger, leftType, rightType,
                                "'<='");
                    pushBoolean(leftValue <= rightValue);
                }
                break;
            case Eq:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                if(leftType == RuntimeType.iString &&
                   rightType == RuntimeType.iString) {
                    String leftString = getString(leftValue);
                    String rightString = getString(rightValue);
                    pushBoolean(leftString.compareTo(rightString) == 0);
                }
                else {
                    expectTypes(RuntimeType.iInteger, leftType, rightType,
                                "'?='");
                    pushBoolean(leftValue == rightValue);
                }
                break;
            case Neq:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                if(leftType == RuntimeType.iString &&
                   rightType == RuntimeType.iString) {
                    String leftString = getString(leftValue);
                    String rightString = getString(rightValue);
                    pushBoolean(leftString.compareTo(rightString) != 0);
                }
                else {
                    expectTypes(RuntimeType.iInteger, leftType, rightType,
                                "'!='");
                    pushBoolean(leftValue != rightValue);
                }
                break;
            case Gte:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                if(leftType == RuntimeType.iString &&
                   rightType == RuntimeType.iString) {
                    String leftString = getString(leftValue);
                    String rightString = getString(rightValue);
                    pushBoolean(leftString.compareTo(rightString) >= 0);
                }
                else {
                    expectTypes(RuntimeType.iInteger, leftType, rightType,
                                "'>='");
                    pushBoolean(leftValue >= rightValue);
                }
                break;
            case Gt:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                if(leftType == RuntimeType.iString &&
                   rightType == RuntimeType.iString) {
                    String leftString = getString(leftValue);
                    String rightString = getString(rightValue);
                    pushBoolean(leftString.compareTo(rightString) > 0);
                }
                else {
                    expectTypes(RuntimeType.iInteger, leftType, rightType,
                                "'>'");
                    pushBoolean(leftValue > rightValue);
                }
                break;
            case And:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iBoolean, leftType, rightType,
                            "'and'");

                pushBoolean(leftValue == 1 && rightValue == 1);
                break;
            case Or:
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                leftValue = stack.pop();
                leftType = runtimeTypeCache[stack.pop()];

                expectTypes(RuntimeType.iBoolean, leftType, rightType,
                            "'or'");

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
                    System.out.print(rightValue);
                    break;
                case iBoolean:
                    System.out.print(rightValue == 1);
                    break;
                case iString:
                    System.out.print(getString(rightValue));
                    break;
                default:
                    System.err.println("don't now how to print type " +
                                       rightType);
                    break;
                }
                System.out.print(" ");
                break;
            case Move: {
                leftValue = code.readInteger();  // frames
                rightValue = code.readInteger(); // offset
                Integer tempFrameBase = frameBase;
                while(leftValue > 0){
                    tempFrameBase = stack.elementAt(tempFrameBase - 1);
                    leftValue--;
                }
                stack.setElementAt(stack.pop(),
                                   tempFrameBase + rightValue + 1); // value
                stack.setElementAt(stack.pop(),
                                   tempFrameBase + rightValue);     // type
            }
                break;
            case Copy: {
                leftValue = code.readInteger();  // frames
                rightValue = code.readInteger(); // offset
                Integer tempFrameBase = frameBase;
                while(leftValue > 0){
                    tempFrameBase = stack.elementAt(tempFrameBase - 1);
                    leftValue--;
                }
                stack.push(stack.elementAt(tempFrameBase +
                                           rightValue));      // type
                stack.push(stack.elementAt(tempFrameBase +
                                           rightValue + 1));  // value
            }
                break;
            case PrtLn:
                System.out.println();
                break;
            case Jmp:
                leftValue = code.readInteger();
                code.setFinger(leftValue);
                break;
            case JmpT:
                leftValue = code.readInteger();
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                expectType(RuntimeType.iBoolean, rightType, "test");
                if(rightValue == 1) {
                    code.setFinger(leftValue);
                }
                break;
            case JmpF:
                leftValue = code.readInteger();
                rightValue = stack.pop();
                rightType = runtimeTypeCache[stack.pop()];
                expectType(RuntimeType.iBoolean, rightType, "test");
                if(rightValue == 0) {
                    code.setFinger(leftValue);
                }
                break;
            case Enter:
                stack.push(frameBase);
                frameBase = stack.size();
                break;
            case Exit:
                while(stack.size() > frameBase) {
                    stack.pop();
                }
                frameBase = stack.pop();
                break;
            case Locals:
                leftValue = code.readInteger();
                while(leftValue > 0) {
                    stack.push(0);
                    leftValue--;
                }
                break;

            case Call:
                leftValue = code.readInteger();
                code.setFinger(leftValue);
                break;

            case Return: {
                Integer previousFrameBase = stack.get(frameBase - 1);
                leftValue = returnValue;  // value
                leftType= runtimeTypeCache[returnType];  // type
                stack.set(previousFrameBase - 4, leftValue);
                stack.set(previousFrameBase - 5, leftType.ordinal());
                leftValue = stack.elementAt(previousFrameBase - 2);

                //                temporary
                while(stack.size() > frameBase) {
                    stack.pop();
                }
                frameBase = stack.pop();
                // end of temporary

                code.setFinger(leftValue);
            }
                break;

            case SetRtn:
                returnValue = stack.pop();
                returnType = stack.pop();
                break;

            default:
                break;
            }
            trace.postInstruction(code, stack, frameBase);
        }
        trace.postProgram(code, stack, frameBase);
        return stack.size();
    }

    private String getString(Integer location) {
        StringBuilder sb = new StringBuilder();
        while(code.getByte(location) != 0) {
            sb.append((char)code.getByte(location++));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        if(args.length != 1 && args.length != 2 && args.length != 3) {
            System.err.println("program needs to be called with a statement language " +
                               "object file.");
            return;
        }

        Trace tracer = new EmptyTrace();
        int fileArg = 0;

        if(args.length == 2 || args.length == 3) {
            switch(args[0]) {
            case "-print":
                tracer = new PrintTrace(System.out);
                fileArg = 1;
                break;
            case "-dump":
                tracer = new PrintTrace(new PrintStream(new File(args[1])));
                fileArg = 2;
                break;
            case "-disasm": {
                DisAsm disAsm = null;
                if(args.length == 2) {
                    disAsm = new DisAsm(args[1], System.out);
                }
                else if(args.length == 3) {
                    PrintStream out = new PrintStream(new File(args[1]));
                    disAsm = new DisAsm(args[2], out);
                }
                disAsm.go();
                return;
            }
            default:
                fileArg = 1;
                break;
            }
        }

        try {
            AbsMach am = new AbsMach(args[fileArg]);
            am.go(tracer);
        }
        catch(RuntimeError err) {
            System.err.println("The program doesn't mean what you think it means: " +
                               err.getMessage());
        }

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
    private Integer startHere;
}

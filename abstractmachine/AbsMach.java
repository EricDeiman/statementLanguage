import java.io.InputStream;
import java.util.Stack;

import common.ByteCodes;
import common.CodeBuffer;
import common.RuntimeError;

public class AbsMach {

    public AbsMach(String fileName) {
        code = new CodeBuffer();
        code.readFrom(fileName);
        stack = new Stack<Integer>();
    }

    public AbsMach(InputStream in) {
        code = new CodeBuffer();
        code.readFrom(in);
        stack = new Stack<Integer>();
    }

    public Integer go(Trace trace) {
        loop:
        while(true) {
            trace.preInstruction(code, stack);
            switch(code.readByte()) {
            case ByteCodes.Halt:
                break loop;
            case ByteCodes.Push:
                left = code.readInteger();
                stack.push(left);
                break;
            case ByteCodes.Pop:
                stack.pop();
                break;
            case ByteCodes.Add:
                right = stack.pop();
                left = stack.pop();
                stack.push(left + right);
                break;
            case ByteCodes.Sub:
                right = stack.pop();
                left = stack.pop();
                stack.push(left - right);
                break;
            case ByteCodes.Mul:
                right = stack.pop();
                left = stack.pop();
                stack.push(left * right);
                break;
            case ByteCodes.Div:
                right = stack.pop();
                left = stack.pop();
                stack.push(left / right);
                break;
            case ByteCodes.Rem:
                right = stack.pop();
                left = stack.pop();
                stack.push(left % right);
                break;
            case ByteCodes.Pow:
                right = stack.pop();
                left = stack.pop();
                Double danswer = Math.pow(left, right);
                stack.push(danswer.intValue());
                break;
            case ByteCodes.Lt:
                right = stack.pop();
                left = stack.pop();
                if(left < right) {
                    stack.push(1);
                }
                else {
                    stack.push(0);
                }
                break;
            case ByteCodes.Lte:
                right = stack.pop();
                left = stack.pop();
                if(left <= right) {
                    stack.push(1);
                }
                else {
                    stack.push(0);
                }
                break;
            case ByteCodes.Eq:
                right = stack.pop();
                left = stack.pop();
                if(left == right) {
                    stack.push(1);
                }
                else {
                    stack.push(0);
                }
                break;
            case ByteCodes.Neq:
                right = stack.pop();
                left = stack.pop();
                if(left != right) {
                    stack.push(1);
                }
                else {
                    stack.push(0);
                }
                break;
            case ByteCodes.Gte:
                right = stack.pop();
                left = stack.pop();
                if(left >= right) {
                    stack.push(1);
                }
                else {
                    stack.push(0);
                }
                break;
            case ByteCodes.Gt:
                right = stack.pop();
                left = stack.pop();
                if(left > right) {
                    stack.push(1);
                }
                else {
                    stack.push(0);
                }
                break;
            case ByteCodes.And:
                right = stack.pop();
                left = stack.pop();
                if(left == 1 && right == 1) {
                    stack.push(1);
                }
                else {
                    stack.push(0);
                }
                break;
            case ByteCodes.Or:
                right = stack.pop();
                left = stack.pop();
                if(left == 1 || right == 1) {
                    stack.push(1);
                }
                else {
                    stack.push(0);
                }
                break;
            case ByteCodes.Not:
                left = stack.pop();
                if(left == 1) {
                    stack.push(0);
                }
                else {
                    stack.push(1);
                }
                break;
            case ByteCodes.Print:
                left = stack.pop();
                System.out.println(left);
                break;
            default:
                break;
            }
            trace.postInstruction(code, stack);
        }
        return stack.size();
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("program needs to be called with a statement language " +
                               "object file.");
            return;
        }

        AbsMach am = new AbsMach(args[0]);
        am.go(new PrintTrace(System.out));
    }

    private CodeBuffer code;
    private Stack<Integer> stack;
    private Integer left;
    private Integer right;
}

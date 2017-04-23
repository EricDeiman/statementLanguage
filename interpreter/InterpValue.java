
import org.antlr.v4.runtime.Token;

import common.RuntimeType;

/**
 * Values that our interpreter works with.  It encapsulates the Java types that
 * are used to implement the interpreter. Values also know how to perform primative
 * operations on themselves, e.g., iIntergers know how to multiply or subtract themselves.
 */
public class InterpValue {

    /**
     * Interpret-time values know their type along with their value.
     */
    public InterpValue( RuntimeType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public RuntimeType getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    /**
     * Perform arithmatic on runtime iIntergers.  This method assumes that type checking
     * has already succeeded.
     */
    public InterpValue doMath(String op, InterpValue iright) {
        Integer left = (Integer)this.value;
        Integer right = (Integer)iright.getValue();
        Integer answer = 0;

        switch(op) {
        case "+":
            answer = left + right;
            break;
        case "-":
            answer = left - right;
            break;
        case "*":
            answer = left * right;
            break;
        case "div":
            answer = left / right;
            break;
        case "rem":
            answer = left % right;
            break;
        case "^":
            Double danswer = Math.pow(left, right);
            answer = danswer.intValue();
            break;
        }

        return new InterpValue(RuntimeType.iInteger, answer);
    }

    /**
     * Perform comparisons of runtime iIntergers. This method assumes that type checking
     * has already succeeded.
     */
    public InterpValue doIntRel(String op, InterpValue iright) {
        Integer left = (Integer)this.value;
        Integer right = (Integer)iright.getValue();

        Boolean answer;

        switch(op) {
        case "<":
            answer = left < right;
            break;
        case "<=" :
            answer = left <= right;
            break;
        case "?=":
            answer = left == right;
            break;
        case "!=":
            answer = left != right;
            break;
        case ">=":
            answer = left >= right;
            break;
        case ">":
            answer = left > right;
            break;
        default:
            answer = false;
        }

        return new InterpValue(RuntimeType.iBoolean, answer);
    }

    /**
     * Perform comparisons of runtime iStrings. This method assumes that type checking
     * has already succeeded.
     */
    public InterpValue doStringRel(String op, InterpValue iright) {
        String left = (String)this.value;
        String right = (String)iright.getValue();

        Boolean answer;

        switch(op) {
        case "<":
            answer = left.compareTo(right) < 0;
            break;
        case "<=" :
            answer = left.compareTo(right) <= 0;
            break;
        case "?=":
            answer = left.compareTo(right) == 0;
            break;
        case "!=":
            answer = left.compareTo(right) != 0;
            break;
        case ">=":
            answer = left.compareTo(right) >= 0;
            break;
        case ">":
            answer = left.compareTo(right) > 0;
            break;
        default:
            answer = false;
        }

        return new InterpValue(RuntimeType.iBoolean, answer);
    }

    /**
     * Perform logical operations on iBooleans.  This method assumes that type checking
     * has already succeeded.
     */
    public InterpValue doLogic(String op, InterpValue iright) {
        Boolean left = (Boolean)this.value;
        Boolean right = false;

        if(iright != null) {
            right = (Boolean)iright.getValue();
        }

        Boolean answer = false;

        switch(op) {
        case "not":
            answer = !left;
            break;
        case "and":
            answer = left && right;
            break;
        case "or":
            answer = left || right;
            break;
        }

        return new InterpValue(RuntimeType.iBoolean, answer);
    }

    public String toString() {
        if(type == RuntimeType.iString) {
            // The ANTLR parser returns the double quotes around the string.
            // When the string gets printed by the interpreter, they get taken off.
            StringBuilder sb = new StringBuilder((String)value);
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return value.toString();
    }

    private RuntimeType type;
    private Object value;
}

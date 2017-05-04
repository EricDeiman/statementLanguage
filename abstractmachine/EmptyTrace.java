import java.util.Stack;
import common.CodeBuffer;

public class EmptyTrace implements Trace {
    public void preProgram(CodeBuffer code, Stack<Integer> stack, Integer frame) {
        return;
    }

    public void preInstruction(CodeBuffer code, Stack<Integer> stack, Integer frame) {
        return;
    }

    public void postInstruction(CodeBuffer code, Stack<Integer> stack, Integer frame) {
        return;
    }

    public void postProgram(CodeBuffer code, Stack<Integer> stack, Integer frame) {
        return;
    }
}

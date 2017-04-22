import java.util.Stack;
import common.CodeBuffer;

public class EmptyTrace implements Trace {
    public void preInstruction(CodeBuffer code, Stack<Integer> stack) {
        return;
    }

    public void postInstruction(CodeBuffer code, Stack<Integer> stack) {
        return;
    }
}

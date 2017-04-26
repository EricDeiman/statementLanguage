import java.util.Stack;
import common.CodeBuffer;

public interface Trace {
    public void preProgram(CodeBuffer code, Stack<Integer> stack);
    public void preInstruction(CodeBuffer code, Stack<Integer> stack);
    public void postInstruction(CodeBuffer code, Stack<Integer> stack);
    public void postProgram(CodeBuffer code, Stack<Integer> stack);
}

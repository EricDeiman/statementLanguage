import java.util.Stack;
import common.CodeBuffer;

public interface Trace {
    public void preProgram(CodeBuffer code, Stack<Integer> stack, Integer frame);
    public void preInstruction(CodeBuffer code, Stack<Integer> stack, Integer frame);
    public void postInstruction(CodeBuffer code, Stack<Integer> stack, Integer frame);
    public void postProgram(CodeBuffer code, Stack<Integer> stack, Integer frame);
}

import java.util.Stack;
import java.io.PrintStream;
import common.CodeBuffer;
import common.ByteCodes;


public class PrintTrace implements Trace {
    public PrintTrace(PrintStream out) {
        this.out = out;
    }

    public void preInstruction(CodeBuffer code, Stack<Integer> stack) {
        Integer position = code.getFinger();
        Integer opCode = code.getByte(position);
        StringBuilder buffer = new StringBuilder();
        Integer operand = 0;
        Boolean hasOperand = false;

        switch(opCode) {
        case ByteCodes.Push:
            hasOperand = true;
            operand = code.getInteger(position + 1);
            break;
        }

        String operandXStr = hasOperand ? String.format("%08x", operand) : "        ";
        String operandDStr = hasOperand ? String.format("%d", operand) : " ";
        out.println(String.format("%04x %02x %s   %s %s",
                                  position,
                                  opCode,
                                  operandXStr,
                                  ByteCodes.getMnemonic(opCode),
                                  operandDStr));

        return;
    }

    public void postInstruction(CodeBuffer code, Stack<Integer> stack) {
        return;
    }

    private PrintStream out;
}

import java.util.Stack;
import java.io.PrintStream;

import common.CodeBuffer;
import common.ByteCodes;
import common.RuntimeType;


public class PrintTrace extends EmptyTrace {
    public PrintTrace(PrintStream out) {
        this.out = out;
        runtimeTypeCache = RuntimeType.values();
        byteCodesCache = ByteCodes.values();
    }

    public void preInstruction(CodeBuffer code, Stack<Integer> stack) {
        Integer position = code.getFinger();
        ByteCodes opCode = byteCodesCache[code.getByte(position)];
        StringBuilder buffer = new StringBuilder();
        Integer operand = 0;
        Integer type = 0;
        Boolean hasOperand = false;

        switch(opCode) {
        case Push:
            hasOperand = true;
            type = code.getByte(position + 1);
            operand = code.getInteger(position + 2);
            break;
        }

        String typeXStr = hasOperand ? String.format("%02x", type) : "";
        String operandXStr = hasOperand ? String.format("%08x", operand) : "";
        String typeDStr = hasOperand ? runtimeTypeCache[type].name() : "";
        String operandDStr = hasOperand ? String.format("%d", operand) : "";
        out.print(String.format("%04x %02x %2s %8s   %-5s %8s %1s",
                                position,
                                opCode.ordinal(),
                                typeXStr,
                                operandXStr,
                                opCode.name().toLowerCase(),
                                typeDStr,
                                operandDStr));

        out.print("  [ ");
        if(stack.size() > 0) {
            for(int i = stack.size() - 1; i > -1; i--) {
                out.print(String.format("%d ", stack.elementAt(i)));
            }
        }
        out.println(" ]");

        return;
    }

    private PrintStream out;
    private RuntimeType[] runtimeTypeCache;
    private ByteCodes[] byteCodesCache;
}

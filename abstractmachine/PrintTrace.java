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
        needsNewLine = false;
    }

    public void preInstruction(CodeBuffer code, Stack<Integer> stack, Integer frame) {
        Integer position = code.getFinger();
        ByteCodes opCode = byteCodesCache[code.getByte(position)];
        StringBuilder buffer = new StringBuilder();
        Integer operand = 0;
        Integer type = 0;
        Boolean hasOperand = false;
        Boolean hasOperandType = false;

        needsNewLine = false;

        switch(opCode) {
        case Push:
            hasOperand = true;
            hasOperandType = true;
            type = code.getByte(position + 1);
            operand = code.getInteger(position + 2);
            break;
        case Move:
        case Copy:
        case Jmp:
        case JmpT:
        case JmpF:
        case Locals:
            hasOperand = true;
            hasOperandType = false;
            operand = code.getInteger(position + 1);
            break;
        case Print:
            needsNewLine = true;
            break;
        }

        String typeXStr = hasOperandType ? String.format("%02x", type) : "";
        String operandXStr = hasOperand ? String.format("%08x", operand) : "";
        String typeDStr = hasOperandType ? runtimeTypeCache[type].name() : "";
        String operandDStr = hasOperand ? String.format("%d", operand) : "";
        out.print(String.format("%04x:  %02x %2s %8s   %-6s %-8s %-8s",
                                position,
                                opCode.ordinal(),
                                typeXStr,
                                operandXStr,
                                opCode.name().toLowerCase(),
                                typeDStr,
                                operandDStr));

        out.print("  [ ");
        if(stack.size() > 0) {
            if(frame == stack.size()) {
                out.print("| ");
            }
            for(int i = stack.size() - 1; i > -1; i--) {
                out.print(String.format("%d ", stack.elementAt(i)));
                if(i == frame) {
                    out.print("| ");
                }
            }
        }
        out.println(" ]");

        return;
    }

    public void postInstruction(CodeBuffer code, Stack<Integer> stack, Integer frame) {
        if(needsNewLine && out == System.out) {
            out.println();
        }
    }

    public void postProgram(CodeBuffer code, Stack<Integer> stack, Integer frame) {
        // Let's look for the string pool
        while(code.getFinger() < code.size()) {
            // There's more stuff past the halt op code.  Assume it's a string pool.
            Integer position = code.getFinger();

            StringBuilder sb = new StringBuilder();
            while(code.getByte(code.getFinger()) != 0) {
                sb.append((char)code.readByte());
            }
            code.readByte(); // ignore the terminating zero byte

            out.println(String.format("%04x:  %s", position, sb.toString()));
        }
    }

    private PrintStream out;
    private RuntimeType[] runtimeTypeCache;
    private ByteCodes[] byteCodesCache;
    private Boolean needsNewLine;
}

import java.io.PrintStream;

import common.CodeBuffer;
import common.ByteCodes;
import common.RuntimeType;

public class DisAsm {
    public static void disAsm(CodeBuffer code, PrintStream out) {
        ByteCodes[] byteCodesCache = ByteCodes.values();
        RuntimeType[] runtimeTypeCache = RuntimeType.values();

        while(code.getByte(code.getFinger()) != ByteCodes.Halt.ordinal()) {
            Integer position = code.getFinger();
            ByteCodes opCode = byteCodesCache[code.readByte()];
            StringBuilder buffer = new StringBuilder();
            Integer operand = 0;
            Integer type = 0;
            Boolean hasOperand = false;
            Boolean hasOperandType = false;

            switch(opCode) {
            case Push:
                hasOperand = true;
                hasOperandType = true;
                type = code.readByte();
                operand = code.readInteger();
                break;
            case Move:
                hasOperand = true;
                hasOperandType = false;
                operand = code.readInteger();
                break;
            case Copy:
                hasOperand = true;
                hasOperandType = false;
                operand = code.readInteger();
                break;
            }

            String typeXStr = hasOperandType ? String.format("%02x", type) : "";
            String operandXStr = hasOperand ? String.format("%08x", operand) : "";
            String typeDStr = hasOperandType ? runtimeTypeCache[type].name() : "";
            String operandDStr = hasOperand ? String.format("%d", operand) : "";
            out.println(String.format("%04x %02x %2s %8s   %-5s %8s %1s",
                                      position,
                                      opCode.ordinal(),
                                      typeXStr,
                                      operandXStr,
                                      opCode.name().toLowerCase(),
                                      typeDStr,
                                      operandDStr));
        }
    }
}

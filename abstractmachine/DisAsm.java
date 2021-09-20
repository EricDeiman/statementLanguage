/*
  The statementLanguage programming language
  Copyright 2016 Eric J. Deiman

  This file is part of the statementLanguage programming language.
  The statementLanguage programming language is free software: you can redistribute it
  and/ormodify it under the terms of the GNU General Public License as published by the
  Free Software Foundation, either version 3 of the License, or (at your option) any
  later version.
  
  The statementLanguage programming language is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with the
  statementLanguage programming language. If not, see <https://www.gnu.org/licenses/>
*/

import java.io.InputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Stack;

import common.ByteCodes;
import common.CodeBuffer;
import common.RuntimeError;
import common.RuntimeType;

public class DisAsm {

    public DisAsm(String fileName, PrintStream out) {
        code = new CodeBuffer();
        this.fileName = fileName;
        code.readFrom(fileName);
        this.out = out;
        runtimeTypeCache = RuntimeType.values();
        byteCodesCache = ByteCodes.values();
    }

    public void dumpInstructions() {
        Boolean done = false;

        while(!done) {
            Integer position = code.getFinger();
            ByteCodes opCode = byteCodesCache[code.readByte()];
            StringBuilder buffer = new StringBuilder();
            Integer operand = 0;
            Integer type = 0;
            Boolean hasOperand = false;
            Boolean hasOperandType = false;

            String typeDStr = "";
            String operandDStr = "";

            switch(opCode) {
            case Push:
                hasOperand = true;
                hasOperandType = true;
                type = code.readByte();
                operand = code.readInteger();
                typeDStr = runtimeTypeCache[type].name();

                if(type == RuntimeType.iString.ordinal()) {
                    operandDStr = String.format("0x%-6x", operand);
                }
                else {
                    operandDStr = String.format("%d", operand);
                }
                break;

            case Move:
            case Copy:
                hasOperandType = true;
                hasOperand = true;
                type = code.readInteger();
                operand = code.readInteger();
                typeDStr = String.format("%d : ", type);
                operandDStr = String.format("%-8d", operand);
                break;

            case Jmp:
            case JmpT:
            case JmpF:
            case Call:
                hasOperand = true;
                hasOperandType = false;
                operand = code.readInteger();
                operandDStr = String.format("0x%-6x", operand);
                break;
            case Locals:
                hasOperand = true;
                hasOperandType = false;
                operand = code.readInteger();
                operandDStr = String.format("%-8d", operand);
                break;
            case Halt:
                done = true;
                break;
            }

            String typeXStr = hasOperandType ? String.format("%02x", type) : "";
            String operandXStr = hasOperand ? String.format("%08x", operand) : "";
            out.println(String.format("%04x:  %02x %2s %8s   %-6s %-8s %-8s",
                                    position,
                                    opCode.ordinal(),
                                    typeXStr,
                                    operandXStr,
                                    opCode.name().toLowerCase(),
                                    typeDStr,
                                    operandDStr));
        }

        return;
    }

    public void dumpStringPool() {
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

    public void go() {
        // Check the signature
        StringBuilder sb = new StringBuilder();
        String signature = "stmnt";
        for(int i = 0; i < signature.length(); i++) {
            sb.append((char)code.readByte());
        }

        if(!sb.toString().equals(signature)) {
            throw new RuntimeError(fileName + " isn't a stmnt object file");
        }

        Integer majorVer = code.readByte();
        Integer minorVer = code.readByte();

        Integer entry = code.readInteger();

        String output = String.format("%s version %d.%d; entry: 0x%08x", sb, majorVer,
                                      minorVer, entry);
        out.println(output);

        dumpInstructions();
        dumpStringPool();
    }

    public static void main(String[] args) {
        if(args.length != 1 && args.length != 2) {
            throw new RuntimeError("can only disassemble 1 file at a time.");
        }

        String inputFileName = args[0];
        PrintStream out = System.out;

        switch(args.length) {
        case 1:
            break;
        case 2:
            try {
                out = new PrintStream(new File(args[1]));
            }
            catch (Exception e) {
                throw new RuntimeError(e.getMessage());
            }
            break;
        }

        DisAsm disAsm = new DisAsm(inputFileName, out);
        disAsm.go();
    }

    private CodeBuffer code;
    private PrintStream out;
    private String fileName;
    private RuntimeType[] runtimeTypeCache;
    private ByteCodes[] byteCodesCache;
}

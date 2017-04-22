package common;

public class ByteCodes {
    public static final int Halt = 0x0;
    public static final int Push = 0x1;
    public static final int Pop = 0x2;
    public static final int Add = 0x3;
    public static final int Sub = 0x4;
    public static final int Mul = 0x5;
    public static final int Div = 0x6;
    public static final int Rem = 0x7;
    public static final int Pow = 0x8;
    public static final int Lt = 0x9;
    public static final int Lte = 0xa;
    public static final int Eq = 0xb;
    public static final int Neq = 0xc;
    public static final int Gte = 0xd;
    public static final int Gt = 0xe;
    public static final int And = 0xf;
    public static final int Or = 0x10;
    public static final int Not = 0x11;
    public static final int Print = 0x12;

    public static final String[] mnemonic =
    {
        "halt",
        "push",
        "pop",
        "add",
        "sub",
        "mul",
        "div",
        "rem",
        "pow",
        "lt",
        "lte",
        "eq",
        "neq",
        "gte",
        "gt",
        "and",
        "or",
        "not",
        "print"
    };

    public static final String getMnemonic(int code) {
        return mnemonic[code];
    }
}

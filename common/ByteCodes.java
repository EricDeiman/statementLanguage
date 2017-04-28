package common;

public enum ByteCodes {
    Halt,   // 0x00
    Push,   // 0x01
    Pop,    // 0x02
    Add,    // 0x03
    Sub,    // 0x04
    Mul,    // 0x05
    Div,    // 0x06
    Rem,    // 0x07
    Pow,    // 0x08
    Lt,     // 0x09
    Lte,    // 0x0a
    Eq,     // 0x0b
    Neq,    // 0x0c
    Gte,    // 0x0d
    Gt,     // 0x0e
    And,    // 0x0f
    Or,     // 0x10
    Not,    // 0x11
    Print,  // 0x12   // print top of stack followed by space to the console
    Move,   // 0x13   // move top of stack to stack position given by parameter
    Copy,   // 0x14   // copy stack position given by parameter to top of stack
    PrtLn,  // 0x15   // print a new line to the console
    Jmp,    // 0x16   // jump unconditionally to the parameter
    JmpT,   // 0x17   // if the top of stack is true jump to parameter
    JmpF,   // 0x18   // if the top of stack is false jump to parameter
    Enter,  // 0x19   // enter a new scope
    Exit,   // 0x1a   // exiti a scope
    Locals, // 0x1b   // reserve parameter number of stack slots
}

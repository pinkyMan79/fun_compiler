package one.terenin.colon.node;

public enum NodeTypes {
    // operations
    CONST,
    VAR,
    ADD,
    SUB,
    MUL,
    DIV,
    LESSTHEN,
    SET,
    IF,
    IFELSE,
    WHILE,
    EMPTY,
    SEQ, // sequence of expressions (past and now -> op1, op2)
    EXPR,
    PROG,
    PRINT
}
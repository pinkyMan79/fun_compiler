package one.terenin.colon.service;

import one.terenin.colon.node.Node;

public  class Extender {
    public static final String PROGRAMS_START = """
            ASSUME CS:CODE, DS:DATA

            DATA SEGMENT
                vars dw 26 dup (0)
            DATA ENDS;

            CODE SEGMENT
                PRINT MACRO
                local DIVCYC, PRINTNUM;
                push ax;
                push bx;
                push cx;
                push dx;
                mov bx, 10;
                mov cx, 0;
            DIVCYC:
                mov dx, 0;
                div bx;
                push dx;
                inc cx;
                cmp ax, 0;
                jne DIVCYC;
            PRINTNUM:
                pop dx;
                add dl, '0';
                mov ah, 02h;
                int 21h;
                dec cx;
                cmp cx, 0;
                jne PRINTNUM;
                mov dl, 0dh;
                mov ah, 02h;
                int 21h;
                mov dl, 0ah;
                mov ah, 02h;
                int 21h;
                pop dx;
                pop cx;
                pop bx;
                pop ax;
                ENDM;

            begin:
                lea si, vars;
            """;
    public static final String PROGRAMS_END = """
                mov ah, 4Ch;
                int 21h;
            CODE ENDS;
            end begin
            """;

    private Node tree;

    public Extender(Node tree) {
        this.tree = tree;
    }

    public String compile() {
        StringBuilder programAsm = new StringBuilder();
        programAsm.append(PROGRAMS_START);
        int labelCount = 0;
        programAsm.append(compileNode(tree, new IntWrapper(labelCount)));
        programAsm.append(PROGRAMS_END);
        return programAsm.toString();
    }

    private static class IntWrapper {
        public int value;

        public IntWrapper(int value) {
            this.value = value;
        }
    }

    private String compileNode(Node block, IntWrapper labelCount) {
        if (block == null) {
            return ""; // или выбросить исключение, если null недопустим
        }
        int lCount1, lCount2, firstLabelNum, secondLabelNum, thirdLabelNum;
        StringBuilder result = new StringBuilder();
        switch (block.type) {
            case PROG:
                result.append(compileNode(block.op1, labelCount));
                break;
            case PRINT:
                result.append(compileNode(block.op1, labelCount));
                result.append("        PRINT;\n");
                break;
            case VAR:
                result.append("        mov ax, [si + ").append(block.value * 2).append("];\n");
                break;
            case CONST:
                result.append("        mov ax, ").append(block.value).append(";\n");
                break;
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case LESSTHEN: //Общая логика для бинарных операций
                result.append(compileNode(block.op1, labelCount));
                result.append("        push ax;\n");
                result.append(compileNode(block.op2, labelCount));
                result.append("        mov bx, ax;\n");
                result.append("        pop ax;\n");
                switch (block.type) {
                    case ADD: result.append("        add ax, bx;\n"); break;
                    case SUB: result.append("        sub ax, bx;\n"); break;
                    case MUL: result.append("        mul bx;\n"); break;
                    case DIV: result.append("        xor dx, dx;\n        div bx;\n"); break;
                    case LESSTHEN:
                        result.append("        cmp ax, bx;\n");
                        result.append("        jae LBL").append(labelCount.value++).append(";\n");
                        result.append("        mov ax, 1;\n");
                        result.append("        jmp LBL").append(labelCount.value++).append(";\n");
                        result.append("    LBL").append(labelCount.value - 2).append(":\n");
                        result.append("        mov ax, 0;\n");
                        result.append("    LBL").append(labelCount.value - 1).append(": nop\n");
                        break;
                }
                break;
            case SET:
                result.append(compileNode(block.op2, labelCount));
                if (block.op1 != null) { // Проверка на null для block.op1
                    result.append("        mov [si + ").append(block.op1.value * 2).append("], ax;\n");
                } else {
                    System.err.println("Error: block.op1 is null in SET node."); // Обработка ошибки
                }
                break;

            case EXPR:
                result.append(compileNode(block.op1, labelCount)).append('\n');
                break;
            case IF:
                // ... (case IF, SEQ, IFELSE, WHILE остаются с проверками на null внутри, если необходимо)
                result.append(compileNode(block.op1, labelCount));
                result.append("        cmp ax, 0;\n");
                lCount1 = labelCount.value;
                result.append("        jne LBL").append(labelCount.value++).append(";\n");
                lCount2 = labelCount.value;
                result.append("        jmp LBL").append(labelCount.value++).append(";\n");
                result.append("    LBL").append(lCount1).append(": nop\n");
                result.append(compileNode(block.op2, labelCount));
                result.append("    LBL").append(lCount2).append(": nop\n");
                break;
            case SEQ:
                result.append(compileNode(block.op1, labelCount));
                result.append(compileNode(block.op2, labelCount));
                break;
            case IFELSE:
                result.append(compileNode(block.op1, labelCount));
                result.append("        cmp ax, 0;\n");
                firstLabelNum = labelCount.value;
                result.append("        je LBL").append(labelCount.value++).append(";\n");
                result.append(compileNode(block.op2, labelCount));
                secondLabelNum = labelCount.value;
                result.append("        jmp LBL").append(labelCount.value++).append(";\n");
                result.append("    LBL").append(firstLabelNum).append(": nop\n");
                result.append(compileNode(block.op3, labelCount));
                result.append("    LBL").append(secondLabelNum).append(": nop\n");
                break;
            case WHILE:
                secondLabelNum = labelCount.value;
                result.append("        jmp LBL").append(labelCount.value++).append("; \n");
                firstLabelNum = labelCount.value;
                result.append("    LBL").append(labelCount.value++).append(": nop\n");
                result.append(compileNode(block.op2, labelCount));
                result.append("    LBL").append(secondLabelNum).append(": nop\n");
                result.append(compileNode(block.op1, labelCount));
                result.append("        cmp ax, 0;\n");
                thirdLabelNum = labelCount.value;
                result.append("        je LBL").append(thirdLabelNum).append("\n");
                result.append("        jmp LBL").append(firstLabelNum).append("\n");
                result.append("    LBL").append(thirdLabelNum).append(": nop\n");
                break;
            default:
                break;
        }
        return result.toString();
    }
}
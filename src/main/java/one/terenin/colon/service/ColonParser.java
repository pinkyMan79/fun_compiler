package one.terenin.colon.service;

import one.terenin.colon.token.Token;
import one.terenin.colon.token.TokenTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ColonParser {

    private List<Token> tokens;
    private final StringBuilder asmConcentrator = new StringBuilder();
    private final Stack<Integer> validateForConst = new Stack<>();
    private final Map<String, Integer> validateForVariables = new HashMap<>();

    public ColonParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean parse() {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            switch (token.type) {
                case NUM -> {
                    // Generate ASM for numeric constant
                    validateForConst.push(token.value);
                    asmConcentrator.append("PUSH ").append(token.variable).append("\n");
                }
                case VAR -> {
                    if (validateForConst.isEmpty())
                        throw new RuntimeException("Not possible to set variable because const stack is empty");

                    // Store variable
                    validateForVariables.put(token.variable + "", validateForConst.pop());
                    asmConcentrator.append("MOV ").append(token.variable).append(", [ESP]\n");
                }
                case MUL -> {
                    if (validateForConst.size() >= 2) {
                        // Pop two values from stack and generate multiplication
                        asmConcentrator.append("POP EBX\n");
                        asmConcentrator.append("POP EAX\n");
                        asmConcentrator.append("IMUL EAX, EBX\n");
                        asmConcentrator.append("PUSH EAX\n");
                    } else {
                        throw new RuntimeException("Invalid token: " + token);
                    }
                }
                case ADD -> {
                    if (validateForConst.size() >= 2 || validateForVariables.size() >= 2 ||
                            (!validateForConst.isEmpty() && !validateForVariables.isEmpty())) {
                        asmConcentrator.append("POP EBX\n");
                        asmConcentrator.append("POP EAX\n");
                        asmConcentrator.append("ADD EAX, EBX\n");
                        asmConcentrator.append("PUSH EAX\n");
                    } else {
                        throw new RuntimeException("Invalid token: " + token);
                    }
                }
                case SUB -> {
                    if (validateForConst.size() >= 2) {
                        asmConcentrator.append("POP EBX\n");
                        asmConcentrator.append("POP EAX\n");
                        asmConcentrator.append("SUB EAX, EBX\n");
                        asmConcentrator.append("PUSH EAX\n");
                    } else {
                        throw new RuntimeException("Invalid token: " + token);
                    }
                }
                case DIV -> {
                    if (validateForConst.size() >= 2) {
                        asmConcentrator.append("POP EBX\n");
                        asmConcentrator.append("POP EAX\n");
                        asmConcentrator.append("CDQ\n"); // Sign extend EAX into EDX for division
                        asmConcentrator.append("IDIV EBX\n");
                        asmConcentrator.append("PUSH EAX\n");
                    } else {
                        throw new RuntimeException("Invalid token: " + token);
                    }
                }
                case PRINT -> {
                    if (!validateForConst.isEmpty()) {
                        asmConcentrator.append("POP EAX\n");
                        asmConcentrator.append("CALL PRINT\n");
                    } else {
                        throw new RuntimeException("Invalid token: " + token);
                    }
                }
                case IF -> {
                    asmConcentrator.append("POP EAX\n"); // Condition
                    asmConcentrator.append("CMP EAX, 0\n");
                    asmConcentrator.append("JE ELSE_LABEL\n"); // Jump to ELSE if false
                }
                case ELSE -> {
                    asmConcentrator.append("JMP END_IF_LABEL\n");
                    asmConcentrator.append("ELSE_LABEL:\n");
                }
                case WHILE -> {
                    asmConcentrator.append("WHILE_LABEL:\n");
                }
                case LBRA -> {
                    // Start block (e.g., for IF or WHILE)
                    // No specific ASM instruction needed, but it marks a scope
                }
                case RBRA -> {
                    asmConcentrator.append("END_IF_LABEL:\n"); // End block for IF/ELSE
                }
                case LPAR, RPAR -> {
                    // Parentheses for grouping, no direct ASM equivalent
                }
                case LESS -> {
                    if (validateForConst.size() >= 2) {
                        asmConcentrator.append("POP EBX\n");
                        asmConcentrator.append("POP EAX\n");
                        asmConcentrator.append("CMP EAX, EBX\n");
                        asmConcentrator.append("JL LESS_TRUE_LABEL\n");
                    } else {
                        throw new RuntimeException("Invalid token: " + token);
                    }
                }
                case ASSIG -> {
                    // Assignment, store value in variable
                    if (!validateForConst.isEmpty()) {
                        asmConcentrator.append("POP EAX\n");
                        asmConcentrator.append("MOV ").append(token.variable).append(", EAX\n");
                    } else {
                        throw new RuntimeException("Invalid assignment token: " + token);
                    }
                }
                case SEMICOL -> {
                    // End of statement, no specific ASM needed
                }
                case ENDFILE -> {
                    asmConcentrator.append("END_PROGRAM:\n");
                }
                case ERROR -> {
                    throw new RuntimeException("Error token encountered: " + token);
                }
                default -> throw new UnsupportedOperationException("Token type not implemented: " + token.type);
            }
        }
        return true;
    }

    public String getGeneratedAssembly() {
        return asmConcentrator.toString();
    }
}

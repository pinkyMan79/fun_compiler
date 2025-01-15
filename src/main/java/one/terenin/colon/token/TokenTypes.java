package one.terenin.colon.token;

import java.util.HashMap;
import java.util.Map;

// {} - expression brackets (all program is expression)
// () - paren expression brackets, u.e. if, while ...

public enum TokenTypes {
    NUM, VAR, IF, ELSE, WHILE, LBRA, RBRA, LPAR, RPAR,
    ADD, SUB, MUL, DIV, LESS, ASSIG,
    SEMICOL, ENDFILE, ERROR, PRINT;

    public static Map<String, TokenTypes> charMap() {
        Map<String, TokenTypes> map = new HashMap<>();
        map.put("{", TokenTypes.LBRA);
        map.put("}", TokenTypes.RBRA);
        map.put("(", TokenTypes.LPAR);
        map.put(")", TokenTypes.RPAR);
        map.put("+", TokenTypes.ADD);
        map.put("-", TokenTypes.SUB);
        map.put("*", TokenTypes.MUL);
        map.put("/", TokenTypes.DIV);
        map.put("<", TokenTypes.LESS);
        map.put("=", TokenTypes.ASSIG);
        map.put(";", TokenTypes.SEMICOL);
        return map;
    }

    public static Map<String, TokenTypes> wordsMap() {
        Map<String, TokenTypes> map = new HashMap<>();
        map.put("if", TokenTypes.IF);
        map.put("else", TokenTypes.ELSE);
        map.put("while", TokenTypes.WHILE);
        map.put("print", TokenTypes.PRINT);
        return map;
    }
}

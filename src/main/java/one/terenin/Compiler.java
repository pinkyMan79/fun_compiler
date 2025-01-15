package one.terenin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import one.terenin.colon.node.Node;
import one.terenin.colon.token.Token;
import one.terenin.colon.token.TokenTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class Compiler {
    char[] text;
    int textLen;
    int nowPos = 0;
    List<Token> tokens = new ArrayList<>();
    
    static Map<String, TokenTypes> specialSymbols = new HashMap<>();
    static Map<String, TokenTypes> specialWords = new HashMap<>();

    public Compiler(char[] text, int textLen) {
        this.text = text;
        this.textLen = textLen;
    }

    public boolean compile(StringBuilder out) {

        Token token = getNextToken();
        while (!token.getType().equals(TokenTypes.ENDFILE)) {
            if (token.getType().equals(TokenTypes.ERROR)) {
                throw new RuntimeException("Error on token parsing step");
            }
            tokens.add(token);
            token = getNextToken();
        }

        return false;
    }

    private Token getNextToken() {
        if (nowPos >= text.length) {
            return new Token(TokenTypes.ENDFILE);
        }
        String concentrator = "";
        char currChar = text[nowPos++];
        // ignore
        while (currChar == '\n' || currChar == '\r' || currChar == ' ' || currChar == '\t') {
            currChar = text[nowPos++];
        }
        // if ends
        if (nowPos >= text.length) {
            return new Token(TokenTypes.ENDFILE);
        }
        // token splitting and validation
        while (!(currChar == '\n' || currChar == '\r' || currChar == ' ' || currChar == '\t')) {
            if (concentrator.isEmpty()) {
                // validate for symbol or digit or variable
                if (specialSymbols.containsKey(currChar + "")) {
                    return new Token(specialSymbols.get(concentrator));
                } else if (isDigit(currChar)) {
                    // number like 1235 or e.g.
                    do {
                        concentrator += currChar;
                        currChar = text[nowPos++];
                    } while (isDigit(currChar));
                    nowPos--;
                    break;
                } else {
                    // it only can be the command
                    concentrator += currChar;
                }
            } else {
                if (specialWords.containsKey(currChar + "")) {
                    nowPos--;
                    break;
                }
                concentrator += currChar;
            }

            if (specialWords.containsKey(concentrator)) {
                return new Token(specialWords.get(concentrator));
            }

            if (nowPos >= text.length) {
                break;
            }

            currChar = text[nowPos++];
        }

        if (concentrator.length() == 1 && concentrator.charAt(0) >= 'a' && concentrator.charAt(0) <= 'z') {
            return new Token(TokenTypes.VAR, 0, concentrator.charAt(0));
        }

        int value = 0;
        for (char c : concentrator.toCharArray()) {
            if (isDigit(c)) {
                value = (value * 10) + Character.getNumericValue(c);
            } else {
                return new Token(TokenTypes.ERROR);
            }
        }
        return new Token(TokenTypes.NUM, value, 'a');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
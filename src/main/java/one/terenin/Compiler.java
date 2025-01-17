package one.terenin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import one.terenin.colon.node.Node;
import one.terenin.colon.node.NodeTypes;
import one.terenin.colon.service.Extender;
import one.terenin.colon.service.Parser;
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
    
    static Map<String, TokenTypes> specialSymbols = TokenTypes.charMap();
    static Map<String, TokenTypes> specialWords = TokenTypes.wordsMap();

    public Compiler(char[] text, int textLen) {
        this.text = text;
        this.textLen = textLen;
    }

    public boolean compile() {

        Token token = getNextToken();
        while (!token.type.equals(TokenTypes.ENDFILE)) {
            if (token.type.equals(TokenTypes.ERROR)) {
                throw new RuntimeException("Error on token parsing step");
            }
            tokens.add(token);
            token = getNextToken();
        }
        Node tree = new Node(NodeTypes.EMPTY, 0);
        boolean parsed = new Parser().parse(tokens, tree);

        Extender extender = new Extender(tree);
        String compile = extender.compile();
        System.out.println(compile);
        return true;
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
                    return new Token(specialSymbols.get(currChar + ""));
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
                if (specialSymbols.containsKey(currChar + "")) {
                    // for cases like a< and e.g.
                    // we detect that special symbol appears before any word
                    // wove careet on step back and break
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
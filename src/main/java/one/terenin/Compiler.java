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
        return false;
    }

    private Token getNextToken() {
        return new Token(TokenTypes.ERROR, 0, 'a');
    }
}
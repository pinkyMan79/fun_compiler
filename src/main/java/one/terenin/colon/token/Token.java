package one.terenin.colon.token;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

public class Token {
    public TokenTypes type;
    public int value;
    public char variable;

    public Token(TokenTypes type, int value, char variable) {
        this.type = type;
        this.value = value;
        this.variable = variable;
    }

    public Token(TokenTypes type) {
        this(type, 0, 's');
    }
}
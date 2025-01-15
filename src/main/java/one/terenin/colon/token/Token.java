package one.terenin.colon.token;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class Token {
    TokenTypes type;
    int value;
    char variable;

    public Token(TokenTypes type, int value, char variable) {
        this.type = type;
        this.value = value;
        this.variable = variable;
    }

    public Token(TokenTypes type) {
        this(type, 0, 's');
    }
}
package one.terenin.colon.node;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

// три операции - максимум детей у ноды, например сложению нужно 2 ноды (op1.value + op2.value)
// а вот условному оператору с иначе нужно уже 3 ноды в (op1 - условие на проверку,
// op2 - выражение на исполнение если тру и op3 - выражение на исполнение иначе)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class Node {
    NodeTypes type;
    int value;
    Node op1;
    Node op2;
    Node op3;

    public Node(NodeTypes type, int value, Node op1, Node op2, Node op3) {
        this.type = type;
        this.value = value;
        this.op1 = op1;
        this.op2 = op2;
        this.op3 = op3;
    }
}
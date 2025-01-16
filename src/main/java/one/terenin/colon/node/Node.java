package one.terenin.colon.node;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

// три операции - максимум детей у ноды, например сложению нужно 2 ноды (op1.value + op2.value)
// а вот условному оператору с иначе нужно уже 3 ноды в (op1 - условие на проверку,
// op2 - выражение на исполнение если тру и op3 - выражение на исполнение иначе)
public class Node {
    public NodeTypes type;
     // for variable cases it will be the poz in ascii array
    public int value;
    public Node op1;
    public Node op2;
    public Node op3;

    public Node(NodeTypes type, int value, Node op1, Node op2, Node op3) {
        this.type = type;
        this.value = value;
        this.op1 = op1 != null ? op1 : new Node(NodeTypes.EMPTY, 0);
        this.op2 = op2 != null ? op2 : new Node(NodeTypes.EMPTY, 0);
        this.op3 = op3 != null ? op3 : new Node(NodeTypes.EMPTY, 0);
    }

    public Node(NodeTypes type) {
        this.type = type;
        this.value = 0;
        this.op1 = null;
        this.op2 = null;
        this.op3 = null;
    }

    public Node() {
        this.type = NodeTypes.EMPTY;
        this.value = 0;
        this.op1 = null;
        this.op2 = null;
        this.op3 = null;
    }

    public Node(NodeTypes type, int value) {
        this.type = type;
        this.value = value;
        this.op1 = null;
        this.op2 = null;
        this.op3 = null;
    }

    @Override
    public String toString() {
        return this.type + " " + this.value + " ";
    }
}
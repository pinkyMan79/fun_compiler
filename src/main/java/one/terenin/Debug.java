package one.terenin;

import one.terenin.colon.node.Node;
import one.terenin.colon.node.NodeTypes;

import java.util.Optional;

public class Debug {

    public static void inOrder(Node node) {
        if (node != null) {
            System.out.print(node.type + " " + node.value + "\n");
            inOrder(node.op1);
            inOrder(node.op2);
            inOrder(node.op3);
        }
    }

}

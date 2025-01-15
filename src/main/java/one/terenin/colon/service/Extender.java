package one.terenin.colon.service;

import one.terenin.colon.node.Node;

public  class Extender {
    private static final String programsStart = "PROGRAM START";
    private static final String programsEnd = "PROGRAM END";
    private Node tree;

    public Extender(Node tree) {
        this.tree = tree;
    }

    public String compile() {
        int labelCount = 0;
        return compileNode(tree, labelCount);
    }

    private String compileNode(Node node, int labelCount) {
        return "";
    }
}
package one.terenin.colon.service;

import one.terenin.colon.node.Node;
import one.terenin.colon.node.NodeTypes;
import one.terenin.colon.token.Token;
import one.terenin.colon.token.TokenTypes;

import java.util.List;
import java.util.Objects;

public class Parser {

    private int currentTokenIndex = 0;
    private List<Token> tokens;

    private Node root; // for debug only

    public boolean parse(List<Token> tokens, Node result) {
        this.tokens = tokens;
        this.root = result;
        currentTokenIndex = 0;
        if (statement(result)) {
            return currentTokenIndex == tokens.size();
        }
        return false;
    }

    private boolean statement(Node node) {
        if (currentTokenIndex >= tokens.size()) {
            return false;
        }

        switch (tokens.get(currentTokenIndex).type) {
            case IF:
                currentTokenIndex++;
                node.type = NodeTypes.IF;
                node.op1 = new Node();
                if (!parenExpr(node.op1)) return false;
                node.op2 = new Node();
                if (!statement(node.op2)) return false;

                if (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).type == TokenTypes.ELSE) {
                    node.type = NodeTypes.IFELSE;
                    currentTokenIndex++;
                    node.op3 = new Node();
                    if (!statement(node.op3)) return false;
                }
                return true;
            case WHILE:
                currentTokenIndex++;
                node.type = NodeTypes.WHILE;
                node.op1 = new Node();
                if (!parenExpr(node.op1)) return false;
                node.op2 = new Node();
                if (!statement(node.op2)) return false;
                return true;
            case LBRA:
                currentTokenIndex++;
                node.type = NodeTypes.SEQ;
                Node currentSeqNode = node;
                while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).type != TokenTypes.RBRA) {
                    currentSeqNode.op2 = new Node();
                    if (!statement(currentSeqNode.op2)) return false;
                    if (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).type != TokenTypes.RBRA) {
                        currentSeqNode.op1 = new Node(NodeTypes.SEQ);
                        currentSeqNode = currentSeqNode.op1;
                    }
                }
                if (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).type == TokenTypes.RBRA) {
                    currentTokenIndex++;
                    return true;
                }
                return false;
            case SEMICOL:
                currentTokenIndex++;
                return true;
            case PRINT:
                currentTokenIndex++;
                node.type = NodeTypes.PRINT;
                node.op1 = new Node();
                if (!parenExpr(node.op1)) return false;
                return true;
            default:
                node.type = NodeTypes.EXPR;
                node.op1 = new Node();
                if (!expr(node.op1)) return false;
                if (currentTokenIndex >= tokens.size() || tokens.get(currentTokenIndex).type != TokenTypes.SEMICOL) return false;
                currentTokenIndex++;
                return true;
        }
    }

    private boolean parenExpr(Node node) {
        if (currentTokenIndex >= tokens.size() || tokens.get(currentTokenIndex).type != TokenTypes.LPAR) {
            return false;
        }
        currentTokenIndex++;
        if (!expr(node)) {
            return false;
        }
        if (currentTokenIndex >= tokens.size() || tokens.get(currentTokenIndex).type != TokenTypes.RPAR) {
            return false;
        }
        currentTokenIndex++;
        return true;
    }

    private boolean expr(Node node) {
        if (currentTokenIndex >= tokens.size()) return false;
        int initialIndex = currentTokenIndex;

        Node leftNode = new Node();
        if (!test(leftNode)) return false;

        if (leftNode.type == NodeTypes.VAR && currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).type == TokenTypes.ASSIG) {
            Node assignNode = new Node(NodeTypes.SET);
            assignNode.op1 = leftNode;
            currentTokenIndex++;
            assignNode.op2 = new Node();
            if (!test(assignNode.op2)) return false;
            node.type = assignNode.type;
            node.op1 = assignNode.op1;
            node.op2 = assignNode.op2;
            return true;
        } else {
            currentTokenIndex = initialIndex;
            return test(node);
        }
    }

    private boolean test(Node node) {
        if (currentTokenIndex >= tokens.size()) return false;

        if (!arExpr(node)) return false;

        Node cache = new Node(node.type, node.value, node.op1, node.op2, node.op3);

        if (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).type == TokenTypes.LESS) {
            Node lessThanNode = new Node(NodeTypes.LESSTHEN);
            lessThanNode.op1 = node;
            currentTokenIndex++;
            lessThanNode.op2 = new Node();
            if (!arExpr(lessThanNode.op2)) return false;
            node.type = lessThanNode.type;
            node.op1 = cache;
            node.op2 = lessThanNode.op2;
        }
        return true;
    }

    private boolean arExpr(Node node) {
        if (currentTokenIndex >= tokens.size()) return false;

        if (!term(node)) return false;

        Node cache = new Node(node.type, node.value, node.op1, node.op2, node.op3);

        while (currentTokenIndex < tokens.size() && (tokens.get(currentTokenIndex).type == TokenTypes.DIV
                || tokens.get(currentTokenIndex).type == TokenTypes.SUB
                || tokens.get(currentTokenIndex).type == TokenTypes.ADD
                || tokens.get(currentTokenIndex).type == TokenTypes.MUL)) {

            NodeTypes nodeType = switch (tokens.get(currentTokenIndex).type) {
                case DIV -> NodeTypes.DIV;
                case SUB -> NodeTypes.SUB;
                case MUL -> NodeTypes.MUL;
                case ADD -> NodeTypes.ADD;
                default -> NodeTypes.EMPTY;
            };

            Node tmpNode = new Node(nodeType);
            tmpNode.op1 = node;
            currentTokenIndex++;
            tmpNode.op2 = new Node();
            if (!term(tmpNode.op2)) return false;
            node.type = tmpNode.type;
            node.op1 = cache;
            node.op2 = tmpNode.op2;
        }
        return true;
    }

    // the leaves generator of our syntax tree
    private boolean term(Node node) {
        if (currentTokenIndex >= tokens.size()) return false;
        Token token = tokens.get(currentTokenIndex);

        switch (token.type) {
            case VAR -> {
                node.type = NodeTypes.VAR;
                node.value = token.variable - 'a';
                currentTokenIndex++;
            }
            case NUM -> {
                node.type = NodeTypes.CONST;
                node.value = token.value;
                currentTokenIndex++;
            }
            default -> {
                boolean res = parenExpr(node);
                if (!res) {
                    return false;
                }
            }
        }
        return true;
    }
}
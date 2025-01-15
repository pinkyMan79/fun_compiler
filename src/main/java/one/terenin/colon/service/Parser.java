package one.terenin.colon.service;

import one.terenin.colon.node.Node;
import one.terenin.colon.node.NodeTypes;
import one.terenin.colon.token.Token;
import one.terenin.colon.token.TokenTypes;

import java.util.List;

public class Parser {
    public static boolean parse(List<Token> tokens, Node result) {
        int count = 0;
        boolean parsed = statement(tokens, count, result);
        if (count != tokens.size() - 1 || !parsed) {
            return false;
        }
        return true;
    }

    private static boolean statement(List<Token> tokens, int count, Node node) {
        boolean parsed = true;
        switch (tokens.get(count).type) {
            case IF -> {
                count++;
                node.type = NodeTypes.IF;
                parsed = parenExpr(tokens, count, node.op1);
                if (!parsed) return false;
                parsed = statement(tokens, count, node.op2);
                if (!parsed) return false;

                if (tokens.get(count).type == TokenTypes.ELSE) {
                    node.type = NodeTypes.IFELSE;
                    count++;
                    parsed = statement(tokens, count, node.op3);
                    if (!parsed) return false;
                }
            }
            case WHILE -> {
                 count++;
                 node.type = NodeTypes.WHILE;
                 parsed = parenExpr(tokens, count, node.op1);
                 if (!parsed) return false;
                 parsed = statement(tokens, count, node.op2);
                 if (!parsed) return false;
            }
            case LBRA -> {
                count++;
                while (count < tokens.size() && tokens.get(count).type != TokenTypes.RBRA) {
                    node = new Node(NodeTypes.SEQ, 0, node, null, null);
                    parsed = statement(tokens, count, node.op2);
                    if (!parsed) return false;
                }
                count++;
            }
            case SEMICOL -> count++;
            case PRINT -> {
                count++;
                node.type = NodeTypes.PRINT;
                parsed = parenExpr(tokens, count, node.op1);
                if (!parsed) return false;
            }
            default -> {
                node.type = NodeTypes.EXPR;
                parsed = expr(tokens, count, node.op1);
                if (tokens.get(count++).type != TokenTypes.SEMICOL) return false;
                if (!parsed) return false;
            }
        }
        return true;
    }

    private static boolean parenExpr(List<Token> tokens, int count, Node node) {
        Token token = tokens.get(count);
        if (token.type != TokenTypes.LPAR) {
            return false;
        }
        count ++;
        expr(tokens, count, node);
        if (token.type != TokenTypes.RPAR) {
            return false;
        }
        count ++;
        return true;
    }

    private static boolean expr(List<Token> tokens, int count, Node node) {
        Token token = tokens.get(count);
        if (token.type != TokenTypes.VAR) {
            return test(tokens, count, node);
        }
        boolean result = test(tokens, count, node);
        if (!result) return false;

        if (node.type == NodeTypes.VAR && token.type == TokenTypes.ASSIG) {
            node = new Node(NodeTypes.SET, 0, node, null, null);
            count++;
            result = expr(tokens, count, node.op2);
        }
        return result;
    }

    private static boolean test(List<Token> tokens, int count, Node node) {

        boolean result = arExpr(tokens, count, node);
        if (!result) return false;
        Token token = tokens.get(count);
        if (token.type != TokenTypes.LESS) {
            count++;
            node = new Node(NodeTypes.LESSTHEN, 0, node, null, null);
            result = arExpr(tokens, count, node.op2);
        }

        return result;
    }

    private static boolean arExpr(List<Token> tokens, int count, Node node) {

        boolean res = term(tokens, count, node);
        if (!res) return false;
        TokenTypes currType = tokens.get(count).type;
        while (currType == TokenTypes.DIV
                || currType == TokenTypes.SUB
                || currType == TokenTypes.ADD
                || currType == TokenTypes.MUL) {
            NodeTypes nodeType = null;
            switch (currType) {
                case DIV -> nodeType = NodeTypes.DIV;
                case SUB -> nodeType = NodeTypes.SUB;
                case MUL -> nodeType = NodeTypes.MUL;
                case ADD -> nodeType = NodeTypes.ADD;
                default -> nodeType = NodeTypes.EMPTY;
            }

            // the arithmetical expression has 2 child
            // op1 - first operand, op2 - second operand
            // by calling term firstly we migrate our node type to var or const
            // after that we generate new node (with arType) and assign it as parent of firstly term
            // op1 - will be the calculated term firstly
            node = new Node(nodeType, 0, node, null, null);
            count ++;
            // we need to push new node on a leaf and assign it as second operand and push it to op2
            boolean result = term(tokens, count, node.op2);
            if (!result) return false;
            currType = tokens.get(count).type;
        }

        return true;
    }

    // the leaves of our syntax tree
    private static boolean term(List<Token> tokens, int count, Node node) {

        Token token = tokens.get(count);
        if (token.type == TokenTypes.VAR) {
            node = new Node(NodeTypes.VAR, token.variable - 'a');
            count++;
        } else if (token.type == TokenTypes.NUM) {
            node = new Node(NodeTypes.CONST, token.value);
            count++;
        } else {
            // expression case
            return parenExpr(tokens, count, node);
        }
        return true;
    }
}
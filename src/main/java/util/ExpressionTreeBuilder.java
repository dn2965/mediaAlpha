package util;

import java.util.List;
import java.util.Stack;
import model.NumberOperator;
import model.TreeNode;

public class ExpressionTreeBuilder {

    static final String LEFT_PARENTHESIS = "(";
    static final String RIGHT_PARENTHESIS = ")";

    public TreeNode buildExpressionTree(final String expression) {
        final List<String> expressionUnitList = InputTokenizeParser.parse(expression);

        final Stack<TreeNode> nodes = new Stack<>();
        final Stack<String> operators = new Stack<>();

        for (final String expressionUnit : expressionUnitList) {
            if (isOperator(expressionUnit)) {
                operators.push(expressionUnit);
                continue;
            }
            if (expressionUnit.equals(LEFT_PARENTHESIS)) {
                operators.push(expressionUnit);
                continue;
            }
            if (expressionUnit.equals(RIGHT_PARENTHESIS)) {
                while (!operators.isEmpty() && !operators.peek().equals(LEFT_PARENTHESIS)) {
                    nodes.push(createNode(nodes, operators));
                }
                operators.pop();
                continue;
            }

            nodes.push(new TreeNode(expressionUnit));
        }

        while (!operators.isEmpty()) {
            TreeNode newNode = createNode(nodes, operators);
            nodes.push(newNode);
        }
        return nodes.pop();
    }

    private TreeNode createNode(final Stack<TreeNode> nodes, final Stack<String> operators) {
        final TreeNode right = nodes.pop();
        final String operator = operators.pop();
        final TreeNode left = nodes.pop();

        final TreeNode node = new TreeNode(operator);
        node.setOperandLeft(left);
        node.setOperandRight(right);
        return node;
    }

    private boolean isOperator(String token) {
        return NumberOperator.findBySign(token) != null;
    }

}
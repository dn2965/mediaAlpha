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

        for (int i = 0; i < expressionUnitList.size(); i++) {
            final String expressionUnit = expressionUnitList.get(i);
            if (isOperator(expressionUnit)) {
                operators.push(expressionUnit);
                continue;
            }
            if (expressionUnit.equals(LEFT_PARENTHESIS)) {
                operators.push(expressionUnit);
                continue;
            }
            if (expressionUnit.equals(RIGHT_PARENTHESIS) && !operators.isEmpty() && operators.peek().equals(LEFT_PARENTHESIS)) {
                operators.pop();
                if (!operators.isEmpty() && operators.peek().equals("-")) {
                    final TreeNode currentNode = nodes.pop();
                    final String expressionData = currentNode.getExpressionData();
                    final boolean isUnaryMinus = i >= 4 && (expressionUnitList.get(i - 2).equals(RIGHT_PARENTHESIS) || expressionUnitList.get(i - 2).equals(LEFT_PARENTHESIS)) && isOperator(expressionUnitList.get(i - 3)) && expressionUnitList.get(i - 4).equals(RIGHT_PARENTHESIS);
                    final boolean isUnaryMinusBeforeLeftParenthesis = i > 2 && (expressionUnitList.get(i - 2).equals(RIGHT_PARENTHESIS) || expressionUnitList.get(i - 2).equals(LEFT_PARENTHESIS));
                    handleApplyUnaryMinus(operators, nodes, expressionData, isUnaryMinus, isUnaryMinusBeforeLeftParenthesis);
                }
                continue;
            }
            if (expressionUnit.equals(RIGHT_PARENTHESIS)) {
                while (!operators.isEmpty() && !operators.peek().equals(LEFT_PARENTHESIS)) {
                    nodes.push(createNode(nodes, operators));
                }
                if (!operators.isEmpty()) {
                    operators.pop();
                }
                continue;
            }

            nodes.push(new TreeNode(expressionUnit));
        }

        while (!operators.isEmpty()) {
            TreeNode newNode = createNode(nodes, operators);
            nodes.push(newNode);
        }
        if (nodes.isEmpty()) {
            return new TreeNode("");
        }
        return nodes.pop();
    }

    private void handleApplyUnaryMinus(final Stack<String> operators, final Stack<TreeNode> nodes, final String expressionData, final boolean isUnaryMinus, final boolean isUnaryMinusBeforeLeftParenthesis) {
        operators.pop(); // pop  "-"
        if (operators.isEmpty()) {
            nodes.push(new TreeNode("-" + expressionData));
        } else if (operators.peek().equals(LEFT_PARENTHESIS)) {
            if (isUnaryMinus) {
                operators.push("-");
                nodes.push(new TreeNode(expressionData));
            } else {
                if (isUnaryMinusBeforeLeftParenthesis) {
                    nodes.push(new TreeNode("-" + expressionData));
                } else {
                    operators.push("-");
                    nodes.push(new TreeNode(expressionData));
                }
            }
        } else if (isOperator(operators.peek())) {
            nodes.push(new TreeNode("-" + expressionData));
        } else {
            operators.push("-"); // 還原
            nodes.push(new TreeNode(expressionData));
        }
    }


    private TreeNode createNode(final Stack<TreeNode> nodes, final Stack<String> operators) {
        final TreeNode right = nodes.pop();
        final String operator = operators.pop();
        final TreeNode node = new TreeNode(operator);
        node.setOperandRight(right);
        if (!nodes.isEmpty()) {
            final TreeNode left = nodes.pop();
            node.setOperandLeft(left);
        }
        return node;
    }

    private boolean isOperator(String token) {
        return NumberOperator.findBySign(token) != null;
    }

}
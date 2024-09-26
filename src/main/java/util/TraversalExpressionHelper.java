package util;


import static util.ExpressionTreeBuilder.LEFT_PARENTHESIS;
import static util.ExpressionTreeBuilder.RIGHT_PARENTHESIS;

import model.NumberOperator;
import model.TreeNode;

public class TraversalExpressionHelper {

    private static final String DEFAULT_OPERATOR_OUTSIDE = "";

    public static String inorderTraversalExpression(final TreeNode node) {
        return inorderTraversalExpression(node, DEFAULT_OPERATOR_OUTSIDE);
    }

    // inorder traversal, and deal with no parenthesis cases
    public static String inorderTraversalExpression(final TreeNode currentNode, final String operatorOutsideString) {
        if (currentNode == null) {
            return "";
        }

        final String left = inorderTraversalExpression(currentNode.getOperandLeft(), currentNode.getExpressionData());
        final String right = inorderTraversalExpression(currentNode.getOperandRight(), currentNode.getExpressionData());

        if (left.isEmpty() && right.isEmpty()) {
            return currentNode.getExpressionData();
        }

        final StringBuilder result = new StringBuilder();

        if (operatorOutsideString == null || operatorOutsideString.isEmpty()) {
            return result.append(left).append(currentNode.getExpressionData()).append(right).toString();
        }

        final NumberOperator nodeOperator = NumberOperator.findBySign(currentNode.getExpressionData());
        final NumberOperator leftNodeOperator = NumberOperator.findBySign(currentNode.getOperandLeft().getExpressionData());
        final NumberOperator rightNodeOperator = NumberOperator.findBySign(currentNode.getOperandRight().getExpressionData());

        final int priorityOfOutsideOperator = (NumberOperator.findBySign(operatorOutsideString) != null) ? NumberOperator.findBySign(operatorOutsideString).getPriority() : Integer.MIN_VALUE;
        final int priorityOfCurrentNodeOperator = (nodeOperator != null) ? nodeOperator.getPriority() : Integer.MIN_VALUE;

        if (leftNodeOperator != null && rightNodeOperator != null) {
            if (leftNodeOperator.getPriority() < priorityOfOutsideOperator) {
                result.append(LEFT_PARENTHESIS).append(left).append(RIGHT_PARENTHESIS);
            } else {
                result.append(left);
            }
            result.append(currentNode.getExpressionData());
            if (rightNodeOperator.getPriority() < priorityOfCurrentNodeOperator) {
                result.append(LEFT_PARENTHESIS).append(right).append(RIGHT_PARENTHESIS);
            } else {
                result.append(right);
            }
        } else if (priorityOfOutsideOperator > priorityOfCurrentNodeOperator) {
            result.append(LEFT_PARENTHESIS).append(left).append(currentNode.getExpressionData()).append(right).append(RIGHT_PARENTHESIS);
        } else {
            result.append(left).append(currentNode.getExpressionData()).append(right);
        }
        return result.toString();
    }

    public static void main(final String[] args) {
        final ExpressionTreeBuilder builder = new ExpressionTreeBuilder();
        final TreeNode root = builder.buildExpressionTree("1 * (-1 * -b) - 3");
        System.out.println(inorderTraversalExpression(root, ""));
    }
}

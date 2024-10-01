package util;


import static util.ExpressionTreeBuilder.LEFT_PARENTHESIS;
import static util.ExpressionTreeBuilder.RIGHT_PARENTHESIS;

import model.NumberOperator;
import model.TreeNode;

public class TraversalExpressionHelper {

    private static final String DEFAULT_OPERATOR_OUTSIDE = "";

    public static String traversalExpression(final TreeNode node) {
        return traversalExpression(node, DEFAULT_OPERATOR_OUTSIDE);
    }

    public static String traversalExpression(final TreeNode currentNode, final String operatorOutsideString) {
        if (currentNode == null) {
            return "";
        }

        final String left = traversalExpression(currentNode.getOperandLeft(), currentNode.getExpressionData());
        final String right = traversalExpression(currentNode.getOperandRight(), currentNode.getExpressionData());

        if (left.isEmpty() && right.isEmpty()) {
            return currentNode.getExpressionData();
        }

        final StringBuilder result = new StringBuilder();

        if (operatorOutsideString == null || operatorOutsideString.isEmpty()) {
            return result.append(left).append(currentNode.getExpressionData()).append(right).toString();
        }

        final NumberOperator nodeOperator = NumberOperator.findBySign(currentNode.getExpressionData());
        final NumberOperator leftNodeOperator = NumberOperator.findBySign(currentNode.getOperandLeft()==null?"":currentNode.getOperandLeft().getExpressionData());
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
        } else if (nodeOperator == NumberOperator.ADDITION && NumberOperator.findBySign(operatorOutsideString) == NumberOperator.SUBTRACTION) {
            result.append(LEFT_PARENTHESIS).append(left).append(currentNode.getExpressionData()).append(right).append(RIGHT_PARENTHESIS);
        } else if (nodeOperator == NumberOperator.SUBTRACTION && NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION) {
            result.append(LEFT_PARENTHESIS).append(left).append(currentNode.getExpressionData()).append(right).append(RIGHT_PARENTHESIS);
        } else {
            result.append(left).append(currentNode.getExpressionData()).append(right);
        }
        return result.toString();
    }

    public static void main(final String[] args) {
        final ExpressionTreeBuilder builder = new ExpressionTreeBuilder();
        final TreeNode root = builder.buildExpressionTree("(-1)+((-1)-(2))");
        System.out.println(traversalExpression(root, root.getExpressionData()));

    }
}

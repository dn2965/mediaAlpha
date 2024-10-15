package util;

import static model.NumberOperator.SUBTRACTION;

import lombok.extern.slf4j.Slf4j;
import model.NumberOperator;
import model.TreeNode;

@Slf4j
public class TraversalExpressionHelper {

    private static final String DEFAULT_OPERATOR_OUTSIDE = "";

    public static String traversalExpression(final TreeNode node) {
        return traversalExpression(node, false, DEFAULT_OPERATOR_OUTSIDE);
    }

    public static String traversalExpression(final TreeNode currentNode, final boolean isLeft, final String operatorOutsideString) {

        final NumberOperator nodeOperator = NumberOperator.findBySign(currentNode.getTrimmedValue());
        final int priorityOfOutsideOperator = (NumberOperator.findBySign(operatorOutsideString) != null) ? NumberOperator.findBySign(operatorOutsideString).getPriority() : Integer.MIN_VALUE;
        final int priorityOfCurrentNodeOperator = (nodeOperator != null) ? nodeOperator.getPriority() : Integer.MIN_VALUE;

        final StringBuilder result = new StringBuilder();
        if (currentNode.getOperandLeft() == null && currentNode.getOperandRight() == null) {
            if (!currentNode.isWrappedByParenthesis()) {
                return currentNode.getExpressionData();
            }
            final boolean isNegative = currentNode.getTrimmedValue().startsWith(SUBTRACTION.getSign());
            final boolean isAddition = NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION;
            final boolean isMultiplication = NumberOperator.findBySign(operatorOutsideString) == NumberOperator.MULTIPLICATION;
            final boolean isDivision = NumberOperator.findBySign(operatorOutsideString) == NumberOperator.DIVISION;

            if ((isAddition && !isNegative) || (isMultiplication && isNegative) || (isDivision) || (isLeft || operatorOutsideString.isEmpty())) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(currentNode.getExpressionData()).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            } else {
                result.append(currentNode.getLeftParenthesis()).append(currentNode.getExpressionData()).append(currentNode.getRightParenthesis());
            }
            return result.toString();
        }

        final String left = traversalExpression(currentNode.getOperandLeft(), true, currentNode.getTrimmedValue());
        final String right = traversalExpression(currentNode.getOperandRight(), false, currentNode.getTrimmedValue());

        if (operatorOutsideString == null || operatorOutsideString.isEmpty()) { // 已經執行到最外圍的節點或原始就只有這麼少的節點
            if (currentNode.getOperandLeft().getOperandLeft() != null && nodeOperator == NumberOperator.findBySign(currentNode.getOperandLeft().getTrimmedValue())) {
                result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getRightParenthesis());
            } else {
                if (operatorOutsideString.isEmpty()) {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                }
            }
            result.append(currentNode.getExpressionData());

            if ((nodeOperator == NumberOperator.ADDITION) && currentNode.getOperandRight().getTrimmedValue().startsWith(SUBTRACTION.getSign()) && (currentNode.getOperandRight().getOperandRight() == null && currentNode.getOperandRight().getOperandLeft() == null)) {

                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());

            } else if ((nodeOperator == NumberOperator.SUBTRACTION)
                && currentNode.getOperandRight().getTrimmedValue().startsWith(SUBTRACTION.getSign())
                && (currentNode.getOperandRight().getOperandRight() == null && currentNode.getOperandRight().getOperandLeft() == null && currentNode.getOperandRight().isWrappedByParenthesis())
            ) {
                result.append(right);
            } else if ((nodeOperator == NumberOperator.SUBTRACTION) && currentNode.getOperandRight().getTrimmedValue().startsWith(SUBTRACTION.getSign()) && (currentNode.getOperandRight().getOperandRight() == null && currentNode.getOperandRight().getOperandLeft() == null)) {
                result.append(currentNode.getOperandRight().getLeftParenthesis()).append(right).append(currentNode.getOperandRight().getRightParenthesis());
            } else if (currentNode.getOperandRight().getOperandRight() != null
                && nodeOperator == NumberOperator.findBySign(currentNode.getOperandRight().getTrimmedValue())
                && currentNode.getOperandRight().isWrappedByParenthesis() && nodeOperator != NumberOperator.ADDITION) {

                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());

            } else {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            }
            return result.toString();
        }

        if (isComposedNode(currentNode)) {

            // for case
            // 1 - (2 - 3)
            if (NumberOperator.findBySign(operatorOutsideString) == nodeOperator && (nodeOperator == NumberOperator.SUBTRACTION || nodeOperator == NumberOperator.DIVISION)) {
                result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                return result.toString();
            }

            if ((NumberOperator.findBySign(operatorOutsideString) == NumberOperator.SUBTRACTION || NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION) && left.startsWith(SUBTRACTION.getSign())) {
                result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                return result.toString();
            }

            if (NumberOperator.findBySign(operatorOutsideString) == nodeOperator) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                return result.toString();
            }

            if ((NumberOperator.findBySign(operatorOutsideString) == NumberOperator.MULTIPLICATION && nodeOperator == NumberOperator.DIVISION) || nodeOperator == NumberOperator.MULTIPLICATION && NumberOperator.findBySign(operatorOutsideString) == NumberOperator.DIVISION) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                return result.toString();
            }

            if (priorityOfOutsideOperator == priorityOfCurrentNodeOperator && NumberOperator.findBySign(operatorOutsideString) != nodeOperator && currentNode.isWrappedByParenthesis()) {
                result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                return result.toString();
            }

            if ((NumberOperator.findBySign(operatorOutsideString) == NumberOperator.SUBTRACTION || NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION) && (nodeOperator == NumberOperator.DIVISION || nodeOperator == NumberOperator.MULTIPLICATION)) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            } else {
                result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
            }
            return result.toString();

        } else if (currentNode.getOperandLeft() != null
            && currentNode.getOperandLeft().getOperandLeft() == null
            && currentNode.getOperandLeft().getOperandRight() == null
            && currentNode.getOperandRight() != null
            && (currentNode.getOperandRight().getOperandLeft() != null || currentNode.getOperandRight().getOperandRight() != null)) {
            if (priorityOfOutsideOperator == priorityOfCurrentNodeOperator) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                return result.toString();
            }
            if ((NumberOperator.findBySign(operatorOutsideString) == NumberOperator.SUBTRACTION || NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION)
                && (nodeOperator == NumberOperator.DIVISION || nodeOperator == NumberOperator.MULTIPLICATION)
            ) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            } else {
                result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
            }
        } else {
            result.append(left).append(currentNode.getExpressionData()).append(right);
        }
        return result.toString();
    }

    private static boolean isComposedNode(final TreeNode currentNode) {
        return currentNode.getOperandLeft() != null
            && currentNode.getOperandLeft().getOperandLeft() == null
            && currentNode.getOperandLeft().getOperandRight() == null
            && currentNode.getOperandRight() != null
            && currentNode.getOperandRight().getOperandLeft() == null
            && currentNode.getOperandRight().getOperandRight() == null;
    }

    public static void main(final String[] args) {
        final ExpressionTreeBuilder builder = new ExpressionTreeBuilder();
        final TreeNode root = builder.buildExpressionTree("2*(3/5)");
        log.debug(traversalExpression(root));

        System.out.println(1 - (2 + 3));
        System.out.println(1 - 2 + 3);

    }
}

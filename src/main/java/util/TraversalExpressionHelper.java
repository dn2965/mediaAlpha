package util;

import static model.NumberOperator.SUBTRACTION;

import lombok.extern.slf4j.Slf4j;
import model.NumberOperator;
import model.TreeNode;

@Slf4j
public class TraversalExpressionHelper {

    private static final String DEFAULT_OPERATOR_OUTSIDE = "";

    public static String traversalExpression(final TreeNode node) {
        return traversalExpression(node, false, "", DEFAULT_OPERATOR_OUTSIDE);
    }

    public static String traversalExpression(final TreeNode currentNode, final boolean isLeft, final String oneUpOperator, final String operatorOutsideString) {

        final NumberOperator nodeOperator = NumberOperator.findBySign(currentNode.getTrimmedValue());
        final NumberOperator oneUpNumberOperator = NumberOperator.findBySign(oneUpOperator);
        final int priorityOfOutsideOperator = (NumberOperator.findBySign(operatorOutsideString) != null) ? NumberOperator.findBySign(operatorOutsideString).getPriority() : Integer.MIN_VALUE;
        final int priorityOfCurrentNodeOperator = (nodeOperator != null) ? nodeOperator.getPriority() : Integer.MIN_VALUE;

        final StringBuilder result = new StringBuilder();
        if (currentNode.getOperandLeft() == null && currentNode.getOperandRight() == null) {
            if (!currentNode.isWrappedByParenthesis()) {
                return currentNode.getExpressionData();
            }
            final boolean isNegative = currentNode.getTrimmedValue().startsWith(SUBTRACTION.getSign());
            final boolean isOperatorOutSideAddition = NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION;
            final boolean isOperatorOutSideMultiplication = NumberOperator.findBySign(operatorOutsideString) == NumberOperator.MULTIPLICATION;
            final boolean isOperatorOutSideDivision = NumberOperator.findBySign(operatorOutsideString) == NumberOperator.DIVISION;

            if (currentNode.isWrappedByParenthesis() && currentNode.getTrimmedValue().startsWith(SUBTRACTION.getSign()) && isOperatorOutSideAddition) {
                if (isLeft && isNegative && (oneUpNumberOperator == SUBTRACTION || oneUpNumberOperator == NumberOperator.ADDITION)) {
                    result.append(currentNode.getLeftParenthesis()).append(currentNode.getExpressionData()).append(currentNode.getRightParenthesis());
                } else if (isLeft && oneUpOperator != null && oneUpNumberOperator != null && oneUpNumberOperator.getPriority() > priorityOfCurrentNodeOperator) {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(currentNode.getExpressionData()).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                } else if (isOperatorOutSideAddition && oneUpOperator.equals("") && isLeft) {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(currentNode.getExpressionData()).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                } else {
                    result.append(currentNode.getLeftParenthesis()).append(currentNode.getExpressionData()).append(currentNode.getRightParenthesis());
                }
            } else if ((isOperatorOutSideAddition && !isNegative) || (isOperatorOutSideMultiplication && isNegative) || (isOperatorOutSideDivision) || (isLeft || operatorOutsideString.isEmpty())) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(currentNode.getExpressionData()).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            } else {
                result.append(currentNode.getLeftParenthesis()).append(currentNode.getExpressionData()).append(currentNode.getRightParenthesis());
            }
            return result.toString();
        }

        final String left = traversalExpression(currentNode.getOperandLeft(), true, operatorOutsideString, currentNode.getTrimmedValue());
        final String right = traversalExpression(currentNode.getOperandRight(), false, operatorOutsideString, currentNode.getTrimmedValue());

        final boolean hasOutSideOperator = operatorOutsideString == null || operatorOutsideString.isEmpty();
        if (hasOutSideOperator) { // 已經執行到最外圍的節點或原始就只有這麼少的節點

            result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            result.append(currentNode.getExpressionData());

            if ((nodeOperator == NumberOperator.ADDITION) && currentNode.getOperandRight().getTrimmedValue().startsWith(SUBTRACTION.getSign()) && (currentNode.getOperandRight().getOperandRight() == null && currentNode.getOperandRight().getOperandLeft() == null)) {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            } else if ((nodeOperator == NumberOperator.SUBTRACTION)
                && currentNode.getOperandRight().getTrimmedValue().startsWith(SUBTRACTION.getSign())
                && (currentNode.getOperandRight().getOperandRight() == null && currentNode.getOperandRight().getOperandLeft() == null && currentNode.getOperandRight().isWrappedByParenthesis())
            ) {
                result.append(right);
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
            if (NumberOperator.findBySign(operatorOutsideString) == nodeOperator && nodeOperator == NumberOperator.DIVISION) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                return result.toString();
            }

            if (NumberOperator.findBySign(operatorOutsideString) == nodeOperator && (nodeOperator == NumberOperator.SUBTRACTION)) {
                result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                return result.toString();
            }

            if ((NumberOperator.findBySign(operatorOutsideString) == NumberOperator.SUBTRACTION || NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION) && left.startsWith(SUBTRACTION.getSign())) {
                // for case
                // new ExpressionTestCase("(-1+2)-3", "-1+2-3"),
                if (oneUpOperator.equals("") && isLeft && (nodeOperator == NumberOperator.SUBTRACTION || nodeOperator == NumberOperator.ADDITION)) {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                } else {
                    result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                }
                return result.toString();
            }

            if (NumberOperator.findBySign(operatorOutsideString) == nodeOperator) {
                if (isLeft && oneUpNumberOperator != null && oneUpNumberOperator.getPriority() > priorityOfCurrentNodeOperator) {
                    result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                } else {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                }
                return result.toString();
            }

            if ((NumberOperator.findBySign(operatorOutsideString) == NumberOperator.MULTIPLICATION && nodeOperator == NumberOperator.DIVISION) || nodeOperator == NumberOperator.MULTIPLICATION && NumberOperator.findBySign(operatorOutsideString) == NumberOperator.DIVISION) {

                if (!isLeft && nodeOperator != NumberOperator.findBySign(operatorOutsideString) && NumberOperator.findBySign(operatorOutsideString) == NumberOperator.DIVISION) {
                    result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                } else {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                }
                return result.toString();
            }

            if (priorityOfOutsideOperator == priorityOfCurrentNodeOperator && NumberOperator.findBySign(operatorOutsideString) != nodeOperator && currentNode.isWrappedByParenthesis()) {
                if (oneUpOperator.equals("") && isLeft) {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                } else {
                    result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                }
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

            if (priorityOfOutsideOperator == priorityOfCurrentNodeOperator && NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION) {
                if (isLeft && oneUpOperator != null && oneUpNumberOperator != null && oneUpNumberOperator.getPriority() > priorityOfCurrentNodeOperator) {
                    result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                } else {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                }
            } else if (isLeft && (NumberOperator.findBySign(operatorOutsideString) == NumberOperator.DIVISION || NumberOperator.findBySign(operatorOutsideString) == NumberOperator.MULTIPLICATION)
                && (nodeOperator == NumberOperator.DIVISION || nodeOperator == NumberOperator.MULTIPLICATION)
            ) {
                if (isLeft && (NumberOperator.findBySign(operatorOutsideString) == NumberOperator.MULTIPLICATION && nodeOperator == NumberOperator.DIVISION)) {
                    result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
                } else {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                }
            } else if ((NumberOperator.findBySign(operatorOutsideString) == NumberOperator.SUBTRACTION || NumberOperator.findBySign(operatorOutsideString) == NumberOperator.ADDITION)
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
        final TreeNode root = builder.buildExpressionTree("1 + (-2) + 3");
        // new ExpressionTestCase("1*(2+(3*(4+5)))", "1*(2+3*(4+5))"),
//        new ExpressionTestCase("(2*(3+4)*5)/6", "2*(3+4)*5/6")
        log.debug(traversalExpression(root));
        System.out.println(((2f / -1f + (1f / 2)) * (3f * 4) * 5f)); //2/-1+1/2
        System.out.println(((2f / -1f + (1f / 2)) * 3f * 4 * 5f)); //2/-1+1/2

    }
}

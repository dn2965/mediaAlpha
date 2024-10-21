package util;

import static model.NumberOperator.ADDITION;
import static model.NumberOperator.DIVISION;
import static model.NumberOperator.MULTIPLICATION;
import static model.NumberOperator.SUBTRACTION;

import lombok.extern.slf4j.Slf4j;
import model.NumberOperator;
import model.TreeNode;

@Slf4j
public class TraversalExpressionHelper {

    private static final String DEFAULT_OPERATOR_OUTSIDE = "";

    public static String traversalExpression(final TreeNode node) {
        return traversalExpression(node, null, node, false, "", DEFAULT_OPERATOR_OUTSIDE);
    }

    private static TreeNode findGrandparentNodeHelper(final TreeNode current, final TreeNode targetNode, final TreeNode parent, final TreeNode grandparent) {
        if (current == null) {
            return null;
        }

        if (current == targetNode) {
            return grandparent;
        }

        final TreeNode leftResult = findGrandparentNodeHelper(current.getOperandLeft(), targetNode, current, parent);
        if (leftResult != null) {
            return leftResult;
        }

        return findGrandparentNodeHelper(current.getOperandRight(), targetNode, current, parent);
    }

    public static TreeNode findGrandparentNode(final TreeNode root, final TreeNode targetNode) {
        return findGrandparentNodeHelper(root, targetNode, null, null);
    }

    public static String traversalExpression(final TreeNode rootNode, final TreeNode parentNode, final TreeNode currentNode, final boolean isLeft, final String oneUpOperator, final String operatorOutsideString) {

        final TreeNode grandparentNode = findGrandparentNode(rootNode, currentNode);
        final NumberOperator grandparentNodeOperator = grandparentNode != null ? NumberOperator.findBySign(grandparentNode.getTrimmedValue()) : null;
        final boolean isGrandparentNodeUnary = grandparentNode != null && grandparentNode.isUnary();
        final boolean isParentNodeUnary = grandparentNode != null && parentNode.isUnary();
        final NumberOperator parentNodeOperator = NumberOperator.findBySign(parentNode == null ? "" : parentNode.getTrimmedValue());
        final NumberOperator nodeOperator = NumberOperator.findBySign(currentNode.getTrimmedValue());
        final NumberOperator oneUpNumberOperator = NumberOperator.findBySign(oneUpOperator);
        final NumberOperator outsideOperator = NumberOperator.findBySign(operatorOutsideString);

        if (currentNode.getOperandLeft() == null && currentNode.getOperandRight() == null && !currentNode.isWrappedByParenthesis()) {
            return currentNode.getExpressionData();
        }

        final StringBuilder result = new StringBuilder();

        final String left = currentNode.getOperandLeft() == null ? "" : traversalExpression(rootNode, currentNode, currentNode.getOperandLeft(), true, operatorOutsideString, currentNode.getTrimmedValue());
        final String right = currentNode.getOperandRight() == null ? "" : traversalExpression(rootNode, currentNode, currentNode.getOperandRight(), false, operatorOutsideString, currentNode.getTrimmedValue());

        final boolean isOutSideOperator = operatorOutsideString == null || operatorOutsideString.isEmpty();
        if (isOutSideOperator) { // 已經執行到最外圍的節點或原始就只有這麼少的節點
            return handleEndNode(currentNode, result, left, right);
        }
        if (currentNode.isWrappedByParenthesis()) {
            if (parentNode.isUnary()) {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
                return result.toString();
            } else {
                return handleWrappedByParenthesis(currentNode, isLeft, right, outsideOperator, result, oneUpNumberOperator, grandparentNodeOperator, parentNodeOperator, isParentNodeUnary, isGrandparentNodeUnary);
            }
        }

        final NumberOperator currentNodeRightNodeOperator = NumberOperator.findBySign(currentNode.getOperandRight().getTrimmedValue());
        if ((outsideOperator == NumberOperator.ADDITION || outsideOperator == SUBTRACTION) && (currentNodeRightNodeOperator == NumberOperator.MULTIPLICATION || currentNodeRightNodeOperator == NumberOperator.DIVISION)) {
            result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            return result.toString();
        } else if (isComposedNode(currentNode)) {
            if ((outsideOperator == NumberOperator.SUBTRACTION || outsideOperator == NumberOperator.ADDITION) && (nodeOperator == NumberOperator.DIVISION || nodeOperator == NumberOperator.MULTIPLICATION)) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            } else {
                result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
            }
            return result.toString();
        } else {
            result.append(currentNode.getLeftParenthesis()).append(left).append(currentNode.getExpressionData()).append(right).append(currentNode.getRightParenthesis());
        }
        return result.toString();
    }

    private static String handleWrappedByParenthesis(final TreeNode currentNode, final boolean isLeft, final String right, final NumberOperator outsideOperator, final StringBuilder result, final NumberOperator oneUpNumberOperator, final NumberOperator grandparentNodeOperator, final NumberOperator parentNodeOperator, final boolean isParentNodeUnary, final boolean isGrandparentNodeUnary) {
        final boolean isNegative = right.startsWith(SUBTRACTION.getSign());
        final NumberOperator currentNodeRightNodeOperator = NumberOperator.findBySign(currentNode.getOperandRight().getTrimmedValue());
        if (outsideOperator != null && currentNodeRightNodeOperator != null && outsideOperator.getPriority() > currentNodeRightNodeOperator.getPriority()) {
            result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
        } else if (outsideOperator == DIVISION && currentNodeRightNodeOperator == DIVISION) {
            if (isLeft) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            } else if (currentNode.getOperandRight().getOperandLeft() != null && currentNode.getOperandRight().getOperandRight() != null) {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            }
        } else if (outsideOperator == DIVISION && currentNodeRightNodeOperator == MULTIPLICATION) {
            if (isLeft) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            } else {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            }
        } else if (outsideOperator == ADDITION && currentNodeRightNodeOperator == SUBTRACTION) {
            result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
        } else if (outsideOperator == SUBTRACTION && currentNodeRightNodeOperator == ADDITION) {
            if (isLeft) {
                if (outsideOperator == SUBTRACTION && (currentNodeRightNodeOperator == ADDITION) && !isNegative) {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                } else if (oneUpNumberOperator == null) {
                    result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
                } else {
                    result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
                }
            } else {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            }
        } else if (isLeft && oneUpNumberOperator == null) {
            result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
        } else if (currentNodeRightNodeOperator == ADDITION && oneUpNumberOperator == MULTIPLICATION) {
            if (isLeft && grandparentNodeOperator == MULTIPLICATION && outsideOperator == ADDITION) {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            } else if (parentNodeOperator == ADDITION) {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            }
        } else if (currentNodeRightNodeOperator == ADDITION || currentNodeRightNodeOperator == SUBTRACTION) {
            if ((outsideOperator == MULTIPLICATION || outsideOperator == DIVISION) || isNegative) {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            } else if (currentNodeRightNodeOperator == SUBTRACTION && outsideOperator == SUBTRACTION) {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            } else {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            }
        } else if (isGrandparentNodeUnary) {
            if (currentNode.getOperandRight().getOperandLeft() != null && currentNode.getOperandRight().getOperandRight() != null) {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            } else {
                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            }
        } else if (!isLeft && !isNegative) {
            result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
        } else if (!isLeft && isNegative && (outsideOperator == ADDITION || outsideOperator == SUBTRACTION)) {
            result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
        } else if (outsideOperator == SUBTRACTION) {
            result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
        } else if (isLeft && isNegative) {
            result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
        } else if (currentNodeRightNodeOperator == MULTIPLICATION && (oneUpNumberOperator == ADDITION || oneUpNumberOperator == SUBTRACTION)) {
            result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
        } else if (currentNode.getOperandRight().getOperandRight() != null && NumberOperator.findBySign(currentNode.getOperandRight().getOperandRight().getTrimmedValue()) != null) {
            result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
        } else if (outsideOperator != ADDITION && isNegative) {
            result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
        } else if (NumberOperator.findBySign(currentNode.getOperandRight().getTrimmedValue()) == MULTIPLICATION) {
            if (oneUpNumberOperator == MULTIPLICATION || oneUpNumberOperator.getPriority() > oneUpNumberOperator.getPriority()) {

                result.append(currentNode.getLeftParenthesis().replaceAll("\\(", "")).append(right).append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
            } else {
                result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
            }
        } else {
            result.append(currentNode.getLeftParenthesis()).append(right).append(currentNode.getRightParenthesis());
        }
        return result.toString();
    }

    private static String handleEndNode(final TreeNode currentNode, final StringBuilder result, final String left, final String right) {
        if (currentNode.isWrappedByParenthesis() && !currentNode.isUnary()) {
            result.append(currentNode.getLeftParenthesis().replaceAll("\\(", ""));
        } else {
            result.append(currentNode.getLeftParenthesis());
        }

        result.append(left);
        result.append(currentNode.getExpressionData());
        result.append(right);
        if (currentNode.isWrappedByParenthesis() && !currentNode.isUnary()) {
            result.append(currentNode.getRightParenthesis().replaceAll("\\)", ""));
        } else {
            result.append(currentNode.getRightParenthesis());
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
        final TreeNode root = builder.buildExpressionTree("2 * 3 / 4");
        final String result = traversalExpression(root);
        log.info("[{}]", result);
        System.out.println(2f * (3f / 5f));
        System.out.println(2f * 3f / 5f);
    }
}

package util;

import static model.NumberOperator.SUBTRACTION;
import static util.InputUtil.toNormalize;

import java.util.List;
import java.util.Stack;
import lombok.extern.slf4j.Slf4j;
import model.NumberOperator;
import model.TreeNode;

@Slf4j
public class ExpressionTreeBuilder {

    static final String LEFT_PARENTHESIS = "(";
    static final String RIGHT_PARENTHESIS = ")";

    public TreeNode buildExpressionTree(final String expression) {
        final List<String> expressionUnitList = InputTokenizeParser.parse(expression); // 含空白
        final Stack<TreeNode> nodes = new Stack<>();
        final Stack<String> operators = new Stack<>();

        for (int i = 0; i < expressionUnitList.size(); i++) {
            final String expressionUnit = expressionUnitList.get(i);
            final String normalizeExpressionUnit = toNormalize(expressionUnit);

            if (isOperator(normalizeExpressionUnit)) {
                operators.push(expressionUnit);
                continue;
            }
            if (normalizeExpressionUnit.equals(LEFT_PARENTHESIS)) {
                operators.push(expressionUnit);
                continue;
            }

            if (normalizeExpressionUnit.equals(RIGHT_PARENTHESIS) && !operators.isEmpty() && toNormalize(operators.peek()).equals(LEFT_PARENTHESIS)) {
                final String operatorForPop = operators.pop();
                if (!operators.isEmpty() && toNormalize(operators.peek()).equals(SUBTRACTION.getSign())) {
                    final TreeNode currentNode = nodes.pop();
                    final String expressionData = currentNode.getExpressionData();

                    final boolean isUnaryMinus = i >= 4
                        && (toNormalize(expressionUnitList.get(i - 2)).equals(RIGHT_PARENTHESIS)
                        || toNormalize(expressionUnitList.get(i - 2)).equals(LEFT_PARENTHESIS))
                        && isOperator(toNormalize(expressionUnitList.get(i - 3)))
                        && toNormalize(expressionUnitList.get(i - 4)).equals(RIGHT_PARENTHESIS);

                    if (currentNode.getTrimmedValue().startsWith("-")) {
                        currentNode.setRightParenthesis(expressionUnit);
                        currentNode.setLeftParenthesis(operatorForPop);
                        currentNode.setWrappedByParenthesis(true);
                        nodes.push(currentNode);
                        continue;
                    } else {
                        handleApplyUnaryMinus(operators, nodes, expressionData, isUnaryMinus, operatorForPop, expressionUnit);
                        continue;
                    }
                } else {
                    operators.push(operatorForPop);
                    if (nodes.isEmpty()) {
                        continue;
                    }
                }
            }

            if (normalizeExpressionUnit.equals(RIGHT_PARENTHESIS)) {
                while (!operators.isEmpty() && !toNormalize(operators.peek()).equals(LEFT_PARENTHESIS)) {
                    nodes.push(createNode(nodes, operators));
                }
                final TreeNode topNode = nodes.peek();
                if (!operators.isEmpty()) {
                    topNode.setLeftParenthesis(operators.pop());
                }
                topNode.setRightParenthesis(expressionUnit);
                topNode.setWrappedByParenthesis(true);
                continue;
            }
            nodes.push(new TreeNode(expressionUnit));
        }

        while (!operators.isEmpty() && !nodes.isEmpty()) {
            TreeNode newNode = createNode(nodes, operators);
            nodes.push(newNode);
        }
        if (nodes.isEmpty()) {
            return new TreeNode("");
        }
        return nodes.pop();
    }

    private void handleApplyUnaryMinus(final Stack<String> operators, final Stack<TreeNode> nodes, final String expressionData, final boolean isUnaryMinus, final String left, final String right) {
        final String pop = operators.pop();// pop  "-"
        if (isUnaryMinus) {
            operators.push(pop);
            final TreeNode newNode = new TreeNode(expressionData);
            newNode.setLeftParenthesis(left);
            newNode.setRightParenthesis(right);
            nodes.push(newNode);
        } else {
            final TreeNode treeNode = new TreeNode(pop + expressionData);
            treeNode.setLeftParenthesis(left);
            treeNode.setRightParenthesis(right);
            nodes.push(treeNode);
        }
    }

    private TreeNode createNode(final Stack<TreeNode> nodes, final Stack<String> operators) {
        final TreeNode right = nodes.pop();
        final String operator = operators.pop();
        final TreeNode node = new TreeNode(operator);
        node.setOperandRight(right);
        final TreeNode left = nodes.pop();
        node.setOperandLeft(left);
        return node;
    }

    private boolean isOperator(String token) {
        return NumberOperator.findBySign(token) != null;
    }

    public static void main(final String[] args) {
        final ExpressionTreeBuilder builder = new ExpressionTreeBuilder();
        final TreeNode root = builder.buildExpressionTree("1 + (-2) * 3");
        log.debug("");

    }
}
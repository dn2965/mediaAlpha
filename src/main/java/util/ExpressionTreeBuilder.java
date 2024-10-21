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
        return buildExpressionTree(expressionUnitList, 0, expressionUnitList.size() - 1);
    }

    // Helper method to recursively parse and build the expression tree
    private TreeNode buildExpressionTree(final List<String> expressionUnitList, int start, int end) {
        final Stack<TreeNode> nodes = new Stack<>();
        final Stack<String> operators = new Stack<>();

        int i = start;
        while (i <= end) {
            final String expressionUnit = expressionUnitList.get(i);
            final String normalizeExpressionUnit = toNormalize(expressionUnit);

            if (isOperator(normalizeExpressionUnit)) {
                if (normalizeExpressionUnit.equals(SUBTRACTION.getSign())
                    && (i == 0 || isOperator(toNormalize(expressionUnitList.get(i - 1)))
                    || toNormalize(expressionUnitList.get(i - 1)).equals(LEFT_PARENTHESIS))) {
                    operators.push(String.format("UNARY_MINUS%s", expressionUnit));
                } else {
                    operators.push(expressionUnit);
                }
            } else if (normalizeExpressionUnit.equals(LEFT_PARENTHESIS)) {
                operators.push(expressionUnit);
            } else if (normalizeExpressionUnit.equals(RIGHT_PARENTHESIS)) {
                while (!operators.isEmpty() && !toNormalize(operators.peek()).equals(LEFT_PARENTHESIS)) {
                    final TreeNode newNode = createNode(nodes, operators);
                    nodes.push(newNode);
                }

                if (!operators.isEmpty() && toNormalize(operators.peek()).equals(LEFT_PARENTHESIS)) {
                    final String pop = operators.pop();// Pop the left parenthesis
                    if (!nodes.isEmpty()) {
                        final TreeNode subTree = nodes.pop(); // Subtree inside the parenthesis
                        final TreeNode parenNode = new TreeNode();
                        parenNode.setWrappedByParenthesis(true);
                        parenNode.setLeftParenthesis(pop);
                        parenNode.setRightParenthesis(expressionUnit);
                        parenNode.setOperandRight(subTree);

                        nodes.push(parenNode);
                    }
                }
            } else {
                nodes.push(new TreeNode(expressionUnit));
            }

            i++;
        }

        while (!operators.isEmpty()) {
            TreeNode newNode = createNode(nodes, operators);
            nodes.push(newNode);
        }

        return nodes.isEmpty() ? new TreeNode("") : nodes.pop();
    }

    private TreeNode createNode(final Stack<TreeNode> nodes, final Stack<String> operators) {
        final TreeNode right = nodes.pop();
        final String operator = operators.pop();

        if (operator.startsWith("UNARY_MINUS")) {
            TreeNode unaryNode = new TreeNode(operator.replaceAll("UNARY_MINUS", ""));
            unaryNode.setUnary(true);
            unaryNode.setOperandRight(right);
            return unaryNode;
        }

        final TreeNode node = new TreeNode(operator);
        node.setOperandRight(right);
        final TreeNode left = nodes.pop();
        node.setOperandLeft(left);
        return node;
    }

    private boolean isOperator(final String token) {
        return NumberOperator.findBySign(token) != null;
    }

    public static void main(final String[] args) {
        final ExpressionTreeBuilder builder = new ExpressionTreeBuilder();
        final TreeNode node = builder.buildExpressionTree("1+(-(2*3)+1)*4");
        log.debug("");
    }

}

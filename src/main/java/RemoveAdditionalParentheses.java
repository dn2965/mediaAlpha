import model.TreeNode;
import util.ExpressionTreeBuilder;
import util.TraversalExpressionHelper;

public class RemoveAdditionalParentheses {
    public static void main(String[] args) {

        final TreeNode expressionTree = new ExpressionTreeBuilder().buildExpressionTree("1*(2+(3*(4+5)))");
        final String result = TraversalExpressionHelper.inorderTraversalExpression(expressionTree);
        System.out.println(result);
    }
}
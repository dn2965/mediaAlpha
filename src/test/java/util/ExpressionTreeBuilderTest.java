package util;


import static org.junit.jupiter.api.Assertions.assertEquals;

import model.TreeNode;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ExpressionTreeBuilderTest {

    static Stream<ExpressionTestCase> expressionProvider() {
        return Stream.of(
            // cases, from james
            new ExpressionTestCase("1*(2+(3*(4+5)))", "1*(2+3*(4+5))"),
            new ExpressionTestCase("2 + (3 / -5)", "2 + 3 / -5"),
            new ExpressionTestCase("x+(y+z)+(t+(v+w))", "x+y+z+t+v+w"),

            new ExpressionTestCase(" ( 1 ) ", "  1  "),
            new ExpressionTestCase("(())", ""),
            new ExpressionTestCase("(((1)))", "1"),
            new ExpressionTestCase("(((1+2)))", "1+2"),
            new ExpressionTestCase("(((1*2)))", "1*2"),
            new ExpressionTestCase(" ( 1         ) + (           2 ) ", "  1          +            2  "),
            new ExpressionTestCase(" 1  *  ( 2 + ( 3 * ( 4 +  5 )))", " 1  *  ( 2 +  3 * ( 4 +  5 ))"),

            new ExpressionTestCase("(1)+(-1*2)", "1+(-1*2)"), // 保留括號
            new ExpressionTestCase("(1)+((-1)*2)", "1+(-1*2)"), // 有負號 -> 轉負數後、保留括號

            new ExpressionTestCase("1*(2+3*(4+5))", "1*(2+3*(4+5))"),
            new ExpressionTestCase("2+3/-5", "2+3/-5"),

            new ExpressionTestCase("(1 - 5)", "1 - 5"),
            new ExpressionTestCase("(-1 + 2)", "-1 + 2"),
            new ExpressionTestCase("- 5 - 1 ", "- 5 - 1 "),
            new ExpressionTestCase("1 - 5", "1 - 5"),
            new ExpressionTestCase("1 - (-1*b)-3", "1 - (-1*b)-3"),
            new ExpressionTestCase("2-(2+3)", "2-(2+3)"),
            new ExpressionTestCase("2+(2-3)", "2+(2-3)"),
            new ExpressionTestCase("1-(-1)", "1-(-1)"),
            new ExpressionTestCase("-1-(-1)", "-1-(-1)"),
            new ExpressionTestCase("1 - (2 - 3)", "1 - (2 - 3)"),
            new ExpressionTestCase("1 - (2 + 3)", "1 - (2 + 3)"),

            new ExpressionTestCase("(2 + 2) - ( - 5 + 1)", "2 + 2 - ( - 5 + 1)"),
            new ExpressionTestCase("1 - ( - 5 + 1)", "1 - ( - 5 + 1)"),

            new ExpressionTestCase("(2 + 2) * 1", "(2 + 2) * 1"),
            new ExpressionTestCase(" 2 + ( 3 *  - 5 ) ", " 2 +  3 *  - 5  "),
            new ExpressionTestCase(" 1   +  (1  + 2 ) ", " 1   +  1  + 2  "),
            new ExpressionTestCase(" 2 * ( 3 / 5 )", " 2 *  3 / 5 "),
            new ExpressionTestCase(" (  2  *  3)  /  5  ", "   2  *  3  /  5  "),

            new ExpressionTestCase("2 + (3 * -5)", "2 + 3 * -5"),
            new ExpressionTestCase("(2/3)/5", "2/3/5"), // 如果移除，  /  會有優先靠左運算原則，結果會不同
            new ExpressionTestCase("2/(3/5)", "2/(3/5)"), // 如果移除，  /  會有優先靠左運算原則，結果會不同
            new ExpressionTestCase("2*(3*5)", "2*3*5"), // 如果移除，  * 不會有影嚮可以移除
            new ExpressionTestCase("(2*3)*5", "2*3*5"),

            new ExpressionTestCase("1+(-1+2)-3", "1+(-1+2)-3"),
            new ExpressionTestCase("(-1+2)-3", "-1+2-3"),
            new ExpressionTestCase("(-1+2)+3", "-1+2+3"),
            new ExpressionTestCase("(-1+2)*3", "(-1+2)*3"),
            new ExpressionTestCase("(-1+2)/1", "(-1+2)/1"),

            new ExpressionTestCase("1+(-1)", "1+(-1)"),
            new ExpressionTestCase("(-1)-1", "-1-1"),
            new ExpressionTestCase("(-1)*1", "-1*1"),
            new ExpressionTestCase("(-1)/1", "-1/1"),

            new ExpressionTestCase("1*(-1)", "1*-1"),
            new ExpressionTestCase("1/(-1)", "1/-1"),
            new ExpressionTestCase(" 1 + ( 2 ) ", " 1 +  2  "),
            new ExpressionTestCase(" 1 + ( - 2 ) ", " 1 + ( - 2 ) "),

            new ExpressionTestCase(" ( - 1 ) - 1", "  - 1  - 1"),

            new ExpressionTestCase("2/(3*5)", "2/(3*5)"),
            new ExpressionTestCase("2/(3+5)", "2/(3+5)"),
            new ExpressionTestCase("2/(3-5)", "2/(3-5)"),

            new ExpressionTestCase("2*(3*5)", "2*3*5"),
            new ExpressionTestCase("2*(3+5)", "2*(3+5)"),
            new ExpressionTestCase("2*(3-5)", "2*(3-5)"),

            new ExpressionTestCase("2/(3*5)", "2/(3*5)"),
            new ExpressionTestCase("2*(3/5)", "2*3/5"),
            new ExpressionTestCase("(2*3)/5", "2*3/5"),
            new ExpressionTestCase("(2*3)/(5*8)", "2*3/(5*8)"),
            new ExpressionTestCase("(2+3)/(5+8)", "(2+3)/(5+8)"),

            new ExpressionTestCase("((1))", "1"),
            new ExpressionTestCase("(-5)", "-5"),
            new ExpressionTestCase("1 + (2 + (3 + 4))", "1 + 2 + 3 + 4"),
            new ExpressionTestCase("a * (b + (c * d))", "a * (b + c * d)"),
            new ExpressionTestCase("2 / (3 / 4)", "2 / (3 / 4)"),

            new ExpressionTestCase("1 - (2 - (3 - 4))", "1 - (2 - (3 - 4))"),
            new ExpressionTestCase("1 / (2 / (3 * 4))", "1 / (2 / (3 * 4))"),
            new ExpressionTestCase("1 + (-2) + 3", "1 + (-2) + 3"),

            new ExpressionTestCase("((1 + 2) + 3)", "1 + 2 + 3"),
            new ExpressionTestCase("(-1)+1", "-1+1"),
            new ExpressionTestCase("(-1)+((-1)-(2))", "-1+(-1-2)"),
            new ExpressionTestCase(" ( - 1 ) + (  ( - 1 ) -    2  )    ", "  - 1  + (   - 1  -    2  )    "),

            new ExpressionTestCase("  (   2 /  3  )       /  5         ", "     2 /  3         /  5         "),
            new ExpressionTestCase(" (  2   *   (  3 + 4 )              *   5             )     /    6", "   2   *   (  3 + 4 )              *   5                  /    6"),

            new ExpressionTestCase("a * -(b - c)", "a * -(b - c)"),
            new ExpressionTestCase("a - b * c + d / e", "a - b * c + d / e"),
            new ExpressionTestCase("(a + (b * (c - d))) / ((e - f) + g)", "(a + b * (c - d)) / ((e - f) + g)"),
            new ExpressionTestCase("a * (-b + c)", "a * (-b + c)"),
            new ExpressionTestCase("(-a) / (-b)", "-a / -b"),

            new ExpressionTestCase("((2*((2+3)-(4*6))+(8+(7*4))))", "2*(2+3-4*6)+8+7*4"),
            new ExpressionTestCase("((2*((2*3)-(4+6))+(8+(7*4))))", "2*(2*3-(4+6))+8+7*4"),
            new ExpressionTestCase("-(1)-123", "-1-123"), // 有負號 -> 轉負數後、保留括號
            new ExpressionTestCase("1+(-(1)*2)", "1+(-1*2)"), // 有負號 -> 轉負數後、保留括號
            new ExpressionTestCase("-(2+3)", "-(2+3)"),
            new ExpressionTestCase("1+(-(-(1*2)+1))*2", "1+(-(-(1*2)+1))*2"),
            new ExpressionTestCase("1+(-(-((-2)*3)+4))*5", "1+(-(-(-2*3)+4))*5"),
            new ExpressionTestCase("1+(-1+2)-3+1*(99+(88*(77+66)))+1", "1+(-1+2)-3+1*(99+88*(77+66))+1"),
            new ExpressionTestCase("5/((-2/-1+(2)))", "5/(-2/-1+2)"),
            new ExpressionTestCase("1+(-1+2)-3+1*(2+(3*(4+5)))+(2*(3+4)*5)/6+(2/3)/5/((-2/-1+(1/12))*(3*4)*5+(3*4)*5*(2/-1+(1/2))*(3*4)*5)+x+(y+z)+(t+(v+w))+2 ", "1+(-1+2)-3+1*(2+3*(4+5))+2*(3+4)*5/6+2/3/5/(-2/-1+1/12*3*4*5+3*4*5*(2/-1+1/2)*3*4*5)+x+y+z+t+v+w+2 "),

            new ExpressionTestCase("(a / (b * (c + d))) - e", "a / (b * (c + d)) - e"),
            new ExpressionTestCase("((a + b) * (c / d)) - ((e - f) * (g + h))", "(a + b) * c / d - (e - f) * (g + h)"),
            new ExpressionTestCase("-(a * (b - c))", "-(a * (b - c))"),

            new ExpressionTestCase("2 * 3 / 4", "2 * 3 / 4"),
            new ExpressionTestCase("a + b - c * d / e + f - g", "a + b - c * d / e + f - g"),
            new ExpressionTestCase("-(a + b) * -c", "-(a + b) * -c"),
            new ExpressionTestCase("-(1 - (2 + (3 - 4)))", "-(1 - (2 + (3 - 4)))"),
            new ExpressionTestCase("a / (b / (c / d))", "a / (b / (c / d))"),
            new ExpressionTestCase("2 / 3 * 4", "2 / 3 * 4")
        );
    }

    @ParameterizedTest
    @MethodSource("expressionProvider")
    void buildExpressionTreeTest(final ExpressionTestCase testCase) {
        final TreeNode expressionTree = new ExpressionTreeBuilder().buildExpressionTree(testCase.expression);
        final String result = TraversalExpressionHelper.traversalExpression(expressionTree);
        assertEquals(testCase.expectedResult, result);
    }

    static class ExpressionTestCase {

        String expression;
        String expectedResult;

        ExpressionTestCase(final String expression, final String expectedResult) {
            this.expression = expression;
            this.expectedResult = expectedResult;
        }
    }

    static Stream<ExpressionTestCase> expressionFailedCasesProvider() {
        return Stream.of(
            new ExpressionTestCase("-1--2", ""),
            new ExpressionTestCase("1--2", ""),
            new ExpressionTestCase("-5 - -1 ", ""),
            new ExpressionTestCase("1 * (-1 - -b)-3-2-1 ", ""),
            new ExpressionTestCase("1*(+1)", ""),
            new ExpressionTestCase("1/(+1)", ""),
            new ExpressionTestCase("1-(+1)", ""),
            new ExpressionTestCase("1   23", "")
        );
    }

    @ParameterizedTest
    @MethodSource("expressionFailedCasesProvider")
    void buildExpressionTreeFailedTest(final ExpressionTestCase testCase) {
        final TreeNode expressionTree = new ExpressionTreeBuilder().buildExpressionTree(testCase.expression);
        final String result = TraversalExpressionHelper.traversalExpression(expressionTree);
        assertEquals(testCase.expectedResult, result);
    }
}

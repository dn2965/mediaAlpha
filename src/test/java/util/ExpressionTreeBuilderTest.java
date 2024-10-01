package util;


import static org.junit.jupiter.api.Assertions.assertEquals;

import model.TreeNode;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ExpressionTreeBuilderTest {

    static Stream<ExpressionTestCase> expressionProvider() {
        return Stream.of(

            new ExpressionTestCase("1*(2+(3*(4+5)))", "1*(2+3*(4+5))"),
            new ExpressionTestCase("2 + (3 / -5)", "2+3/-5"),
            new ExpressionTestCase("x+(y+z)+(t+(v+w))", "x+y+z+t+v+w"),

            new ExpressionTestCase("()", ""),
            new ExpressionTestCase("(())", ""),
            new ExpressionTestCase("(((1)))", "1"),
            new ExpressionTestCase("(((1+2)))", "1+2"),
            new ExpressionTestCase("(((1*2)))", "1*2"),
            new ExpressionTestCase("(((1--2)))", "1--2"),
            new ExpressionTestCase("((-(1)))", "-1"),
            new ExpressionTestCase("((-(1)*2))", "-1*2"),
            new ExpressionTestCase("1+(-(1)*2)", "1+-1*2"),
            new ExpressionTestCase("(1)+(-1*2)", "1+-1*2"),
            new ExpressionTestCase("(1)+((-1)*2)", "1+-1*2"),
            new ExpressionTestCase("-(1)+((-1)*2)", "-1+-1*2"),
            new ExpressionTestCase("-(1)+((-1)*(2))", "-1+-1*2"),
            new ExpressionTestCase("(-1)+(-1)*(2)", "-1+-1*2"),
            new ExpressionTestCase("(-1)+((-1)+(2))", "-1+-1+2"),
            new ExpressionTestCase("(-1)+((-1)-(2))", "-1+(-1-2)"),
            new ExpressionTestCase("-1--2", "-1--2"),
            new ExpressionTestCase("1--2", "1--2"),

            new ExpressionTestCase("(1+1+2) ", "1+1+2"),
            new ExpressionTestCase("1*(2+3*(4+5))", "1*(2+3*(4+5))"),
            new ExpressionTestCase("2+3/-5", "2+3/-5"),
            new ExpressionTestCase("x+y+z+t+v+w", "x+y+z+t+v+w"),

            new ExpressionTestCase("-5 - -1 ", "-5--1"),
            new ExpressionTestCase("(1 - 5)", "1-5"),
            new ExpressionTestCase("(-1 + 2)", "-1+2"),
            new ExpressionTestCase("- 5 - 1 ", "-5-1"),
            new ExpressionTestCase("1 - 5", "1-5"),
            new ExpressionTestCase("1 - (-1*b)-3", "1--1*b-3"),
            new ExpressionTestCase("1 * (-1 - -b)-3-2-1 ", "1*(-1--b)-3-2-1"),
            new ExpressionTestCase("2-(2+3)", "2-(2+3)"),
            new ExpressionTestCase("-(2)-(2+3)", "-2-(2+3)")
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
}

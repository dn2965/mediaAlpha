package util;

import static util.ExpressionTreeBuilder.LEFT_PARENTHESIS;
import static util.ExpressionTreeBuilder.RIGHT_PARENTHESIS;
import static model.NumberOperator.ADDITION;
import static model.NumberOperator.DIVISION;
import static model.NumberOperator.MULTIPLICATION;
import static model.NumberOperator.SUBTRACTION;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class InputTokenizeParser {

    public static List<String> parse(final String expression) {
        final List<String> result = new ArrayList<>();
        final StringBuilder currentExpressionUnit = new StringBuilder();
        IntStream.range(0, expression.length())
            .mapToObj(expression::charAt)
            .filter(c -> !Character.isWhitespace(c))
            .forEach(streamUnit -> {
                if (isParenthesesMultiDivAddSign(streamUnit)) { // those signs "(, ), *, /, +"
                    addCurrentExpressionUnit(result, currentExpressionUnit);
                    result.add(String.valueOf(streamUnit));
                } else if (currentExpressionUnit.length() == 0 && isRightParenthesis(result)) {
                    result.add(String.valueOf(streamUnit));
                } else if (isSubtraction(streamUnit)) { // those sign "-"
                    handleSubtraction(result, currentExpressionUnit, streamUnit);
                } else {
                    appendToCurrentExpressionUnit(currentExpressionUnit, streamUnit);
                }
            });
        // done, read all char from expression

        addCurrentExpressionUnit(result, currentExpressionUnit); // Add any remaining unit
        return result;
    }

    private static void handleSubtraction(List<String> result, StringBuilder currentExpressionUnit, char streamUnit) {
        if (currentExpressionUnit.length() > 0) {
            addCurrentExpressionUnit(result, currentExpressionUnit);
            result.add(String.valueOf(streamUnit));
        } else {
            appendToCurrentExpressionUnit(currentExpressionUnit, streamUnit);
        }
    }

    private static void appendToCurrentExpressionUnit(StringBuilder currentExpressionUnit, char streamUnit) {
        currentExpressionUnit.append(streamUnit);
    }

    private static boolean isRightParenthesis(final List<String> result) {
        return !result.isEmpty() && result.get(result.size() - 1).equals(RIGHT_PARENTHESIS);
    }

    private static boolean isParenthesesMultiDivAddSign(final Character input) {
        return input == LEFT_PARENTHESIS.charAt(0) || input == RIGHT_PARENTHESIS.charAt(0) || input == ADDITION.getSign().charAt(0) || input == MULTIPLICATION.getSign().charAt(0) || input == DIVISION.getSign().charAt(0);
    }

    private static boolean isSubtraction(final Character streamUnit) {
        return streamUnit == SUBTRACTION.getSign().charAt(0);
    }

    private static void addCurrentExpressionUnit(final List<String> result, final StringBuilder currentExpressionUnit) {
        if (currentExpressionUnit.length() > 0) {
            result.add(currentExpressionUnit.toString());
            currentExpressionUnit.setLength(0);
        }
    }

    public static void main(final String[] args) {

        final List<String> parsed1 = parse("1+(-(1)*2)");
        parsed1.forEach(System.out::println);

    }
}

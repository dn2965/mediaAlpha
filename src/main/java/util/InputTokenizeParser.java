package util;

import static util.ExpressionTreeBuilder.LEFT_PARENTHESIS;
import static util.ExpressionTreeBuilder.RIGHT_PARENTHESIS;
import static model.NumberOperator.ADDITION;
import static model.NumberOperator.DIVISION;
import static model.NumberOperator.MULTIPLICATION;
import static model.NumberOperator.SUBTRACTION;
import static util.InputUtil.toNormalize;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InputTokenizeParser {

    public static List<String> parse(final String expression) {
        final boolean isValid = isValidInput(expression);
        if (!isValid) {
            log.warn("invalid input:{}", expression);
            return List.of();
        }
        final List<String> result = new ArrayList<>();
        final StringBuilder currentExpressionUnit = new StringBuilder();
        Pattern pattern = Pattern.compile("(\\s*[a-zA-Z]+\\s*|\\s*\\d+\\s*|\\s*[+\\-*/()]\\s*)");

        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            final String streamUnit = matcher.group(); // 使用 substring 取得字串
            if (isParenthesesMultiDivAddSign(streamUnit)) { // 處理運算符和括號
                addCurrentExpressionUnit(result, currentExpressionUnit);
                result.add(streamUnit);
            } else if (currentExpressionUnit.length() == 0 && isRightParenthesis(result)) {
                result.add(streamUnit);
            } else if (isSubtraction(streamUnit)) { // 處理減號
                handleSubtraction(result, currentExpressionUnit, streamUnit);
            } else {
                currentExpressionUnit.append(streamUnit); // 添加到當前運算元中
            }
        }
        addCurrentExpressionUnit(result, currentExpressionUnit); // Add any remaining unit
        return result;
    }

    private static boolean isValidInput(final String input) {
        if (input.matches(".*\\d+\\s+\\d.*")) {
            return false;
        }

        if (toNormalize(input).matches(".*[+-]\\s*[-+]\\s*.*|.*[-+]\\s*[+-].*|.*\\(\\s*\\+\\d+\\s*\\).*")) {
            return false;
        }

        if (isInvalidNegativeNumber(input)) {
            return false;
        }

        final Stack<Character> countForParenthesisStack = new Stack<>();
        for (final char c : input.toCharArray()) {
            if (c == '(') {
                countForParenthesisStack.push(c);
            } else if (c == ')') {
                countForParenthesisStack.pop();
            }
        }

        return countForParenthesisStack.isEmpty();
    }

    private static boolean isInvalidNegativeNumber(final String input) {
        return input.matches("^\\s*-\\s*\\(\\s*\\d+\\s*\\).*");
    }

    private static void handleSubtraction(final List<String> result, final StringBuilder currentExpressionUnit, final String streamUnit) {
        if (currentExpressionUnit.length() > 0) { // 表示已有
            addCurrentExpressionUnit(result, currentExpressionUnit);
            result.add(String.valueOf(streamUnit));
        } else {
            appendToCurrentExpressionUnit(currentExpressionUnit, streamUnit);
        }
    }

    private static void appendToCurrentExpressionUnit(final StringBuilder currentExpressionUnit, final String streamUnit) {
        currentExpressionUnit.append(streamUnit);
    }

    private static boolean isRightParenthesis(final List<String> result) {
        return !result.isEmpty() && toNormalize(result.get(result.size() - 1)).equals(RIGHT_PARENTHESIS);
    }

    private static boolean isParenthesesMultiDivAddSign(final String input) {
        String trimmed = toNormalize(input);
        return trimmed.equals(LEFT_PARENTHESIS) || trimmed.equals(RIGHT_PARENTHESIS) || trimmed.equals(ADDITION.getSign()) || trimmed.equals(MULTIPLICATION.getSign()) || trimmed.equals(DIVISION.getSign());
    }

    private static boolean isSubtraction(final String streamUnit) {
        return toNormalize(streamUnit).equals(SUBTRACTION.getSign());
    }

    private static void addCurrentExpressionUnit(final List<String> result, final StringBuilder currentExpressionUnit) {
        if (currentExpressionUnit.length() > 0) {
            result.add(currentExpressionUnit.toString());
            currentExpressionUnit.setLength(0);
        }
    }

    public static void main(final String[] args) {
//        final List<String> parsed1 = parse("(2*3)/5");
        final List<String> parsed1 = parse("2*(3/5)");
        parsed1.forEach(e -> System.out.println("[" + e + "]"));
        final StringBuilder concat = new StringBuilder();
        parsed1.forEach(concat::append);
        log.debug(concat.toString());
    }
}

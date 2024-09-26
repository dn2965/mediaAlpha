package model;

import java.util.List;
import lombok.Getter;

public enum NumberOperator {
    ADDITION("+", 1), SUBTRACTION("-", 1), MULTIPLICATION("*", 2), DIVISION("/", 2);
    @Getter
    private String sign;
    @Getter
    private int priority;

    public static NumberOperator findBySign(final char charInput) {
        return findBySign(String.valueOf(charInput));
    }

    public static NumberOperator findBySign(final String signString) {
        final NumberOperator[] values = NumberOperator.values();
        for (final NumberOperator numberOperator : values) {
            if (numberOperator.sign.equalsIgnoreCase(signString)) {
                return numberOperator;
            }
        }
        return null;
    }

    public static boolean isLastElementNumberOperator(final List<String> signList) {
        if(signList == null && signList.isEmpty()){
            return false;
        }
        return findBySign(signList.get(signList.size() - 1)) != null;
    }

    NumberOperator(String signString, int priorityNumber) {
        sign = signString;
        priority = priorityNumber;
    }
}

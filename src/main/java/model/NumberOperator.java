package model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum NumberOperator {
    ADDITION("+", 1), SUBTRACTION("-", 1), MULTIPLICATION("*", 2), DIVISION("/", 2);
    @Getter
    private String sign;
    @Getter
    private int priority;

    public static NumberOperator findBySign(final String signString) {
        final NumberOperator[] values = NumberOperator.values();
        for (final NumberOperator numberOperator : values) {
            if (numberOperator.sign.equalsIgnoreCase(signString)) {
                return numberOperator;
            }
        }
//        log.warn("check, the result is null {}", signString);
        return null;
    }

    NumberOperator(String signString, int priorityNumber) {
        sign = signString;
        priority = priorityNumber;
    }
}

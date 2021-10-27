package com.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Currency {
    PLN("Polish zloty", "PLN"),
    EUR("Euro", "EUR"),
    USD("US Dollar", "USD");

    private final String name;
    private final String symbol;

    Currency (String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}

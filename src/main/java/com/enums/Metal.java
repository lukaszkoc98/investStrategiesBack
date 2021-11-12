package com.enums;

public enum Metal {
    GOLD("gold", "XAU"),
    SILVER("silver", "XAG");

    private final String name;
    private final String symbol;

    Metal (String name, String symbol) {
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

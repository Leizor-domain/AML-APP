package com.leizo.enums;

public  enum RuleSensitivity {
    LOW(10),
    MEDIUM(20),
    HIGH(30);

    private final int weight;

    RuleSensitivity(int weight) {
        this.weight = weight;
    }
    public int getWeight() {
        return weight;
    }
}

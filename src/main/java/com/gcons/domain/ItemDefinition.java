package com.gcons.domain;

public class ItemDefinition {
    private final String name;
    private final Category category;
    private final Rarity rarity;
    private final long baseValueCents;

    public ItemDefinition(String name, Category category, Rarity rarity, long baseValueCents) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        if (rarity == null) {
            throw new IllegalArgumentException("rarity must not be null");
        }
        if (baseValueCents < 0) {
            throw new IllegalArgumentException("baseValueCents must be >= 0");
        }

        this.name = name;
        this.category = category;
        this.rarity = rarity;
        this.baseValueCents = baseValueCents;
    }
     public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public long getBaseValueCents() {
        return baseValueCents;
    }

    @Override
    public String toString() {
        return "ItemDefinition{" +
                "name='" + name + '\'' +
                ", category=" + category +
                ", rarity=" + rarity +
                ", baseValueCents=" + baseValueCents +
                '}';
    }
}
    


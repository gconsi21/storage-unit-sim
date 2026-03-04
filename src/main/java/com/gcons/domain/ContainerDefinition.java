package com.gcons.domain;

import java.util.EnumSet;

public class ContainerDefinition {
    private final String name;
    private final long costCents;
    private final int weight1;
    private final int weight2;
    private final int weight3;
    private final int commonWeight;
    private final int uncommonWeight;
    private final int rareWeight;
    private final int epicWeight;
    private final int legendaryWeight;
    private final EnumSet<Category> allowedCategories;

    public ContainerDefinition(String name, long costCents, int weight1, int weight2, int weight3, int commonWeight, int uncommonWeight, int rareWeight, int epicWeight, int legendaryWeight, EnumSet<Category> allowedCategories){
        
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (costCents < 0) {
            throw new IllegalArgumentException("costCents must be >=0");
        }
        if (weight1 < 0) {
            throw new IllegalArgumentException("weight1 must be >=0");
        }
        if (weight2 < 0) {
            throw new IllegalArgumentException("weight2 must be >=0");
        }
        if (weight3 < 0) {
            throw new IllegalArgumentException("weight3 must be >=0");
        }
        if (commonWeight < 0) {
            throw new IllegalArgumentException("commonWeight must be >= 0");
        }
        if (uncommonWeight < 0) {
            throw new IllegalArgumentException("uncommonWeight must be >= 0");
        }
        if (rareWeight < 0) {
            throw new IllegalArgumentException("rareWeight must be >= 0");
        }
        if (epicWeight < 0) {
            throw new IllegalArgumentException("epicWeight must be >= 0");
        }
        if (legendaryWeight < 0) {
            throw new IllegalArgumentException("legendaryWeight must be >= 0");
        }
        if ((commonWeight + uncommonWeight + rareWeight + epicWeight + legendaryWeight) <= 0) {
            throw new IllegalArgumentException("The total rarity weight must be > 0 ");
        }
        if ((weight1 + weight2 + weight3) <= 0) {
            throw new IllegalArgumentException("The total drop weight must be > 0 ");
        }
        if ((allowedCategories == null || allowedCategories.isEmpty())){
            throw new IllegalArgumentException("categories must not be null or empty");
        }

        this.name = name;
        this.costCents = costCents;
        this.weight1 = weight1;
        this.weight2 = weight2;
        this.weight3 = weight3;
        this.commonWeight = commonWeight;
        this.uncommonWeight = uncommonWeight;
        this.rareWeight = rareWeight;
        this.epicWeight = epicWeight;
        this.legendaryWeight = legendaryWeight;
        this.allowedCategories = EnumSet.copyOf(allowedCategories);
    }
    public String getName() {
        return name;
    }
    public long getCostCents() {
        return costCents;
    }
    public int getWeight1() {
        return weight1;
    }
    public int getWeight2() {
        return weight2;
    }
    public int getWeight3() {
        return weight3;
    }
    public int getCommonWeight() {
        return commonWeight;
    }
    public int getUncommonWeight() {
        return uncommonWeight;
    }
    public int getRareWeight() {
        return rareWeight;
    }
    public int getEpicWeight() {
        return epicWeight;
    }
    public int getLegendaryWeight() {
        return legendaryWeight;
    }
    public int getTotalDropWeight() {
        return weight1 + weight2 + weight3;
    }
    public int getTotalRarityWeight() {
        return commonWeight + uncommonWeight + rareWeight + epicWeight + legendaryWeight;
    }
    public EnumSet<Category> getAllowedCategories() {
        return EnumSet.copyOf(allowedCategories);
    }
}

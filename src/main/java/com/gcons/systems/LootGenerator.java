package com.gcons.systems;

import com.gcons.domain.Category;
import com.gcons.domain.ContainerDefinition;
import com.gcons.domain.ItemDefinition;
import com.gcons.domain.ItemInstance;
import com.gcons.domain.Rarity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public final class LootGenerator {

    private final List<ItemDefinition> catalog;
    private final Random rng;

    /**
     * @param catalog List of possible items (templates).
     * @param rng     Random source (pass in so you can seed it for predictable testing).
     */
    public LootGenerator(List<ItemDefinition> catalog, Random rng) {
        if (catalog == null || catalog.isEmpty()) {
            throw new IllegalArgumentException("catalog must not be null or empty");
        }
        if (rng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }
        this.catalog = List.copyOf(catalog); // defensive copy: callers can't modify our internal list
        this.rng = rng;
    }

    /**
     * Opens a container and returns the items found inside.
     */
    public List<ItemInstance> open(ContainerDefinition container) {
        if (container == null) {
            throw new IllegalArgumentException("container must not be null");
        }

        int dropCount = rollDropCount(container);
        List<ItemInstance> results = new ArrayList<>(dropCount);

        for (int i = 0; i < dropCount; i++) {
            Rarity rolledRarity = rollRarity(container);
            ItemDefinition chosen = pickItemWithFallback(container.getAllowedCategories(), rolledRarity);
            results.add(new ItemInstance(chosen));
        }

        return results;
    }

    /**
     * Weighted roll for number of items dropped: 1, 2, or 3.
     * Uses container's (weight1, weight2, weight3).
     */
    private int rollDropCount(ContainerDefinition container) {
        int w1 = container.getWeight1();
        int w2 = container.getWeight2();
        int w3 = container.getWeight3();

        int total = w1 + w2 + w3;
        // total should already be validated in ContainerDefinition, but we guard anyway
        if (total <= 0) {
            throw new IllegalStateException("Container has invalid drop weights (total <= 0)");
        }

        int roll = rng.nextInt(total); // 0..total-1

        // Cumulative buckets:
        // [0..w1-1] => 1 item
        // [w1..w1+w2-1] => 2 items
        // [w1+w2..total-1] => 3 items
        if (roll < w1) return 1;
        if (roll < w1 + w2) return 2;
        return 3;
    }

    /**
     * Weighted roll for rarity using container's rarity weights.
     */
    private Rarity rollRarity(ContainerDefinition container) {
        int[] weights = new int[] {
                container.getCommonWeight(),
                container.getUncommonWeight(),
                container.getRareWeight(),
                container.getEpicWeight(),
                container.getLegendaryWeight()
        };

        int idx = pickWeightedIndex(weights);
        // Assumes Rarity enum order is: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
        return Rarity.values()[idx];
    }

    /**
     * Picks an item matching:
     * - allowed categories
     * - rolled rarity (or lower rarities if no match exists)
     *
     * Fallback strategy:
     * 1) Try rolled rarity
     * 2) If empty, try next-lower rarity, down to COMMON
     * 3) If still empty, pick ANY item in allowed categories (any rarity)
     */
    private ItemDefinition pickItemWithFallback(EnumSet<Category> allowedCategories, Rarity startRarity) {
        // Try start rarity, then lower rarities
        for (Rarity r : raritiesFrom(startRarity)) {
            List<ItemDefinition> candidates = candidatesFor(allowedCategories, r);
            if (!candidates.isEmpty()) {
                return pickUniform(candidates);
            }
        }

        // Last resort: any item in allowed categories, any rarity
        List<ItemDefinition> anyAllowed = new ArrayList<>();
        for (ItemDefinition def : catalog) {
            if (allowedCategories.contains(def.getCategory())) {
                anyAllowed.add(def);
            }
        }
        if (anyAllowed.isEmpty()) {
            throw new IllegalStateException("No catalog items match the container's allowed categories");
        }
        return pickUniform(anyAllowed);
    }

    /**
     * Returns a list of rarities from start down to COMMON.
     * Example: start=EPIC => [EPIC, RARE, UNCOMMON, COMMON]
     */
    private List<Rarity> raritiesFrom(Rarity start) {
        List<Rarity> list = new ArrayList<>();
        int startIdx = start.ordinal();
        for (int i = startIdx; i >= 0; i--) {
            list.add(Rarity.values()[i]);
        }
        return list;
    }

    /**
     * Builds candidate list for a given allowedCategories + rarity.
     */
    private List<ItemDefinition> candidatesFor(EnumSet<Category> allowedCategories, Rarity rarity) {
        List<ItemDefinition> candidates = new ArrayList<>();
        for (ItemDefinition def : catalog) {
            if (def.getRarity() == rarity && allowedCategories.contains(def.getCategory())) {
                candidates.add(def);
            }
        }
        return candidates;
    }

    /**
     * Picks a uniformly random element from a non-empty list.
     */
    private ItemDefinition pickUniform(List<ItemDefinition> candidates) {
        int idx = rng.nextInt(candidates.size());
        return candidates.get(idx);
    }

    /**
     * Generic weighted picker: returns the chosen index.
     * weights must sum to > 0 and contain no negatives.
     */
    private int pickWeightedIndex(int[] weights) {
        int total = 0;
        for (int w : weights) {
            if (w < 0) throw new IllegalStateException("Negative weight not allowed");
            total += w;
        }
        if (total <= 0) throw new IllegalStateException("Total weight must be > 0");

        int roll = rng.nextInt(total);
        int cum = 0;
        for (int i = 0; i < weights.length; i++) {
            cum += weights[i];
            if (roll < cum) return i;
        }

        // Should never happen if total/roll/cumulative are correct
        throw new IllegalStateException("Weighted selection failed");
    }
}
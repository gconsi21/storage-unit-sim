package com.gcons;

import com.gcons.domain.Category;
import com.gcons.domain.ContainerDefinition;
import com.gcons.domain.ItemDefinition;
import com.gcons.domain.ItemInstance;
import com.gcons.domain.Rarity;
import com.gcons.systems.LootGenerator;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        // 1) Tiny item catalog (templates). Values are in cents.
        List<ItemDefinition> catalog = List.of(
                new ItemDefinition("Hammer", Category.TOOLS, Rarity.COMMON, 899),
                new ItemDefinition("Screwdriver Set", Category.TOOLS, Rarity.COMMON, 1299),
                new ItemDefinition("Cordless Drill", Category.TOOLS, Rarity.UNCOMMON, 6999),
                new ItemDefinition("Socket Wrench Kit", Category.TOOLS, Rarity.UNCOMMON, 8999),

                new ItemDefinition("Old Laptop", Category.ELECTRONICS, Rarity.UNCOMMON, 11999),
                new ItemDefinition("Vintage Stereo Receiver", Category.ELECTRONICS, Rarity.RARE, 24999),
                new ItemDefinition("Sealed Smartphone Box", Category.ELECTRONICS, Rarity.EPIC, 59999),

                new ItemDefinition("Ceramic Vase", Category.HOME_GOODS, Rarity.COMMON, 1599),
                new ItemDefinition("Antique Lamp", Category.HOME_GOODS, Rarity.UNCOMMON, 7999),

                new ItemDefinition("Vintage Baseball Card", Category.COLLECTIBLES, Rarity.RARE, 17999),
                new ItemDefinition("Signed Memorabilia", Category.COLLECTIBLES, Rarity.EPIC, 49999),
                new ItemDefinition("Rare Gold Coin", Category.COLLECTIBLES, Rarity.LEGENDARY, 150000),

                // Keep contraband abstract + safe
                new ItemDefinition("Contraband Package (Level 1)", Category.CONTRABAND, Rarity.RARE, 199999),
                new ItemDefinition("Contraband Package (Level 2)", Category.CONTRABAND, Rarity.EPIC, 699999)
        );

        Random rng = new Random(); // later you can seed this for repeatable tests
        LootGenerator lootGenerator = new LootGenerator(catalog, rng);

        // 2) Container definitions (name, cost, drop weights, rarity weights, allowed categories)
        ContainerDefinition freeUnit = new ContainerDefinition(
                "Abandoned Unit (FREE)",
                0,
                60, 35, 5,          // drop count weights: 1,2,3 (mostly 1)
                90, 9, 1, 0, 0,      // rarity weights: Common..Legendary (tiny Rare chance)
                EnumSet.of(Category.TOOLS, Category.HOME_GOODS)
        );

        ContainerDefinition smallLocker = new ContainerDefinition(
                "Small Locker",
                5000,               // $50.00
                25, 55, 20,         // drop weights (1,2,3)
                70, 20, 8, 2, 0,     // rarity weights
                EnumSet.of(Category.TOOLS, Category.HOME_GOODS, Category.ELECTRONICS)
        );

        ContainerDefinition cleanout = new ContainerDefinition(
                "House Cleanout",
                20000,              // $200.00
                15, 55, 30,
                55, 25, 15, 4, 1,
                EnumSet.of(Category.TOOLS, Category.HOME_GOODS, Category.ELECTRONICS, Category.COLLECTIBLES)
        );

        ContainerDefinition estate = new ContainerDefinition(
                "Estate Unit",
                75000,              // $750.00
                10, 50, 40,
                40, 25, 20, 10, 5,
                EnumSet.of(Category.TOOLS, Category.HOME_GOODS, Category.ELECTRONICS, Category.COLLECTIBLES, Category.CONTRABAND)
        );

        List<ContainerDefinition> containers = List.of(freeUnit, smallLocker, cleanout, estate);

        // 3) Minimal game state
        long balanceCents = 20000; // Start with $200.00
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Storage Unit Flip Simulator ===");
        System.out.println("You start with " + money(balanceCents));
        System.out.println();

        // 4) Simple loop
        boolean running = true;
        while (running) {

            System.out.println("Balance: " + money(balanceCents));
            System.out.println("Choose a unit to buy/open:");
            System.out.println("0) Quit");
            System.out.println("1) " + containers.get(0).getName() + " - " + money(containers.get(0).getCostCents()));
            System.out.println("2) " + containers.get(1).getName() + " - " + money(containers.get(1).getCostCents()));
            System.out.println("3) " + containers.get(2).getName() + " - " + money(containers.get(2).getCostCents()));
            System.out.println("4) " + containers.get(3).getName() + " - " + money(containers.get(3).getCostCents()));
            System.out.print("> ");

            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number (0-4).");
                System.out.println();
                continue;
            }

            if (choice == 0) {
                running = false;
                continue;
            }

            if (choice < 1 || choice > 4) {
                System.out.println("Invalid choice. Pick 0-4.");
                System.out.println();
                continue;
            }

            ContainerDefinition selected = containers.get(choice - 1);
            long cost = selected.getCostCents();

            // Free unit rule: only allow FREE if you're broke (or it is free anyway).
            boolean isFreeUnit = (cost == 0);
            if (isFreeUnit && balanceCents > 5000) {
                System.out.println("The free unit is only available if you hit less than $50.");
                System.out.println();
                continue;
            }

            if (!isFreeUnit && balanceCents < cost) {
                System.out.println("Not enough money for that unit.");
                System.out.println();
                continue;
            }

            // Purchase: subtract cost first
            balanceCents -= cost;

            // Open & roll loot
            List<ItemInstance> found = lootGenerator.open(selected);

            // Return = sum of base values (MVP)
            long returnCents = 0;
            for (ItemInstance item : found) {
                returnCents += item.getDefinition().getBaseValueCents();
            }

            // Sell instantly: add return
            balanceCents += returnCents;

            long netCents = returnCents - cost;

            // Receipt output
            System.out.println();
            System.out.println("=== OPENED: " + selected.getName() + " ===");
            System.out.println("Cost:   " + money(cost));
            System.out.println("Return: " + money(returnCents));
            System.out.println("Net:    " + money(netCents));
            System.out.println("New Balance: " + money(balanceCents));
            System.out.println("Items found:");

            for (ItemInstance item : found) {
                ItemDefinition def = item.getDefinition();
                System.out.println("- " + def.getName()
                        + " [" + def.getRarity() + ", " + def.getCategory() + "] "
                        + money(def.getBaseValueCents()));
            }

            System.out.println();

            // Failsafe: if player goes broke, they can pick the free unit next loop
            if (balanceCents <= 0) {
                balanceCents = 0;
                System.out.println("You are broke. The FREE unit is now available.");
                System.out.println();
            }
        }

        System.out.println("Goodbye!");
    }

    // Helper to display long cents as $D.CC
    private static String money(long cents) {
        return String.format("$%,.2f", cents / 100.0);
    }
}
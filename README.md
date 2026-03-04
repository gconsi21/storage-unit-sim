# Storage Unit Flip Simulator

A command-line Java game that simulates buying abandoned storage units and flipping the contents for profit.

The player purchases storage containers of varying risk levels. Each container drops a random set of items with weighted rarity probabilities. Items are automatically sold, and the player’s balance updates after each purchase.

This project was built as a practice exercise for Java object-oriented design, probability systems, and Git workflow.

---

# Features

- Weighted random loot generation
- Multiple container tiers with different risks and rewards
- Item rarity system
- Category-based item filtering per container
- Economy system using cents for precise money handling
- Emergency free container if the player goes bankrupt
- Simple CLI interface
- Maven-based project structure

---

# Gameplay

Players start with **$200** and can purchase different storage units.

Each container:

1. Costs money to open
2. Drops **1–3 items**
3. Uses **weighted rarity probabilities**
4. Automatically sells the items
5. Updates your balance

Example gameplay output:

=== OPENED: Small Locker ===
Cost:   $50.00
Return: $72.98
Net:    +$22.98
New Balance: $222.98

Items found:
- Hammer [COMMON, TOOLS] $8.99
- Vintage Stereo Receiver [RARE, ELECTRONICS] $249.99

If the player runs out of money, a **free emergency container** becomes available to allow recovery.

---

# Container Types

| Container | Cost | Risk | Rewards |
|----------|------|------|--------|
| Abandoned Unit | Free | Very Low | Mostly common items |
| Small Locker | $50 | Low | Common / Uncommon |
| House Cleanout | $200 | Medium | Rare possible |
| Estate Unit | $750 | High | Epic / Legendary possible |

---

# Item System

Each item has:

- Name
- Category
- Rarity
- Base value

Example categories:

- Tools
- Electronics
- Home Goods
- Collectibles
- Contraband

Example rarities:

COMMON  
UNCOMMON  
RARE  
EPIC  
LEGENDARY  

Containers define **weighted probabilities** that determine which rarity can drop.

---

# Project Structure

storage-unit-sim
│
├─ src/main/java/com/gcons
│  ├─ App.java                # Main game loop
│
├─ domain
│  ├─ Category.java
│  ├─ Rarity.java
│  ├─ ItemDefinition.java
│  ├─ ItemInstance.java
│  ├─ ContainerDefinition.java
│
├─ systems
│  ├─ LootGenerator.java
│
└─ pom.xml

The architecture separates:

Domain objects
- Represent game data

Systems
- Handle gameplay logic

App
- CLI interaction and game loop

---

# Technologies Used

- Java
- Maven
- Git
- VS Code

---

# How to Run

Clone the repository:

git clone https://github.com/YOUR_USERNAME/storage-unit-sim.git

Navigate to the project:

cd storage-unit-sim

Compile:

mvn compile

Run:

mvn exec:java

Or run `App.java` directly from your IDE.

---

# Future Improvements

Potential features that could be added:

- Item condition system (damaged / pristine)
- Dynamic pricing engine
- Container auctions
- Inventory instead of auto-selling
- Event system (police raid, lucky find, etc.)
- GUI version
- Save/load game
- Unit tests

---

# Purpose of the Project

This project was built to practice:

- Java OOP design
- Random probability systems
- Game state management
- Git feature branch workflow
- Maven project structure


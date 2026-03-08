import java.util.Scanner;

public class GameEngine {
    // Core game data
    public Player[] heroes = new Player[2];       // Two player characters
    public Enemy[] enemies = new Enemy[3];        // Max number of enemies per encounter
    public int enemyCount;                         // Actual number of enemies in current encounter
    private int encounterNumber = 0;                // Current encounter (1-8)
    private Scanner scanner = new Scanner(System.in);

    // Equipment tiers per class (index 0 = basic, 1 = improved, 2 = best)
    private Weapon[] warriorWeapons = { new Weapon("Iron Sword", 5),      new Weapon("Steel Blade", 10),     new Weapon("Dragonbane", 15) };
    private Weapon[] mageWeapons    = { new Weapon("Wooden Staff", 5),    new Weapon("Enchanted Staff", 10), new Weapon("Arcane Scepter", 15) };
    private Weapon[] archerWeapons  = { new Weapon("Short Bow", 5),       new Weapon("Longbow", 10),         new Weapon("Elven Bow", 15) };

    private Armor[] warriorArmors   = { new Armor("Leather Armor", 3),    new Armor("Chain Mail", 6),        new Armor("Dragon Scale", 10) };
    private Armor[] mageArmors      = { new Armor("Cloth Robe", 3),       new Armor("Enchanted Robe", 6),    new Armor("Arcane Vestment", 10) };
    private Armor[] archerArmors    = { new Armor("Leather Vest", 3),     new Armor("Ranger Coat", 6),       new Armor("Shadow Cloak", 10) };

    private Weapon[] getWeaponTiers(Player p) {
        switch (p.heroClass) {
            case "Mage":   return mageWeapons;
            case "Archer": return archerWeapons;
            default:       return warriorWeapons;
        }
    }

    private Armor[] getArmorTiers(Player p) {
        switch (p.heroClass) {
            case "Mage":   return mageArmors;
            case "Archer": return archerArmors;
            default:       return warriorArmors;
        }
    }

    // MAIN GAME LOOP
    public void start() {
        printSeparator();
        System.out.println("            ETERNAL DUEL");
        printSeparator();
        System.out.println();
        createCharacters();
        equipInitialGear();

        // Encounter loop (max 8)
        for (encounterNumber = 1; encounterNumber <= 8; encounterNumber++) {
            System.out.println();
            boolean isBoss = (encounterNumber % 4 == 0);
            printSeparator();
            if (isBoss) {
                System.out.println("         ENCOUNTER " + encounterNumber + " - BOSS!");
                printSeparator();
                System.out.println("      *** A powerful enemy approaches! ***");
            } else {
                System.out.println("             ENCOUNTER " + encounterNumber);
            }
            printSeparator();
            System.out.println();
            generateEnemies(isBoss);
            boolean victory = runCombat();

            if (!victory) {
                System.out.println();
                printSeparator();
                System.out.println("              GAME OVER");
                printSeparator();
                System.out.println();
                scanner.close();
                return;
            }

            if (encounterNumber == 8) {
                System.out.println();
                printSeparator();
                System.out.println("               YOU WIN!");
                printSeparator();
                System.out.println();
                scanner.close();
                return;
            }

            // After each successful encounter (except the last), heal and remove debuffs
            fullHealHeroes();

            // Offer equipment upgrade before every boss encounter (encounter 3 and 7)
            if (encounterNumber == 3 || encounterNumber == 7) {
                offerEquipmentUpgrade();
            }
        }
    }

    // INITIALIZATION
    private void createCharacters() {
        System.out.println("---------- Character Creation ----------\n");
        System.out.println("Create Hero 1:");
        heroes[0] = createHero(1);
        System.out.println("\nCreate Hero 2:");
        heroes[1] = createHero(2);
        System.out.println("\n---------- Creation Complete ----------\n");
    }

    private Player createHero(int number) {
        System.out.println("Choose class for Hero " + number + ":");
        System.out.println("  1. Warrior");
        System.out.println("  2. Mage");
        System.out.println("  3. Archer");
        int classChoice = getIntInput(1, 3);
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        Player p;
        switch (classChoice) {
            case 1: // Warrior
                p = new Player(name, "Warrior", 100, 50, 20, 10, 5);
                p.setSkills(
                    new DamageSkill("Whirlwind Slash", 10, 15, null),
                    new SupportSkill("Second Wind", 8, false, 20, new DefenseBuffEffect(10, 2))
                );
                break;
            case 2: // Mage
                p = new Player(name, "Mage", 70, 80, 15, 5, 10);
                p.setSkills(
                    new DamageSkill("Firestorm", 15, 20, new BurnEffect(5, 2)),
                    new SupportSkill("Cure Wound", 10, true, 25, null)
                );
                break;
            case 3: // Archer
                p = new Player(name, "Archer", 80, 60, 18, 7, 7);
                p.setSkills(
                    new DamageSkill("Poison Volley", 12, 18, new PoisonEffect(5, 2)),
                    new SupportSkill("Hunter's Mark", 8, false, 0, new DefenseDebuffEffect(15, 2))
                );
                break;
            default:
                p = null; // Should never happen
        }
        System.out.println("Hero created: " + p.name + " the " + p.heroClass);
        return p;
    }

    private void equipInitialGear() {
        for (Player p : heroes) {
            p.equipWeapon(getWeaponTiers(p)[0]);
            p.equipArmor(getArmorTiers(p)[0]);
        }
        System.out.println("\nAll heroes equipped with basic gear.\n");
    }

    // ENEMY GENERATION
    private void generateEnemies(boolean isBoss) {
        if (isBoss) {
            enemyCount = 1;
            enemies[0] = new BossEnemy("Goblin King", 150, 0, 25, 8, 1);
        } else {
            enemyCount = (int)(Math.random() * 3) + 1; // 1 to 3 enemies
            for (int i = 0; i < enemyCount; i++) {
                int type = (int)(Math.random() * 3); // 0, 1, or 2
                switch (type) {
                    case 0: enemies[i] = new Enemy("Goblin", 40, 0, 12, 2, 1); break;
                    case 1: enemies[i] = new Enemy("Orc",    60, 0, 16, 4, 2); break;
                    case 2: enemies[i] = new Enemy("Troll",  80, 0, 20, 6, 3); break;
                }
            }
        }
    }

    // COMBAT LOOP
    private boolean runCombat() {
        // No redundant enemy list here – it will be shown on each player's turn

        while (true) {
            // Players' turns (always first)
            for (int i = 0; i < 2; i++) {
                if (heroes[i].isAlive()) {
                    heroes[i].takeTurn(this, scanner);
                    if (allEnemiesDefeated()) {
                        printVictorySummary();
                        return true;
                    }
                    System.out.println(); // Make text readable between turns
                }
            }

            // Enemies' turns
            for (int i = 0; i < enemyCount; i++) {
                if (enemies[i].isAlive()) {
                    enemies[i].takeTurn(this, scanner);
                    // Check after each enemy action
                    if (allEnemiesDefeated()) {
                        printVictorySummary();
                        return true;
                    }
                    if (allHeroesDefeated()) {
                        System.out.println("\n*** All heroes have fallen... ***\n");
                        return false;
                    }
                    System.out.println();
                }
            }
        }
    }

    private boolean allEnemiesDefeated() {
        for (int i = 0; i < enemyCount; i++) {
            if (enemies[i].isAlive()) return false;
        }
        return true;
    }

    private boolean allHeroesDefeated() {
        for (int i = 0; i < 2; i++) {
            if (heroes[i].isAlive()) return false;
        }
        return true;
    }

    // POST-ENCOUNTER ACTIONS
    private void fullHealHeroes() {
        for (Player p : heroes) {
            p.hp = p.maxHp;
            p.mp = p.maxMp;
            p.removeStatusEffect(); // Clears any lingering debuffs (e.g., Intimidate)
        }
        System.out.println("\n*** Heroes fully healed and restored! ***\n");
    }

    private void offerEquipmentUpgrade() {
        System.out.println();
        printSeparator();
        System.out.println("          New Equipment Found!");
        printSeparator();
        System.out.println();
        int tierIndex = (encounterNumber == 3) ? 1 : 2; // encounter 3 → tier 1, encounter 7 → tier 2

        for (int i = 0; i < 2; i++) {
            Player p = heroes[i];
            System.out.println("--- " + p.name + " (" + p.heroClass + ") ---");

            Weapon newWeapon = getWeaponTiers(p)[tierIndex];
            Armor newArmor   = getArmorTiers(p)[tierIndex];

            int weaponDiff = newWeapon.getAttackBonus() - (p.weapon != null ? p.weapon.getAttackBonus() : 0);
            int armorDiff  = newArmor.getDefenseBonus() - (p.armor  != null ? p.armor.getDefenseBonus()  : 0);

            System.out.println("  Weapon: " + newWeapon.getName() + " (+" + newWeapon.getAttackBonus() + " atk)"
                + "  |  Current: " + (p.weapon != null ? p.weapon.getName() : "none")
                + "  |  Upgrade: " + (weaponDiff >= 0 ? "+" : "") + weaponDiff + " atk");

            System.out.println("  Armor:  " + newArmor.getName() + " (+" + newArmor.getDefenseBonus() + " def)"
                + "  |  Current: " + (p.armor != null ? p.armor.getName() : "none")
                + "  |  Upgrade: " + (armorDiff >= 0 ? "+" : "") + armorDiff + " def");

            System.out.println("  1. Take both          2. Take weapon only");
            System.out.println("  3. Take armor only    4. Keep current gear");
            int choice = getIntInput(1, 4);
            switch (choice) {
                case 1:
                    p.equipWeapon(newWeapon);
                    p.equipArmor(newArmor);
                    System.out.println("  " + p.name + " equips " + newWeapon.getName() + " and " + newArmor.getName() + "!\n");
                    break;
                case 2:
                    p.equipWeapon(newWeapon);
                    System.out.println("  " + p.name + " equips " + newWeapon.getName() + "!\n");
                    break;
                case 3:
                    p.equipArmor(newArmor);
                    System.out.println("  " + p.name + " equips " + newArmor.getName() + "!\n");
                    break;
                case 4:
                    System.out.println("  " + p.name + " keeps their current gear.\n");
                    break;
            }
        }
        printSeparator();
        System.out.println();
    }

    private void printVictorySummary() {
        int heroesAlive = 0;
        for (Player p : heroes) { if (p.isAlive()) heroesAlive++; }
        System.out.println();
        printSeparator();
        System.out.println("             *** VICTORY! ***");
        System.out.println("          Enemies defeated: " + enemyCount);
        System.out.println("          Heroes remaining: " + heroesAlive + "/2");
        if (encounterNumber < 8) System.out.println("    Proceeding to encounter " + (encounterNumber + 1) + "...");
        printSeparator();
        System.out.println();
    }

    // INPUT CHECKER
    private void printSeparator() {
        System.out.println("=======================================");
    }

    private int getIntInput(int min, int max) {
        int val;
        while (true) {
            System.out.print("  Enter choice: ");
            if (scanner.hasNextInt()) {
                val = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (val >= min && val <= max) {
                    return val;
                } else {
                    System.out.println("  Please enter a number between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("  Invalid input. Please enter a number.");
                scanner.next(); // discard invalid token
            }
        }
    }
}
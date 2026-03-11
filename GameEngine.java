import java.util.Scanner;

public class GameEngine {
    // Core game data
    public Player[] heroes = new Player[3];           // Three player characters
    public Enemy[] enemies = new Enemy[3];            // Max number of enemies per encounter
    public int enemyCount;                            // Actual number of enemies in current encounter
    private int encounterNumber = 0;                  // Current encounter (1-8)
    private Scanner scanner = new Scanner(System.in);

    // Equipment tiers per class (index 0 = basic, 1 = improved, 2 = best)
    private Weapon[] warriorWeapons = { new Weapon("Iron Sword", 4),      new Weapon("Steel Blade", 8),      new Weapon("Dragonbane", 12) };
    private Weapon[] mageWeapons    = { new Weapon("Wooden Staff", 4),    new Weapon("Enchanted Staff", 8),  new Weapon("Arcane Scepter", 12) };
    private Weapon[] archerWeapons  = { new Weapon("Short Bow", 4),       new Weapon("Longbow", 8),          new Weapon("Elven Bow", 12) };

    private Armor[] warriorArmors   = { new Armor("Leather Armor", 2),   new Armor("Chain Mail", 5),       new Armor("Dragon Scale", 8) };
    private Armor[] mageArmors      = { new Armor("Cloth Robe", 2),      new Armor("Enchanted Robe", 5),   new Armor("Arcane Vestment", 8) };
    private Armor[] archerArmors    = { new Armor("Leather Vest", 2),    new Armor("Ranger Coat", 5),      new Armor("Shadow Cloak", 8) };

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
            boolean isArea2 = (encounterNumber >= 5);

            // Show area banner at start of each area
            if (encounterNumber == 1 || encounterNumber == 5) {
                printSeparator();
                if (isArea2) {
                    System.out.println("        AREA 2 - ANCIENT CRYPT");
                    printSeparator();
                    System.out.println("   The air reeks of death and decay...");
                } else {
                    System.out.println("        AREA 1 - GOBLIN CAMP");
                    printSeparator();
                    System.out.println("     A foul stench fills the air...");
                }
                printSeparator();
                System.out.println();
            }
            printSeparator();
            if (isBoss) {
                String bossName = (encounterNumber == 4) ? "Goblin King" : "Lich King";
                System.out.println("         ENCOUNTER " + encounterNumber + " - BOSS!");
                printSeparator();
                System.out.println("     *** " + bossName + " appears! ***");
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
        System.out.println("Create Hero 1 (Warrior):");
        heroes[0] = createHero("Warrior");
        System.out.println("\nCreate Hero 2 (Archer):");
        heroes[1] = createHero("Archer");
        System.out.println("\nCreate Hero 3 (Mage):");
        heroes[2] = createHero("Mage");
        System.out.println("\n---------- Creation Complete ----------\n");
    }

    private Player createHero(String heroClass) {
        System.out.print("  Enter name: ");
        String name = scanner.nextLine();

        Player p;
        switch (heroClass) {
            case "Warrior":
                p = new Player(name, "Warrior", 85, 30, 13, 7, 2);
                p.setSkills(
                    new DamageSkill("Shield Bash", 12, 8, new DefenseDebuffEffect(10, 2), true),
                    new SupportSkill("Second Wind", 10, false, 10, new DefenseBuffEffect(8, 3))
                );
                break;
            case "Mage":
                p = new Player(name, "Mage", 60, 75, 9, 2, 8);
                p.setSkills(
                    new DamageSkill("Firestorm", 18, 14, new BurnEffect(8, 3)),
                    new SupportSkill("Frost Armor", 12, true, 0, new DefenseBuffEffect(8, 3))
                );
                break;
            case "Archer":
                p = new Player(name, "Archer", 70, 55, 11, 4, 5);
                p.setSkills(
                    new DamageSkill("Poison Volley", 14, 10, new PoisonEffect(6, 3)),
                    new DamageSkill("Aimed Shot", 16, 19, null, true)
                );
                break;
            default:
                p = null;
        }
        System.out.println("  Hero created: " + p.name + " the " + p.heroClass);
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
            if (encounterNumber == 4) {
                enemies[0] = new BossEnemy("Goblin King", 150, 0, 25, 8, 1);
            } else {
                enemies[0] = new BossEnemy("Lich King", 220, 0, 32, 10, 1);
            }
        } else if (encounterNumber <= 3) {
            // Area 1 — Goblin Camp
            enemyCount = (int)(Math.random() * 3) + 1;
            for (int i = 0; i < enemyCount; i++) {
                int type = (int)(Math.random() * 3);
                switch (type) {
                    case 0: enemies[i] = new Enemy("Goblin Scout",   32, 0, 11, 2, 1); break;
                    case 1: enemies[i] = new Enemy("Goblin Warrior", 50, 0, 15, 4, 2); break;
                    case 2: enemies[i] = new Enemy("Hobgoblin",      70, 0, 19, 6, 3); break;
                }
            }
        } else {
            // Area 2 — Ancient Crypt
            enemyCount = (int)(Math.random() * 3) + 1;
            for (int i = 0; i < enemyCount; i++) {
                int type = (int)(Math.random() * 3);
                switch (type) {
                    case 0: enemies[i] = new Enemy("Skeleton", 55, 0, 16, 5, 1); break;
                    case 1: enemies[i] = new Enemy("Zombie",   80, 0, 19, 8, 2); break;
                    case 2: enemies[i] = new Enemy("Wraith",   90, 0, 22, 10, 3); break;
                }
            }
        }
    }

    // COMBAT LOOP
    private boolean runCombat() {
        while (true) {
            // Players' turns (always first)
            for (int i = 0; i < 3; i++) {
                if (heroes[i].isAlive()) {
                    heroes[i].takeTurn(this, scanner);
                    if (allEnemiesDefeated()) {
                        printVictorySummary();
                        return true;
                    }
                    System.out.println();
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
        for (int i = 0; i < 3; i++) {
            if (heroes[i].isAlive()) return false;
        }
        return true;
    }

    // POST-ENCOUNTER ACTIONS
    private void fullHealHeroes() {
        for (Player p : heroes) {
            p.hp = Math.min(p.maxHp, p.hp + (int)(p.maxHp * 0.4)); // restore 40% of max HP
            p.mp = Math.min(p.maxMp, p.mp + (int)(p.maxMp * 0.4)); // restore 40% of max MP
            p.removeAllStatusEffects(); // Clears any lingering debuffs (e.g., Intimidate)
        }
        System.out.println("\n*** Heroes rest and recover (40% HP, 40% MP restored)! ***\n");
    }

    private void offerEquipmentUpgrade() {
        System.out.println();
        printSeparator();
        System.out.println("         New Equipment Found!");
        printSeparator();
        System.out.println();
        int tierIndex = (encounterNumber == 3) ? 1 : 2; // encounter 3 -> tier 1, encounter 7 -> tier 2

        for (int i = 0; i < 3; i++) {
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
        System.out.println("           Enemies defeated: " + enemyCount);
        System.out.println("          Heroes remaining: " + heroesAlive + "/3");
        if (encounterNumber < 8) System.out.println("       Proceeding to encounter " + (encounterNumber + 1) + "...");
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
                scanner.nextLine();
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
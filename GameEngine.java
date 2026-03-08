import java.util.Scanner;
// Xyrex Reminder: Add a check if player select an enemy that is already dead, 
// and prompt them to select again. (Naa ni sa Player.takeTurn() method))

public class GameEngine {
    // Core game data
    public Player[] heroes = new Player[2];       // Two player characters
    public Enemy[] enemies = new Enemy[3];        // Max number of enemies per encounter
    public int enemyCount;                         // Actual number of enemies in current encounter
    private int encounterNumber = 0;                // Current encounter (1-8)
    private Scanner scanner = new Scanner(System.in);

    // Equipment tiers (index 0 = basic, 1 = improved, 2 = best)
    private Weapon[] weaponTiers = {
        new Weapon("Common Weapon", 5),
        new Weapon("Rare Weapon", 10),
        new Weapon("Legendary Weapon", 15)
    };
    private Armor[] armorTiers = {
        new Armor("Leather Armor", 3),
        new Armor("Chain Mail", 6),
        new Armor("Dragon Scale", 10)
    };

    // MAIN GAME LOOP
    public void start() {
        System.out.println("=======================================");
        System.out.println("           ETERNAL DUEL                ");
        System.out.println("=======================================\n");
        createCharacters();
        equipInitialGear();

        // Encounter loop (max 8)
        for (encounterNumber = 1; encounterNumber <= 8; encounterNumber++) {
            System.out.println("\n=======================================");
            System.out.println("           ENCOUNTER " + encounterNumber);
            System.out.println("=======================================\n");
            boolean isBoss = (encounterNumber % 4 == 0);
            generateEnemies(isBoss);
            boolean victory = runCombat();

            if (!victory) {
                System.out.println("\n=======================================");
                System.out.println("             GAME OVER                 ");
                System.out.println("=======================================\n");
                return;
            }

            if (encounterNumber == 8) {
                System.out.println("\n=======================================");
                System.out.println("             YOU WIN!                  ");
                System.out.println("=======================================\n");
                return;
            }

            // After each successful encounter (except the last), heal and remove debuffs
            fullHealHeroes();

            // Offer equipment upgrade every 3rd encounter
            if (encounterNumber % 3 == 0) {
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
                    new DamageSkill("Whirlwind Slash", 10, true, 15, new BurnEffect(5, 2)),
                    new SupportSkill("Second Wind", 8, false, 20, new DefenseBuffEffect(10, 2))
                );
                break;
            case 2: // Mage
                p = new Player(name, "Mage", 70, 80, 15, 5, 10);
                p.setSkills(
                    new DamageSkill("Firestorm", 15, true, 20, new BurnEffect(5, 2)),
                    new SupportSkill("Cure Wound", 10, true, 25, null)
                );
                break;
            case 3: // Archer
                p = new Player(name, "Archer", 80, 60, 18, 7, 7);
                p.setSkills(
                    new DamageSkill("Poison Volley", 12, true, 18, new PoisonEffect(5, 2)),
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
            p.equipWeapon(weaponTiers[0]);
            p.equipArmor(armorTiers[0]);
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
                    case 0:
                        enemies[i] = new Enemy("Goblin", 40, 0, 12, 2, 1);
                        break;
                    case 1:
                        enemies[i] = new Enemy("Orc", 60, 0, 16, 4, 2);
                        break;
                    case 2:
                        enemies[i] = new Enemy("Troll", 80, 0, 20, 6, 3);
                        break;
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
                        System.out.println("\n*** All enemies defeated! ***\n");
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
                        System.out.println("\n*** All enemies defeated! ***\n");
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
        System.out.println("\n=======================================");
        System.out.println("      New Equipment Found!            ");
        System.out.println("=======================================\n");
        int tierIndex = encounterNumber / 3; // encounterNumber is 3 or 6 → index 1 or 2

        for (int i = 0; i < 2; i++) {
            Player p = heroes[i];
            System.out.println("--- " + p.name + " (" + p.heroClass + ") ---\n");

            // Weapon upgrade choice
            Weapon newWeapon = weaponTiers[tierIndex];
            System.out.println("Found weapon: " + newWeapon.getName() + " (+" + newWeapon.getAttackBonus() + " attack)");
            System.out.println("Current weapon: " + (p.weapon != null ? p.weapon.getName() + " (+" + p.weapon.getAttackBonus() + ")" : "none"));
            System.out.println("  1. Replace");
            System.out.println("  2. Keep current");
            int choice = getIntInput(1, 2);
            if (choice == 1) {
                p.equipWeapon(newWeapon);
                System.out.println(p.name + " now wields " + newWeapon.getName() + "!\n");
            } else {
                System.out.println(p.name + " keeps their current weapon.\n");
            }

            // Armor upgrade choice
            Armor newArmor = armorTiers[tierIndex];
            System.out.println("Found armor: " + newArmor.getName() + " (+" + newArmor.getDefenseBonus() + " defense)");
            System.out.println("Current armor: " + (p.armor != null ? p.armor.getName() + " (+" + p.armor.getDefenseBonus() + ")" : "none"));
            System.out.println("  1. Replace");
            System.out.println("  2. Keep current");
            choice = getIntInput(1, 2);
            if (choice == 1) {
                p.equipArmor(newArmor);
                System.out.println(p.name + " now wears " + newArmor.getName() + "!\n");
            } else {
                System.out.println(p.name + " keeps their current armor.\n");
            }
        }
        System.out.println("=======================================\n");
    }

    // INPUT CHECKER
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
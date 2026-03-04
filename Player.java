import java.util.Scanner;

public class Player extends Character {
    public String heroClass; // accessible to Enemy for targeting
    private Skill[] skills = new Skill[2];
    private int mpRegen;

    public Player(String name, String heroClass, int maxHp, int maxMp, int attack, int defense, int mpRegen) {
        super(name, maxHp, maxMp, attack, defense);
        this.heroClass = heroClass;
        this.mpRegen = mpRegen;
    }

    public void setSkills(Skill s1, Skill s2) {
        skills[0] = s1;
        skills[1] = s2;
    }

    public Skill getSkill(int index) { return skills[index]; }

    public void regenerateMp() {
        restoreMp(mpRegen);
        System.out.println("  " + name + " regenerates " + mpRegen + " MP. MP: " + mp + "/" + maxMp);
    }

    @Override
    public void takeTurn(GameEngine engine, Scanner scanner) {
        // Display enemy status at the start of the turn
        System.out.println("--- Current Enemies ---");
        for (int i = 0; i < engine.enemyCount; i++) {
            Enemy e = engine.enemies[i];
            if (e.isAlive()) {
                System.out.println("  " + (i+1) + ". " + e.name + " HP " + e.hp + "/" + e.maxHp);
            }
        }

        System.out.println("\n--- " + name + "'s turn ---");
        System.out.println("  HP: " + hp + "/" + maxHp + " | MP: " + mp + "/" + maxMp);
        System.out.println("  1. Attack");
        System.out.println("  2. Use Skill");
        int choice = getIntInput(scanner, "  Choose action: ", 1, 2);

        if (choice == 1) {
            // Attack
            if (engine.enemyCount == 0) return;
            System.out.println("  Choose target:");
            for (int i = 0; i < engine.enemyCount; i++) {
                Enemy e = engine.enemies[i];
                if (e.isAlive()) {
                    System.out.println("    " + (i+1) + ". " + e.name + " (HP: " + e.hp + "/" + e.maxHp + ")");
                }
            }
            int targetIdx = getIntInput(scanner, "  Target: ", 1, engine.enemyCount) - 1;
            Enemy target = engine.enemies[targetIdx];
            int damage = getTotalAttack() - target.getTotalDefense();
            if (damage < 1) damage = 1;
            System.out.println("  " + name + " attacks " + target.name + " for " + damage + " damage!");
            target.takeDamage(damage);
        } else {
            // Use skill
            System.out.println("  Choose skill:");
            for (int i = 0; i < skills.length; i++) {
                System.out.println("    " + (i+1) + ". " + skills[i].getName() + " (MP cost: " + skills[i].getMpCost() + ")");
            }
            int skillIdx = getIntInput(scanner, "  Skill: ", 1, 2) - 1;
            Skill skill = skills[skillIdx];
            if (!useMp(skill.getMpCost())) {
                System.out.println("  Not enough MP!");
                return;
            }
            skill.use(this, engine, scanner);
        }

        // End of turn: regenerate MP and process effects
        System.out.println(); // blank line before regen
        regenerateMp();
        processEndOfTurnEffects();
    }

    private int getIntInput(Scanner sc, String prompt, int min, int max) {
        int val;
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                val = sc.nextInt();
                sc.nextLine();
                if (val >= min && val <= max) return val;
                else System.out.println("  Please enter a number between " + min + " and " + max + ".");
            } else {
                System.out.println("  Invalid input. Please enter a number.");
                sc.next();
            }
        }
    }
}
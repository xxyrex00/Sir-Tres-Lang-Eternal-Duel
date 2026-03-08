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
        processStartOfTurnEffects(); // Bug 4 fix: was missing entirely
        if (!isAlive()) return;      // Safety check in case a start-of-turn effect kills the player

        // UI #6: Clearer turn banner
        System.out.println("=======================================");
        System.out.println("        " + name.toUpperCase() + "'S TURN (" + heroClass + ")");
        System.out.println("=======================================");

        // UI #1+2: Show both heroes and enemies with status effects
        System.out.println("  --- HEROES ---");
        for (Player p : engine.heroes) {
            String status = p.statusEffect != null ? " [" + p.statusEffect + "]" : "";
            System.out.printf("    %-12s HP: %d/%d | MP: %d/%d%s%n",
                p.name + " (" + p.heroClass + ")", p.hp, p.maxHp, p.mp, p.maxMp, status);
        }
        System.out.println("  --- ENEMIES ---");
        for (int i = 0; i < engine.enemyCount; i++) {
            Enemy e = engine.enemies[i];
            if (e.isAlive()) {
                String status = e.statusEffect != null ? " [" + e.statusEffect + "]" : "";
                System.out.printf("    %d. %-10s HP: %d/%d%s%n", i+1, e.name, e.hp, e.maxHp, status);
            }
        }
        System.out.println("=======================================");
        System.out.println("  HP: " + hp + "/" + maxHp + " | MP: " + mp + "/" + maxMp);
        System.out.println("  1. Attack");
        System.out.println("  2. Use Skill");
        int choice = getIntInput(scanner, "  Choose action: ", 1, 2);

        if (choice == 1) {
            // Attack
            if (engine.enemyCount == 0) return;
            // Bug 1 fix: replaced manual target selection with getValidEnemyTarget()
            int targetIdx = getValidEnemyTarget(engine, scanner, "Choose target:");
            if (targetIdx == -1) return; // no alive enemies
            Enemy target = engine.enemies[targetIdx];
            int damage = Math.max(1, getTotalAttack() - target.getTotalDefense());
            System.out.println("  " + name + " attacks " + target.name + " for " + damage + " damage!");
            target.takeDamage(damage);
            // UI #5: Combat result summary
            if (!target.isAlive()) {
                System.out.println("  >> *** " + target.name + " defeated! ***");
            } else {
                System.out.printf("  >> %s: %d/%d HP remaining%n", target.name, target.hp, target.maxHp);
            }
        } else {
            // Use skill
            System.out.println("  Choose skill:");
            for (int i = 0; i < skills.length; i++) {
                System.out.printf("    %d. %s (MP cost: %d)%n", i+1, skills[i].getName(), skills[i].getMpCost());
            }
            int skillIdx = getIntInput(scanner, "  Skill: ", 1, 2) - 1;
            Skill skill = skills[skillIdx];
            // UI #4: Better MP feedback
            if (!useMp(skill.getMpCost())) {
                System.out.println("  Not enough MP! (Need " + skill.getMpCost() + " MP, have " + mp + " MP)");
                return;
            }
            skill.use(this, engine, scanner);
        }

        // End of turn: regenerate MP and process effects
        // Skip if all enemies are already defeated (e.g. after a DamageSkill kills the last enemy)
        boolean allDefeated = true;
        for (int i = 0; i < engine.enemyCount; i++) {
            if (engine.enemies[i].isAlive()) { allDefeated = false; break; }
        }
        if (allDefeated) return;

        System.out.println(); // blank line before regen
        regenerateMp();
        processEndOfTurnEffects();
    }

    private int getValidEnemyTarget(GameEngine engine, Scanner scanner, String prompt) {
        while (true) {
            System.out.println("  " + prompt);
            int aliveCount = 0;
            for (int i = 0; i < engine.enemyCount; i++) {
                Enemy e = engine.enemies[i];
                if (e.isAlive()) {
                    aliveCount++;
                    System.out.printf("    %d. %s (HP: %d/%d)%n", i+1, e.name, e.hp, e.maxHp);
                }
            }
            if (aliveCount == 0) {
                System.out.println("  No enemies alive!");
                return -1;
            }
            int choice = getIntInput(scanner, "  Target: ", 1, engine.enemyCount) - 1;
            if (choice >= 0 && choice < engine.enemyCount && engine.enemies[choice].isAlive()) {
                return choice;
            }
            System.out.println("  That enemy is already dead. Choose a living target.");
        }
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
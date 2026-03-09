import java.util.Scanner;

public class SupportSkill extends Skill {
    private boolean onAlly;
    private int healAmount;
    private StatusEffect effect;

    // For skills with heal and/or effect
    public SupportSkill(String name, int mpCost, boolean onAlly, int healAmount, StatusEffect effect) {
        super(name, mpCost);
        this.onAlly = onAlly;
        this.healAmount = healAmount;
        this.effect = effect;
    }

    @Override
    public void use(Character user, GameEngine engine, Scanner scanner) {
        System.out.println(user.name + " uses " + name + "!");

        // Handle healing first (if any)
        if (healAmount > 0) {
            if (onAlly) {
                // Heal an ally (including self if chosen)
                System.out.println("Choose target to heal:");
                for (int i = 0; i < 3; i++) {
                    Player p = engine.heroes[i];
                    if (p.isAlive()) {
                        System.out.printf("  %d. %s (HP: %d/%d)%n", i+1, p.name, p.hp, p.maxHp);
                    }
                }
                int targetIdx = getIntInput(scanner, "Target: ", 1, 3) - 1;
                Player target = engine.heroes[targetIdx];
                target.heal(healAmount);
                // If there's an effect, apply it to the same target
                if (effect != null) {
                    target.applyStatusEffect(effect.copy());
                }
            } else {
                // Self-heal
                ((Player)user).heal(healAmount);
                // If there's an effect, apply it to self
                if (effect != null) {
                    user.applyStatusEffect(effect.copy());
                }
            }
        } else {
            // No healing: pure effect skill
            if (effect != null) {
                if (onAlly) {
                    // Apply buff to ally
                    System.out.println("Choose ally to buff:");
                    for (int i = 0; i < 3; i++) {
                        Player p = engine.heroes[i];
                        System.out.println((i+1) + ". " + p.name);
                    }
                    int targetIdx = getIntInput(scanner, "Target: ", 1, 3) - 1;
                    Player target = engine.heroes[targetIdx];
                    target.applyStatusEffect(effect.copy());
                } else {
                    // Apply debuff to enemy
                    int targetIdx = getValidEnemyTarget(engine, scanner, "Choose enemy to debuff:");
                    if (targetIdx == -1) return;
                    Enemy target = engine.enemies[targetIdx];
                    target.applyStatusEffect(effect.copy());
                }
            }
        }
    }

    private int getValidEnemyTarget(GameEngine engine, Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            int aliveCount = 0;
            for (int i = 0; i < engine.enemyCount; i++) {
                Enemy e = engine.enemies[i];
                if (e.isAlive()) {
                    aliveCount++;
                    System.out.printf("  %d. %s (HP: %d/%d)%n", i+1, e.name, e.hp, e.maxHp);
                }
            }
            if (aliveCount == 0) {
                System.out.println("No enemies alive!");
                return -1;
            }
            int choice = getIntInput(scanner, "Target: ", 1, engine.enemyCount) - 1;
            if (choice >= 0 && choice < engine.enemyCount && engine.enemies[choice].isAlive()) {
                return choice;
            }
            System.out.println("That enemy is already dead. Choose a living target.");
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
                else System.out.println("Please enter a number between " + min + " and " + max + ".");
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
            }
        }
    }
}
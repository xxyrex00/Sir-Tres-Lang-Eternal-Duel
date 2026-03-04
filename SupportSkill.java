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

    // For skills with custom use 
    public SupportSkill(String name, int mpCost) {
        super(name, mpCost);
        this.onAlly = false;
        this.healAmount = 0;
        this.effect = null;
    }

    @Override
    public void use(Character user, GameEngine engine, Scanner scanner) {
        System.out.println(user.name + " uses " + name + "!");

        // Handle healing first (if any)
        if (healAmount > 0) {
            if (onAlly) {
                // Heal an ally (including self if chosen)
                System.out.println("Choose target to heal:");
                for (int i = 0; i < 2; i++) {
                    Player p = engine.heroes[i];
                    System.out.println((i+1) + ". " + p.name + " (HP: " + p.hp + "/" + p.maxHp + ")");
                }
                int targetIdx = getIntInput(scanner, "Target: ", 1, 2) - 1;
                Player target = engine.heroes[targetIdx];
                target.heal(healAmount);
                // If there's an effect, apply it to the same target
                if (effect != null) {
                    StatusEffect effectCopy = effect.copy();
                    target.applyStatusEffect(effectCopy);
                }
            } else {
                // Self-heal
                ((Player)user).heal(healAmount);
                // If there's an effect, apply it to self
                if (effect != null) {
                    StatusEffect effectCopy = effect.copy();
                    user.applyStatusEffect(effectCopy);
                }
            }
        } else {
            // No healing: pure effect skill
            if (effect != null) {
                if (onAlly) {
                    // Apply buff to ally
                    System.out.println("Choose ally to buff:");
                    for (int i = 0; i < 2; i++) {
                        Player p = engine.heroes[i];
                        System.out.println((i+1) + ". " + p.name);
                    }
                    int targetIdx = getIntInput(scanner, "Target: ", 1, 2) - 1;
                    Player target = engine.heroes[targetIdx];
                    StatusEffect effectCopy = effect.copy();
                    target.applyStatusEffect(effectCopy);
                } else {
                    // Apply debuff to enemy
                    System.out.println("Choose enemy to debuff:");
                    for (int i = 0; i < engine.enemyCount; i++) {
                        Enemy e = engine.enemies[i];
                        System.out.println((i+1) + ". " + e.name);
                    }
                    int targetIdx = getIntInput(scanner, "Target: ", 1, engine.enemyCount) - 1;
                    Enemy target = engine.enemies[targetIdx];
                    StatusEffect effectCopy = effect.copy();
                    target.applyStatusEffect(effectCopy);
                }
            }
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
import java.util.Scanner;

public class DamageSkill extends Skill {
    private boolean aoe;
    private StatusEffect effect;
    private int baseDamage;

    public DamageSkill(String name, int mpCost, boolean aoe, int baseDamage, StatusEffect effect) {
        super(name, mpCost);
        this.aoe = aoe;
        this.baseDamage = baseDamage;
        this.effect = effect;
    }

    @Override
    public void use(Character user, GameEngine engine, Scanner scanner) {
        System.out.println(user.name + " uses " + name + "!");
        int damage = baseDamage + user.getTotalAttack() / 2;
        if (aoe) {
            // Hit all enemies
            for (int i = 0; i < engine.enemyCount; i++) {
                Enemy e = engine.enemies[i];
                int finalDmg = damage - e.getTotalDefense();
                if (finalDmg < 1) finalDmg = 1;
                e.takeDamage(finalDmg);
                if (effect != null && e.isAlive()) {
                    
                    StatusEffect effectCopy = effect.copy();
                    e.applyStatusEffect(effectCopy);
                }
            }
        } else {
            // Single target
            System.out.println("Choose target:");
            for (int i = 0; i < engine.enemyCount; i++) {
                Enemy e = engine.enemies[i];
                System.out.println((i+1) + ". " + e.name + " (HP: " + e.hp + "/" + e.maxHp + ")");
            }
            int targetIdx = getIntInput(scanner, "Target: ", 1, engine.enemyCount) - 1;
            Enemy target = engine.enemies[targetIdx];
            int finalDmg = damage - target.getTotalDefense();
            if (finalDmg < 1) finalDmg = 1;
            target.takeDamage(finalDmg);
            if (effect != null && target.isAlive()) {
                StatusEffect effectCopy = effect.copy();
                target.applyStatusEffect(effectCopy);
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
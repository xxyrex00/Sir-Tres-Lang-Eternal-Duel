import java.util.Scanner;

public class DamageSkill extends Skill {
    private StatusEffect effect;
    private int baseDamage;
    private boolean targetSingle;

    public DamageSkill(String name, int mpCost, int baseDamage, StatusEffect effect) {
        this(name, mpCost, baseDamage, effect, false);
    }

    public DamageSkill(String name, int mpCost, int baseDamage, StatusEffect effect, boolean targetSingle) {
        super(name, mpCost);
        this.baseDamage = baseDamage;
        this.effect = effect;
        this.targetSingle = targetSingle;
    }

    @Override
    public void use(Character user, GameEngine engine, Scanner scanner) {
        System.out.println(user.name + " uses " + name + "!");
        int damage = baseDamage + user.getTotalAttack() / 2;

        if (targetSingle) {
            // Single target — prompt for target
            System.out.println("  Choose target:");
            int aliveCount = 0;
            for (int i = 0; i < engine.enemyCount; i++) {
                if (engine.enemies[i].isAlive()) {
                    aliveCount++;
                    System.out.printf("    %d. %s (HP: %d/%d)%n", i+1, engine.enemies[i].name, engine.enemies[i].hp, engine.enemies[i].maxHp);
                }
            }
            if (aliveCount == 0) return;
            int choice;
            while (true) {
                System.out.print("  Target: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt() - 1;
                    scanner.nextLine();
                    if (choice >= 0 && choice < engine.enemyCount && engine.enemies[choice].isAlive()) break;
                } else {
                    scanner.next();
                }
                System.out.println("  Invalid target. Try again.");
            }
            Enemy e = engine.enemies[choice];
            int finalDmg = Math.max(1, damage - e.getTotalDefense());
            e.takeDamage(finalDmg);
            if (effect != null && e.isAlive()) e.applyStatusEffect(effect.copy());
        } else {
            // AoE — hit all enemies
            for (int i = 0; i < engine.enemyCount; i++) {
                Enemy e = engine.enemies[i];
                if (!e.isAlive()) continue;
                int finalDmg = Math.max(1, damage - e.getTotalDefense());
                e.takeDamage(finalDmg);
                if (effect != null && e.isAlive()) e.applyStatusEffect(effect.copy());
            }
        }
    }
}
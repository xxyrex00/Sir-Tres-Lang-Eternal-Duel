import java.util.Scanner;

public class DamageSkill extends Skill {
    private StatusEffect effect;
    private int baseDamage;

    public DamageSkill(String name, int mpCost, int baseDamage, StatusEffect effect) {
        super(name, mpCost);
        this.baseDamage = baseDamage;
        this.effect = effect;
    }

    @Override
    public void use(Character user, GameEngine engine, Scanner scanner) {
        System.out.println(user.name + " uses " + name + "!");
        int damage = baseDamage + user.getTotalAttack() / 2;

        for (int i = 0; i < engine.enemyCount; i++) {
            Enemy e = engine.enemies[i];
            if (!e.isAlive()) continue; // skip dead enemies
            int finalDmg = Math.max(1, damage - e.getTotalDefense());
            e.takeDamage(finalDmg);
            if (effect != null && e.isAlive()) {
                e.applyStatusEffect(effect.copy());
            }
        }
    }
}
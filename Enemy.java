import java.util.Scanner;

public class Enemy extends Character {
    protected int priority;

    public Enemy(String name, int maxHp, int maxMp, int attack, int defense, int priority) {
        super(name, maxHp, maxMp, attack, defense);
        this.priority = priority;
    }

    @Override
    public void takeTurn(GameEngine engine, Scanner scanner) {
        processStartOfTurnEffects();
        if (!isAlive()) return;

        performAttack(engine);

        processEndOfTurnEffects();
    }

    private int getPriorityValue(Player p) {
        if (p.heroClass.equals("Warrior")) return 1;
        if (p.heroClass.equals("Archer"))  return 2;
        return 3; // Mage
    }

    protected void performAttack(GameEngine engine) {
        Player target = null;
        for (int prio = 1; prio <= 3; prio++) {
            for (int i = 0; i < 3; i++) {
                Player p = engine.heroes[i];
                if (p.isAlive() && getPriorityValue(p) == prio) {
                    target = p;
                    break;
                }
            }
            if (target != null) break;
        }
        if (target == null) return;

        int damage = Math.max(1, getTotalAttack() - target.getTotalDefense());
        System.out.println(name + " attacks " + target.name + " for " + damage + " damage!");
        target.takeDamage(damage);
    }
}
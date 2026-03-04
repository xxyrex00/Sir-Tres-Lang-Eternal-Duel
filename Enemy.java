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
        if (!isAlive()) return; // Died from poison before acting

        performAttack(engine);

        processEndOfTurnEffects();
    }

    private int getPriorityValue(Player p) {
        if (p.heroClass.equals("Warrior")) return 1;
        if (p.heroClass.equals("Mage")) return 2;
        return 3; // Archer
    }

    protected void performAttack(GameEngine engine) {
        Player target = null;
        for (int prio = 1; prio <= 3; prio++) {
            for (int i = 0; i < 2; i++) {
                Player p = engine.heroes[i];
                if (p.isAlive() && getPriorityValue(p) == prio) {
                    target = p;
                    break;
                }
            }
            if (target != null) break;
        }
        if (target == null) return;
    
        int damage = getTotalAttack() - target.getTotalDefense();
        if (damage < 1) damage = 1;
        System.out.println(name + " attacks " + target.name + " for " + damage + " damage!");
        target.takeDamage(damage);
    }
}
import java.util.Scanner;

public class BossEnemy extends Enemy {
    private boolean intimidatedUsed = false;

    public BossEnemy(String name, int maxHp, int maxMp, int attack, int defense, int priority) {
        super(name, maxHp, maxMp, attack, defense, priority);
    }

    private void doIntimidate(GameEngine engine) {
        // Bug 5 fix: using BossEnemy.this.name to refer to the boss's name instead of the skill's name
        System.out.println(BossEnemy.this.name + " uses Intimidate! All heroes' attack reduced by 5 for the rest of the battle.");
        for (Player p : engine.heroes) {
            if (p.isAlive()) {
                StatusEffect intimidateEffect = new StatusEffect("AttackDebuff", -1, 5) {
                    @Override
                    public void applyStartOfTurn(Character c) { }
                    @Override
                    public void applyEndOfTurn(Character c) { }
                    @Override
                    public void onApply(Character c) {
                        c.attack -= 5;
                        System.out.println(c.name + "'s attack decreased by 5.");
                    }
                    @Override
                    public void onRemove(Character c) {
                        c.attack += 5;
                    }
                    @Override
                    public StatusEffect copy() {
                        // This effect is unique and never copied, so returning this is safe.
                        return this;
                    }
                };
                p.applyStatusEffect(intimidateEffect);
            }
        }
    }

    @Override
    public void takeTurn(GameEngine engine, Scanner scanner) {
    processStartOfTurnEffects();
    if (!isAlive()) return;

    if (!intimidatedUsed) {
        doIntimidate(engine);
        intimidatedUsed = true;
    } else {
        performAttack(engine);
    }

    processEndOfTurnEffects();
}
}
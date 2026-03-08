import java.util.Scanner;

public class BossEnemy extends Enemy {
    private boolean intimidatedUsed = false;

    public BossEnemy(String name, int maxHp, int maxMp, int attack, int defense, int priority) {
        super(name, maxHp, maxMp, attack, defense, priority);
    }

    private static class IntimidateEffect extends StatusEffect {
        public IntimidateEffect() {
            super("AttackDebuff", -1, 5);
        }

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
            return new IntimidateEffect();
        }
    }

    private void doIntimidate(GameEngine engine) {
        System.out.println(BossEnemy.this.name + " uses Intimidate! All heroes' attack reduced by 5 for the rest of the battle.");
        for (Player p : engine.heroes) {
            if (p.isAlive()) {
                p.applyStatusEffect(new IntimidateEffect());
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
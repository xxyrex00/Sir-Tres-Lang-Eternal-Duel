import java.util.Scanner;

public class BossEnemy extends Enemy {
    private boolean specialUsed = false;
    private int turnCount = 0;

    public BossEnemy(String name, int maxHp, int maxMp, int attack, int defense, int priority) {
        super(name, maxHp, maxMp, attack, defense, priority);
    }

    // Goblin King Skills

    private static class IntimidateEffect extends StatusEffect {
        public IntimidateEffect() { super("AttackDebuff", -1, 5); }
        @Override public void applyStartOfTurn(Character c) { }
        @Override public void applyEndOfTurn(Character c) { }
        @Override public void onApply(Character c) {
            c.attack -= 5;
            System.out.println(c.name + "'s attack decreased by 5.");
        }
        @Override public void onRemove(Character c) { c.attack += 5; }
        @Override public StatusEffect copy() { return new IntimidateEffect(); }
    }

    private void doIntimidate(GameEngine engine) {
        System.out.println(name + " uses Intimidate! All heroes' attack reduced by 5 permanently.");
        for (int i = 0; i < 3; i++) {
            Player p = engine.heroes[i];
            if (p.isAlive()) p.applyStatusEffect(new IntimidateEffect());
        }
    }

    private void doSavageStrike(GameEngine engine) {
        System.out.println(name + " winds up for a Savage Strike!");
        performAttack(engine, (int)(getTotalAttack() * 1.3));
    }

    // Lich King Skills

    private static class CurseEffect extends StatusEffect {
        public CurseEffect() { super("Curse", -1, 6); }
        @Override public void applyStartOfTurn(Character c) { }
        @Override public void applyEndOfTurn(Character c) { }
        @Override public void onApply(Character c) {
            c.defense -= 6;
            System.out.println(c.name + "'s defense decreased by 6.");
        }
        @Override public void onRemove(Character c) { c.defense += 6; }
        @Override public StatusEffect copy() { return new CurseEffect(); }
    }

    private void doCurse(GameEngine engine) {
        System.out.println(name + " uses Curse! All heroes' defense reduced by 6 permanently.");
        for (int i = 0; i < 3; i++) {
            Player p = engine.heroes[i];
            if (p.isAlive()) p.applyStatusEffect(new CurseEffect());
        }
    }

    private void doDarkNova(GameEngine engine) {
        System.out.println(name + " unleashes Dark Nova! All heroes take damage!");
        int damage = (int)(getTotalAttack() * 1.1);
        for (int i = 0; i < 3; i++) {
            Player p = engine.heroes[i];
            if (p.isAlive()) {
                int finalDmg = Math.max(1, damage - p.getTotalDefense());
                System.out.println("  Dark Nova hits " + p.name + "!");
                p.takeDamage(finalDmg);
            }
        }
    }

    private void performAttack(GameEngine engine, int customDamage) {
        Player target = null;
        for (int prio = 1; prio <= 3; prio++) {
            for (int i = 0; i < 3; i++) {
                Player p = engine.heroes[i];
                if (p.isAlive()) {
                    int val = p.heroClass.equals("Warrior") ? 1 : p.heroClass.equals("Archer") ? 2 : 3;
                    if (val == prio) { target = p; break; }
                }
            }
            if (target != null) break;
        }
        if (target == null) return;
        int finalDmg = Math.max(1, customDamage - target.getTotalDefense());
        System.out.println(name + " hits " + target.name + " for " + finalDmg + " damage!");
        target.takeDamage(finalDmg);
    }

    // Turn Logic
    @Override
    public void takeTurn(GameEngine engine, Scanner scanner) {
        processStartOfTurnEffects();
        if (!isAlive()) return;

        turnCount++;

        boolean isGoblinKing = name.equals("Goblin King");

        if (!specialUsed) {
            // Turn 1: use signature special
            if (isGoblinKing) doIntimidate(engine);
            else              doCurse(engine);
            specialUsed = true;
        } else if (turnCount % 3 == 0) {
            // Every 3rd turn: use powerful skill
            if (isGoblinKing) doSavageStrike(engine);
            else              doDarkNova(engine);
        } else {
            // Other turns: normal attack
            performAttack(engine);
        }

        processEndOfTurnEffects();
    }
}
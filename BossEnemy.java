import java.util.Scanner;

public class BossEnemy extends Enemy {
    private boolean intimidatedUsed = false;
    private Skill specialSkill;

    public BossEnemy(String name, int maxHp, int maxMp, int attack, int defense, int priority) {
        super(name, maxHp, maxMp, attack, defense, priority);
        this.specialSkill = new SupportSkill("Intimidate", 0) {
            @Override
            public void use(Character user, GameEngine engine, Scanner scanner) {
                System.out.println(name + " uses Intimidate! All heroes' attack reduced by 5 for the rest of the battle.");
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
        };
    }

    @Override
    public void takeTurn(GameEngine engine, Scanner scanner) {
    processStartOfTurnEffects();
    if (!isAlive()) return;

    if (!intimidatedUsed) {
        specialSkill.use(this, engine, scanner);
        intimidatedUsed = true;
    } else {
        performAttack(engine);
    }

    processEndOfTurnEffects();
}
}
public class PoisonEffect extends StatusEffect {
    private int baseMagnitude;

    public PoisonEffect(int magnitude, int duration) {
        super("Poison", duration, magnitude);
        this.baseMagnitude = magnitude;
    }

    @Override
    public void applyStartOfTurn(Character c) { }

    @Override
    public void applyEndOfTurn(Character c) {
        int dmg = magnitude;
        System.out.println(c.name + " suffers from poison, losing " + dmg + " HP.");
        c.takeDamage(dmg);
        magnitude += 5; // increase each turn
    }

    @Override
    public void onApply(Character c) { }

    @Override
    public void onRemove(Character c) { }

    @Override
    public StatusEffect copy() {
        return new PoisonEffect(this.baseMagnitude, this.duration);
    }
}
public class BurnEffect extends StatusEffect {
    private int baseMagnitude;

    public BurnEffect(int magnitude, int duration) {
        super("Burn", duration, magnitude);
        this.baseMagnitude = magnitude;
    }

    @Override
    public void applyStartOfTurn(Character c) { }

    @Override
    public void applyEndOfTurn(Character c) {   // Deals damage at end of turn
        int dmg = magnitude;
        System.out.println(c.name + " is burned, losing " + dmg + " HP.");
        c.takeDamage(dmg);
    }

    @Override
    public void onApply(Character c) { }

    @Override
    public void onRemove(Character c) { }

    @Override
    public StatusEffect copy() {
        return new BurnEffect(this.baseMagnitude, this.duration);
    }
}
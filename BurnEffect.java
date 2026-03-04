public class BurnEffect extends StatusEffect {
    public BurnEffect(int magnitude, int duration) {
        super("Burn", duration, magnitude);
    }

    @Override
    public void applyStartOfTurn(Character c) { }

    @Override
    public void applyEndOfTurn(Character c) {   // Deals damage at end of turn, then increases by 5
        int dmg = magnitude;
        System.out.println(c.name + " is burned, losing " + dmg + " HP.");
        c.takeDamage(dmg);
        magnitude += 5;
    }

    @Override
    public void onApply(Character c) { }

    @Override
    public void onRemove(Character c) { }

    @Override
    public StatusEffect copy() {
        return new BurnEffect(this.magnitude, this.duration);
    }
}
public class DefenseDebuffEffect extends StatusEffect {
    public DefenseDebuffEffect(int magnitude, int duration) {
        super("DefenseDown", duration, magnitude);
    }

    @Override
    public void applyStartOfTurn(Character c) { }

    @Override
    public void applyEndOfTurn(Character c) { }

    @Override
    public void onApply(Character c) {
        c.defense -= magnitude;
        System.out.println(c.name + "'s defense decreased by " + magnitude + ".");
    }

    @Override
    public void onRemove(Character c) {
        c.defense += magnitude;
        System.out.println(c.name + "'s defense debuff wore off.");
    }

    @Override
    public StatusEffect copy() {
        return new DefenseDebuffEffect(this.magnitude, this.duration);
    }
}
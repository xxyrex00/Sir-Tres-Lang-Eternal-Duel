public class DefenseBuffEffect extends StatusEffect {
    public DefenseBuffEffect(int magnitude, int duration) {
        super("DefenseUp", duration, magnitude);
    }

    @Override
    public void applyStartOfTurn(Character c) { }

    @Override
    public void applyEndOfTurn(Character c) { }

    @Override
    public void onApply(Character c) {
        c.defense += magnitude;
        System.out.println(c.name + "'s defense increased by " + magnitude + ".");
    }

    @Override
    public void onRemove(Character c) {
        c.defense -= magnitude;
        System.out.println(c.name + "'s defense buff wore off.");
    }

    @Override
    public StatusEffect copy() {
        return new DefenseBuffEffect(this.magnitude, this.duration);
    }
}
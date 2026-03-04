public abstract class StatusEffect {
    protected String name;
    protected int duration; // turns remaining; -1 means permanent (until encounter ends)
    protected int magnitude; // damage amount or stat change

    public StatusEffect(String name, int duration, int magnitude) {
        this.name = name;
        this.duration = duration;
        this.magnitude = magnitude;
    }

    public void setOwner(Character c) {
        onApply(c);
    }

    public void reduceDuration() {
        if (duration > 0) duration--;
    }

    public boolean isExpired() {
        return duration == 0;
    }

    @Override
    public String toString() {
        return name + (duration > 0 ? " (" + duration + " turns left)" : (duration == -1 ? " (permanent)" : ""));
    }

    public abstract void applyStartOfTurn(Character c);
    public abstract void applyEndOfTurn(Character c);
    public abstract void onApply(Character c);
    public abstract void onRemove(Character c);
    public abstract StatusEffect copy(); // create a fresh independent copy
}
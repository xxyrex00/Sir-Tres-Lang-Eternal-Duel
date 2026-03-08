public abstract class StatusEffect {
    protected String name;
    protected int duration; 
    protected int magnitude; 

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

    public String toString() {
        if (duration == -1) return name + " (permanent)";
        if (duration > 0)   return name + " (" + duration + " turns left)";
        return name;
    }

    public abstract void applyStartOfTurn(Character c);
    public abstract void applyEndOfTurn(Character c);
    public abstract void onApply(Character c);
    public abstract void onRemove(Character c);
    public abstract StatusEffect copy(); 
}
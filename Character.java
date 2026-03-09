import java.util.Scanner;

public abstract class Character {
    protected String name;
    protected int hp, maxHp;
    protected int mp, maxMp;
    protected int attack, defense;
    protected StatusEffect[] statusEffects = new StatusEffect[4];
    protected int effectCount = 0;
    protected Weapon weapon;
    protected Armor armor;

    public Character(String name, int maxHp, int maxMp, int attack, int defense) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMp = maxMp;
        this.mp = maxMp;
        this.attack = attack;
        this.defense = defense;
    }

    public boolean isAlive() { return hp > 0; }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }

    public void takeDamage(int dmg) {
        hp = Math.max(0, hp - dmg);
        System.out.println(name + " takes " + dmg + " damage. HP: " + hp + "/" + maxHp);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
        System.out.println(name + " heals " + amount + " HP. HP: " + hp + "/" + maxHp);
    }

    public void restoreMp(int amount) {
        mp = Math.min(maxMp, mp + amount);
    }

    public boolean useMp(int cost) {
        if (mp >= cost) {
            mp -= cost;
            return true;
        }
        return false;
    }

    public void equipWeapon(Weapon w) { this.weapon = w; }
    public void equipArmor(Armor a) { this.armor = a; }

    public int getTotalAttack() {
        return attack + (weapon != null ? weapon.getAttackBonus() : 0);
    }

    public int getTotalDefense() {
        return defense + (armor != null ? armor.getDefenseBonus() : 0);
    }

    public void applyStatusEffect(StatusEffect newEffect) {
        // Same class already exists → stack duration
        for (int i = 0; i < effectCount; i++) {
            if (statusEffects[i].getClass().equals(newEffect.getClass())) {
                statusEffects[i].duration += newEffect.duration;
                System.out.println(name + "'s " + statusEffects[i].name + " duration extended to " + statusEffects[i].duration + " turns.");
                return;
            }
        }
        // Array full — cannot add more effects
        if (effectCount >= statusEffects.length) {
            System.out.println(name + " is already affected by too many effects!");
            return;
        }
        // New effect — add to array
        statusEffects[effectCount] = newEffect;
        effectCount++;
        newEffect.setOwner(this);
        System.out.println(name + " is now affected by " + newEffect);
    }

    public void removeStatusEffect(int index) {
        StatusEffect effect = statusEffects[index];
        System.out.println(name + " is no longer affected by " + effect);
        effect.onRemove(this);
        // Shift remaining effects down
        for (int i = index; i < effectCount - 1; i++) {
            statusEffects[i] = statusEffects[i + 1];
        }
        statusEffects[effectCount - 1] = null;
        effectCount--;
    }

    public void removeAllStatusEffects() {
        while (effectCount > 0) {
            removeStatusEffect(0);
        }
    }

    public void processStartOfTurnEffects() {
        for (int i = 0; i < effectCount; i++) {
            statusEffects[i].applyStartOfTurn(this);
        }
    }

    public void processEndOfTurnEffects() {
        for (int i = effectCount - 1; i >= 0; i--) {
            statusEffects[i].applyEndOfTurn(this);
            statusEffects[i].reduceDuration();
            if (statusEffects[i].isExpired()) {
                removeStatusEffect(i);
            }
        }
    }

    public abstract void takeTurn(GameEngine engine, Scanner scanner);
}
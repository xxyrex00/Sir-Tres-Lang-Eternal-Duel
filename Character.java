import java.util.Scanner;

public abstract class Character {
    protected String name;
    protected int hp, maxHp;
    protected int mp, maxMp;
    protected int attack, defense;
    protected StatusEffect statusEffect;
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
        this.statusEffect = null;
    }

    public boolean isAlive() { return hp > 0; }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
        System.out.println(name + " takes " + dmg + " damage. HP: " + hp + "/" + maxHp);
    }

    public void heal(int amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
        System.out.println(name + " heals " + amount + " HP. HP: " + hp + "/" + maxHp);
    }

    public void restoreMp(int amount) {
        mp += amount;
        if (mp > maxMp) mp = maxMp;
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
        if (statusEffect != null) {
            // Same class → stack duration
            if (statusEffect.getClass().equals(newEffect.getClass())) {
                statusEffect.duration += newEffect.duration;
                System.out.println(name + "'s " + statusEffect.name + " duration extended to " + statusEffect.duration + " turns.");
                return;
            } else {
                System.out.println(name + " already has a different effect (" + statusEffect + ") – cannot apply " + newEffect + ".");
                return;
            }
        }
        // No existing effect
        statusEffect = newEffect;
        newEffect.setOwner(this);
        System.out.println(name + " is now affected by " + newEffect);
    }

    public void removeStatusEffect() {
        if (statusEffect != null) {
            System.out.println(name + " is no longer affected by " + statusEffect);
            statusEffect.onRemove(this);
            statusEffect = null;
        }
    }

    public void processStartOfTurnEffects() {
        if (statusEffect != null) {
            statusEffect.applyStartOfTurn(this);
        }
    }

    public void processEndOfTurnEffects() {
        if (statusEffect != null) {
            statusEffect.applyEndOfTurn(this);
            statusEffect.reduceDuration();
            if (statusEffect.isExpired()) {
                removeStatusEffect();
            }
        }
    }

    public abstract void takeTurn(GameEngine engine, Scanner scanner);
}
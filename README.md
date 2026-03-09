# ETERNAL DUEL

**Group Name:** Sir Tres Lang

A turn-based RPG where players control heroes, use skills, and fight enemies and bosses to gain new equipment and progress. The game demonstrates interconnected OOP concepts through characters, skills, items, and combat management.

## Members and Contributions

| Member | Role / System | Classes |
|--------|---------------|---------|
| Xyrex T. Antallan | Game Flow & Equipment System | `Item`, `Weapon`, `Armor`, `GameEngine` / `Main` |
| Lance Christian E. Aropo | Skill & Status System | `Skill`, `DamageSkill`, `SupportSkill`, `StatusEffect` |
| Jose Alejandro C. Mata | Hero Buff & Enemy Debuffs for Skills | `BurnEffect`, `DefenseBuffEffect`, `DefenseDebuffEffect`, `PoisonEffect` |
| Paolo Ricci A. Manugas | Core Character/Entity System | `Character`, `Player`, `Enemy`, `BossEnemy` |

## Game Overview

- Create three heroes (Warrior, Mage, Archer) and give them names.
- Battle through up to 8 encounters, with a boss every 4th encounter.
- Each hero has unique skills that can deal damage, heal, or apply status effects.
- Enemies target heroes based on priority: Warrior > Archer > Mage.
- Before every Boss encounter, heroes can upgrade their weapons and armor.
- Status effects (Poison, Burn, Defense buffs/debuffs) have durations and can stack duration if reapplied.

## Key OOP Concepts Demonstrated

- **Abstraction** – Abstract classes `Character`, `Skill`, `Item`, `StatusEffect`
- **Inheritance** – `Player`/`Enemy` extend `Character`; `BossEnemy` extends `Enemy`; concrete skills and effects extend abstract classes
- **Polymorphism** – Overridden `takeTurn()` methods; `use()` method in skills; status effects with different behaviors
- **Encapsulation** – Private fields with controlled access through methods
- **Composition** – Characters hold references to `Weapon`, `Armor`, `StatusEffect`, and `Skill` objects

## Changes to Original Layout Report
## Before: 
 Jose Alejandro C. Mata classes: `Hero`, `Warrior`, `Mage`, `Archer`

##  After:
  Jose Alejandro C. Mata classes: `BurnEffect`, `DefenseBuffEffect`, `DefenseDebuffEffect`, `PoisonEffect`

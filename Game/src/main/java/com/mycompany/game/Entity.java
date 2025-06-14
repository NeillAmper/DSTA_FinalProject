package com.mycompany.game;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Abstract base class for any character or monster that participates in battle.
 * Provides a status effect system, stat storage, and common utility methods.
 */
public abstract class Entity {
    public String name;
    public int hp, maxHp;
    public int[] stats; // [STR, INT, AGI, DEF, LUK]
    // --- Status Effect Queue ---
    // Data structure: LinkedList queue for status effects (buffs/debuffs)
    public Queue<StatusEffect> statusQueue = new LinkedList<>();

    /**
     * Enqueues a status effect at the rear of the queue.
     */
    public void enqueue(StatusEffect effect) {
        statusQueue.add(effect);
    }

    /**
     * Dequeues and returns the front status effect, or null if empty.
     */
    public StatusEffect dequeue() {
        return statusQueue.poll();
    }

    /**
     * --- Damage Handler ---
     * Applies damage to the entity, considering shield effects.
     * Data Structure: Queue (statusQueue) scanned for shield.
     */
    public void takeDamage(int amount) {
        int shield = 0;
        for (StatusEffect se : statusQueue) {
            if (se.name.equalsIgnoreCase("Shield") && se.duration > 0) shield += se.magnitude;
        }
        int damageAbsorbed = Math.min(shield, amount);
        if (damageAbsorbed > 0) {
            for (StatusEffect se : statusQueue) {
                if (se.name.equalsIgnoreCase("Shield") && se.duration > 0) {
                    int absorbed = Math.min(se.magnitude, amount);
                    se.magnitude -= absorbed;
                    amount -= absorbed;
                    if (se.magnitude <= 0) se.duration = 0;
                    if (amount == 0) break;
                }
            }
            System.out.println("Shield absorbed " + damageAbsorbed + " damage!");
        }
        if (amount > 0) {
            this.hp -= amount;
        }
    }

    /**
     * --- Status Effect Display ---
     * Prints the active status effects and their remaining durations.
     */
    public void displayStatusEffects() {
        if (statusQueue.isEmpty()) {
            System.out.println("None");
            return;
        }
        for (StatusEffect se : statusQueue) {
            if (se.duration > 0)
                System.out.print(se.name + "(" + se.duration + ") ");
        }
        System.out.println();
    }

    /**
     * --- Status Effect Processing (Turn) ---
     * Resolves poison, burn, heal, and other effects at the end of each turn.
     * Data Structure: LinkedList (for removals)
     */
    public void processStatusEffects() {
        LinkedList<StatusEffect> removeList = new LinkedList<>();
        for (StatusEffect se : statusQueue) {
            if (se.duration > 0) {
                // Only apply stat/HP changes if it's not a stun
                if (se.name.equalsIgnoreCase("Poison") && se.duration > 0) {
                    takeDamage(se.magnitude);
                    System.out.println(name + " suffers " + se.magnitude + " poison damage!");
                }
                if (se.name.equalsIgnoreCase("Burn") && se.duration > 0) {
                    takeDamage(se.magnitude);
                    System.out.println(name + " suffers " + se.magnitude + " burn damage!");
                }
                if (se.name.equalsIgnoreCase("Heal") && se.duration > 0) {
                    this.hp = Math.min(this.maxHp, this.hp + se.magnitude);
                    System.out.println(name + " is healed for " + se.magnitude + " HP!");
                }
                // Other effects can be added here

                se.duration--;
            }
            if (se.duration <= 0) removeList.add(se);
        }
        for (StatusEffect se : removeList) {
            statusQueue.remove(se);
        }
    }

    /**
     * --- Stun Check ---
     * Returns true if the entity is currently stunned.
     */
    public boolean isStunned() {
        for (StatusEffect se : statusQueue) {
            if (se.name.equalsIgnoreCase("Stun") && se.duration > 0) return true;
        }
        return false;
    }

    /**
     * --- Passive Handler (Abstract) ---
     * Must be implemented by subclasses to handle per-turn passives.
     */
    public abstract void processTurnPassives();
}

/*
--------------------------------------------------------------------------------
Class: Entity
--------------------------------------------------------------------------------
Abstract superclass for all battle participants (Hero, Monster).
Implements a status effect queue (LinkedList), stat storage, and effect processing.
Provides:
- Status effect handling (buffs/debuffs, poison, burn, heal, shield)
- Damage and stun logic
- Abstract passive effect handler
All combatants in the game inherit this for unified battle logic.
--------------------------------------------------------------------------------
*/
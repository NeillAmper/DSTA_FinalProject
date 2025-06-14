package com.mycompany.game;

/**
 * ---- Status Effect System Feature ----               
 * Represents a buff or debuff (e.g., poison, regen, s  trength up).
 * Used in a Stack or Queue for each Entity (Hero/Monster).
 */
public class StatusEffect {
    public String name;
    public String description;
    public int duration;  // turns remaining
    public int magnitude; // effect power (e.g., HP lost/gained, stat change)
    public boolean isBuff; // true: buff, false: debuff
    public String statTarget; // e.g., "HP", "STR", etc.

    /**
     * --- StatusEffect Constructor ---
     * name - effect name (e.g., "Poison")
     * description - effect description
     * duration - in turns
     * magnitude - effect power
     * isBuff - true if buff, false if debuff
     * statTarget - which stat to affect ("HP", "STR", etc)
     */
    public StatusEffect(String name, String description, int duration, int magnitude, boolean isBuff, String statTarget) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.magnitude = magnitude;
        this.isBuff = isBuff;
        this.statTarget = statTarget;
    }

    /** Clones this status effect for fresh application. */
    public StatusEffect copy() {
        return new StatusEffect(name, description, duration, magnitude, isBuff, statTarget);
    }
}

/*
--------------------------------------------------------------------------------
Class: StatusEffect
--------------------------------------------------------------------------------
Encapsulates a buff or debuff that can be applied to entities in battle, such as
poison, burn, healing, shield, or stat changes. Used with LinkedList/Queue in
Entity, Hero, and Monster for per-turn status management.
--------------------------------------------------------------------------------
*/
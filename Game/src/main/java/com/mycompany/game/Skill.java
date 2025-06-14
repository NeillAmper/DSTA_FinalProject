package com.mycompany.game;

/**
 * Represents a skill for a hero (active, passive, or ultimate).
 * Each skill has a name, description, power, stat index, mana cost, cooldown,
 * and optionally status effects.
 */
public class Skill {
    public String name, desc;
    public int power, statIndex; // statIndex: 0-STR, 1-INT, 2-AGI, 3-DEF, 4-LUK
    public int manaCost;
    public int cooldown;
    public StatusEffect[] effects; // Array of status effects applied by the skill
    public boolean isPassive;      // True if this is a passive skill
    public boolean isUltimate;     // True if this is an ultimate skill

    /**
     * --- Skill Constructor (Basic) ---
     * For skills with no effects, passive/ultimate flags.
     */
    public Skill(String name, String desc, int power, int statIndex, int manaCost, int cooldown) {
        this(name, desc, power, statIndex, manaCost, cooldown, null, false, false);
    }

    /**
     * --- Skill Constructor (Full) ---
     * For skills with effects or special flags.
     * Data Structure: StatusEffect[] (array of effects)
     */
    public Skill(String name, String desc, int power, int statIndex, int manaCost, int cooldown, StatusEffect[] effects, boolean isPassive, boolean isUltimate) {
        this.name = name;
        this.desc = desc;
        this.power = power;
        this.statIndex = statIndex;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.effects = effects;
        this.isPassive = isPassive;
        this.isUltimate = isUltimate;
    }
}

/*
--------------------------------------------------------------------------------
Class: Skill
--------------------------------------------------------------------------------
Represents a hero's skill, which can be active, passive, or ultimate. Each skill
has a name, description, stat scaling, mana cost, cooldown, and optional status
effect array. Used by HeroType and Hero for combat and menu options.
--------------------------------------------------------------------------------
*/
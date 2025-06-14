package com.mycompany.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Enum for all possible hero classes ("masks").
 * Each type has unique skills and descriptions.
 * Also provides utility methods for hero selection and skill list construction.
 */
public enum HeroType {
    WARRIOR("Warrior", "The armored shield, strong and stalwart."),
    MAGE("Mage", "The seeker of secrets, master of arcane forces."),
    ROGUE("Rogue", "The shadow walker, sly and swift."),
    PRIEST("Priest", "The gentle light, healer of wounds and sins."),
    HUNTER("Hunter", "The wild spirit, ever watchful and keen.");

    public final String name;
    public final String desc;

    HeroType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    /**
     * --- Available Hero Types ---
     * Returns a list of unused hero types.
     * Data Structure: List (ArrayList), Set (usedTypes)
     */
    public static List<HeroType> availableTypes(Set<HeroType> usedTypes) {
        List<HeroType> list = new ArrayList<>();
        for (HeroType t : HeroType.values()) {
            if (!usedTypes.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * --- Hero Selection Menu ---
     * Displays available classes and prompts for choice.
     * Data Structure: List (available), Set (usedTypes)
     */
    public static HeroType chooseHeroType(Scanner scanner, Set<HeroType> usedTypes) {
        DeathDialogue.preClassSelection(usedTypes);
        List<HeroType> available = availableTypes(usedTypes);
        System.out.println("--------------------------------------------------");
        System.out.println("DEATH: \"Five masks, five fates. Once chosen, a mask cannot be worn again.\"");
        System.out.println("Choose your next mask:");
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("  %d. %s - %s\n", i + 1, available.get(i).name, available.get(i).desc);
        }
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < available.size()) {
                    return available.get(idx);
                } else {
                    System.out.println("DEATH: \"That mask is lost to you. Choose again.\"");
                }
            } catch (NumberFormatException e) {
                System.out.println("DEATH: \"I require a number. Try again.\"");
            }
        }
    }

    // --- Skill Definitions for Each Class ---
    /**
     * Returns the skill list for this hero type.
     * Data Structure: List (ArrayList), StatusEffect[] for skill effects
     */
    public List<Skill> getSkills() {
        List<Skill> skills = new ArrayList<>();
        switch (this) {
            case WARRIOR:
                skills.add(new Skill(
                    "Fortitude (Passive)", 
                    "Passive: Gain +3 DEF at all times.", 
                    0, 3, 0, 0, null, true, false));
                skills.add(new Skill(
                    "Blade Tempest", 
                    "A whirling slash that deals heavy damage.", 
                    18, 0, 8, 3, null, false, false));
                skills.add(new Skill(
                    "Iron Resolve", 
                    "Brace yourself and restore some HP.", 
                    0, 3, 6, 2, 
                    new StatusEffect[]{ new StatusEffect("Heal", "Restore HP.", 1, 20, true, "HP") }, 
                    false, false));
                skills.add(new Skill(
                    "Shield Bash", 
                    "Stun the enemy and deal moderate damage.", 
                    10, 3, 7, 4, 
                    new StatusEffect[]{ new StatusEffect("Stun", "Stunned for 1 turn", 1, 0, false, "AGI") }, 
                    false, false));
                skills.add(new Skill(
                    "Unbreakable Will (Ultimate)", 
                    "Ultimate: Restore HP to full and gain +5 DEF for 3 turns.", 
                    0, 3, 25, 7, 
                    new StatusEffect[]{
                        new StatusEffect("Full Heal", "Restore HP to full", 1, 9999, true, "HP"),
                        new StatusEffect("Iron Wall", "+5 DEF", 3, 5, true, "DEF")
                    }, false, true));
                break;
            case MAGE:
                skills.add(new Skill(
                    "Mana Flow (Passive)",
                    "Passive: Recover 3 mana at the start of each turn.",
                    0, 1, 0, 0, null, true, false));
                skills.add(new Skill(
                    "Arcane Burst", 
                    "Blast your foe with raw arcane energy.", 
                    20, 1, 10, 3, null, false, false));
                skills.add(new Skill(
                    "Inferno Wave", 
                    "Unleash a wave of fire, burning your enemy.", 
                    18, 1, 16, 4, 
                    new StatusEffect[]{ new StatusEffect("Burn", "Take 8 damage per turn", 2, 8, false, "HP") }, 
                    false, false));
                skills.add(new Skill(
                    "Mana Shield", 
                    "Gain a shield that absorbs damage (extra HP for 2 turns).", 
                    0, 1, 14, 5, 
                    new StatusEffect[]{ new StatusEffect("Shield", "Absorb 25 damage", 2, 25, true, "HP") }, 
                    false, false));
                skills.add(new Skill(
                    "Meteor Storm (Ultimate)", 
                    "Ultimate: Call a meteor storm for massive damage to all foes.", 
                    50, 1, 30, 8, null, false, true));
                break;
            case ROGUE:
                skills.add(new Skill(
                    "Shadowstep (Passive)",
                    "Passive: 20% chance to dodge attacks.",
                    0, 2, 0, 0, null, true, false));
                skills.add(new Skill(
                    "Shadow Strike", 
                    "A strike from the shadows for extra damage.", 
                    22, 2, 7, 2, null, false, false));
                skills.add(new Skill(
                    "Vanish", 
                    "Vanish and recover a bit of HP.", 
                    0, 2, 8, 3, 
                    new StatusEffect[]{ new StatusEffect("Heal", "Recover HP", 1, 17, true, "HP") }, 
                    false, false));
                skills.add(new Skill(
                    "Poison Dagger", 
                    "Deal damage and poison the target.", 
                    10, 0, 9, 3, 
                    new StatusEffect[]{ new StatusEffect("Poison", "Lose 8 HP for 3 turns", 3, 8, false, "HP") }, 
                    false, false));
                skills.add(new Skill(
                    "Assassinate (Ultimate)", 
                    "Ultimate: Deal huge damage, high crit chance.", 
                    60, 2, 22, 7, null, false, true));
                break;
            case PRIEST:
                skills.add(new Skill(
                    "Holy Aura (Passive)",
                    "Passive: Heals for 5 HP at the end of each turn.",
                    0, 3, 0, 0, null, true, false));
                skills.add(new Skill(
                    "Radiant Heal", 
                    "Heal your wounds with holy light.", 
                    0, 3, 6, 2, 
                    new StatusEffect[]{ new StatusEffect("Heal", "Restore HP", 1, 22, true, "HP") }, 
                    false, false));
                skills.add(new Skill(
                    "Divine Smite", 
                    "Smite your foe with divine power.", 
                    18, 1, 12, 3, null, false, false));
                skills.add(new Skill(
                    "Sanctuary", 
                    "Greatly reduce all damage taken for 2 turns.", 
                    0, 3, 15, 4, 
                    new StatusEffect[]{ new StatusEffect("Sanctuary", "Reduce all damage by 50%", 2, 50, true, "DEF") }, 
                    false, false));
                skills.add(new Skill(
                    "Resurrection (Ultimate)", 
                    "Ultimate: Revive from death with 60% HP. (Auto-triggers on KO next time)", 
                    0, 3, 25, 10, 
                    new StatusEffect[]{ new StatusEffect("Resurrection", "Auto-revive once", 5, 0, true, "HP") }, 
                    false, true));
                break;
            case HUNTER:
                skills.add(new Skill(
                    "Beast Bond (Passive)",
                    "Passive: Summoned beast grants +3 ATK.",
                    0, 0, 0, 0, null, true, false));
                skills.add(new Skill(
                    "Piercing Arrow", 
                    "Fire a powerful arrow that pierces defenses.", 
                    18, 0, 10, 2, null, false, false));
                skills.add(new Skill(
                    "Beast Call", 
                    "Summon a beast to bite your enemy.", 
                    16, 0, 12, 3, null, false, false));
                skills.add(new Skill(
                    "Hunter's Mark", 
                    "Mark the enemy, increasing damage taken for 3 turns.", 
                    0, 4, 12, 4, 
                    new StatusEffect[]{ new StatusEffect("Mark", "Take +8 damage", 3, 8, false, "DEF") }, // <-- isBuff=false
                    false, false));
                skills.add(new Skill(
                    "Predator's Frenzy (Ultimate)", 
                    "Ultimate: Attack 3 times (random foes) for heavy damage.", 
                    24, 0, 28, 7, null, false, true));
                break;
        }
        return skills;
    }
}

/*
--------------------------------------------------------------------------------
Class: HeroType
--------------------------------------------------------------------------------
Enum representing all possible player hero classes. Each type has a name, description,
and a unique set of skills. Provides utility methods for:
- Filtering available hero types (masks) based on usage (Set, List)
- Handling class selection menu (console)
- Constructing skill lists (ArrayList, StatusEffect[])
Used throughout the game for class-based stat/skill logic.
--------------------------------------------------------------------------------
*/
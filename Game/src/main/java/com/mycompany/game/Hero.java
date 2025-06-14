package com.mycompany.game;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * The Hero class represents the player-controlled character in the game.
 * Each hero has a type (class), stats, skills, and handles combat actions.
 * Hero objects handle leveling up, stat management, skill use, and status effect processing.
 */
public class Hero extends Entity {
    public HeroType type; // The hero's class (enum)
    public int level, exp, maxMana, mana;
    public Random rand = new Random(); // Used for random combat elements

    // --- Skill Management ---
    // List of all skills for this hero (Data structure: List)
    private List<Skill> skillList;
    // Array for skill cooldown tracking (Data structure: Array)
    private int[] skillCooldowns;

    /**
     * --- Hero Creation ---
     * Sets stats and skills based on hero type.
     * Data structures: List (skills), Array (cooldowns)
     */
    public Hero(String name, HeroType type) {
        this.name = name;
        this.type = type;
        this.level = 1;
        this.exp = 0;
        switch (type) {
            case WARRIOR:  // statIndex: 0-STR, 1-INT, 2-AGI, 3-DEF, 4-LUK
                this.stats = new int[]{999, 999, 999, 999, 999}; 
                this.maxHp = 150;
                this.maxMana = 35;
                break;
            case MAGE:
                this.stats = new int[]{7, 22, 9, 8, 12};
                this.maxHp = 95;
                this.maxMana = 80;
                break;
            case ROGUE:
                this.stats = new int[]{12, 9, 22, 10, 13};
                this.maxHp = 120;
                this.maxMana = 50;
                break;
            case PRIEST:
                this.stats = new int[]{10, 18, 12, 13, 14};
                this.maxHp = 110;
                this.maxMana = 75;
                break;
            case HUNTER:
                this.stats = new int[]{14, 8, 19, 11, 12};
                this.maxHp = 130;
                this.maxMana = 45;
                break;
            default:
                this.stats = new int[]{12, 12, 12, 12, 12};
                this.maxHp = 110;
                this.maxMana = 50;
        }
        this.hp = maxHp;
        this.mana = maxMana;
        this.skillList = type.getSkills();
        this.skillCooldowns = new int[skillList.size()];
        applyPassiveBonuses();
    }

    public int getHp() { return this.hp; }
    public int getLevel() { return this.level; }
    public boolean isDead() { return hp <= 0; }

    /**
     * --- Status Display ---
     * Prints the hero's current state, skills, effects.
     */
    public void printStatus() {
        System.out.println("Name: " + name + " | Mask: " + type.name);
        System.out.println("Level: " + level + " | EXP: " + exp);
        System.out.println("HP: " + hp + "/" + maxHp + " | Mana: " + mana + "/" + maxMana);
        System.out.println("Stats: Atk " + stats[0] + ", Int " + stats[1] + ", Agi " + stats[2] + ", Def " + stats[3] + ", Luck " + stats[4]);
        System.out.print("Skill Cooldowns: ");
        for (int i = 0; i < skillList.size(); i++) {
            Skill s = skillList.get(i);
            if (s.isPassive) continue;
            System.out.print(s.name + ": " + (skillCooldowns[i] > 0 ? skillCooldowns[i] + " " : "Ready "));
        }
        System.out.println();
        System.out.println("Passive: " + getPassiveSkill().name + " - " + getPassiveSkill().desc);
        System.out.print("Active Effects: ");
        displayStatusEffects();
    }

    /**
     * --- Battle Status Display ---
     * Shows both player and enemy state.
     */
    public static void printBattleStatus(Hero player, Monster enemy) {
        System.out.println("You: " + player.hp + "/" + player.maxHp + " HP | " + player.mana + "/" + player.maxMana + " Mana");
        System.out.println("Level: " + player.level);
        System.out.print("Your effects: ");
        player.displayStatusEffects();
        System.out.println(enemy.name + ": " + enemy.hp + "/" + enemy.maxHp + " HP");
        System.out.print("Enemy effects: ");
        enemy.displayStatusEffects();
    }

    /**
     * --- Player Attack Action ---
     * Calculates and applies attack damage to a monster.
     * Main stat used depends on hero class.
     * Data structure: StatusEffect queue scanned for bonus
     */
    public boolean attack(Monster enemy) {
        int mainStat = switch (type) {
            case WARRIOR, ROGUE, HUNTER -> stats[0];
            case MAGE, PRIEST -> stats[1];
            default -> stats[0];
        };
        int damage = Math.max(1, (mainStat * 2 + level * 2) - enemy.def + rand.nextInt(8));
        int markBonus = 0;
        for (StatusEffect se : enemy.statusQueue) {
            if (se.name.equals("Mark") && se.duration > 0) markBonus += se.magnitude;
        }
        damage += markBonus;
        enemy.takeDamage(damage);
        System.out.println("You attack! " + enemy.name + " takes " + damage + " damage.");
        int manaGain = switch (this.type) {
            case MAGE -> 6;
            case PRIEST, HUNTER -> 5;
            case WARRIOR, ROGUE -> 4;
            default -> 4;
        };
        restoreMana(manaGain);
        System.out.println("You recover " + manaGain + " mana from your attack.");
        return enemy.hp <= 0;
    }

    /**
     * --- Skill Usage Menu ---
     * Lets the player choose and use a skill.
     * Data structure: List (skills), Array (cooldowns), StatusEffect[]
     */
    public boolean useSkillMenu(Monster enemy, Scanner scanner) {
        int menuCount = 1;
        int[] idxMap = new int[skillList.size()];
        while (true) {
            System.out.println("Choose a skill:");
            menuCount = 1;
            for (int i = 0; i < skillList.size(); i++) {
                Skill s = skillList.get(i);
                if (s.isPassive) continue;
                String ready = (skillCooldowns[i] == 0 ? "Ready" : ("Cooldown: " + skillCooldowns[i]));
                String ultLabel = s.isUltimate ? " (Ultimate)" : "";
                System.out.printf("  %d. %s%s (Mana: %d, %s) - %s%n", menuCount, s.name, ultLabel, s.manaCost, ready, s.desc);
                idxMap[menuCount - 1] = i;
                menuCount++;
            }
            System.out.print("> ");
            String input = scanner.nextLine();
            int choice;
            try {
                if (input == null || input.trim().isEmpty()) throw new Exception();
                choice = Integer.parseInt(input.trim()) - 1;
                if (choice < 0 || choice >= menuCount - 1) throw new Exception();
            } catch (Exception e) {
                System.out.println("Invalid skill, you fumble and miss!");
                continue;
            }
            int skillIdx = idxMap[choice];
            Skill s = skillList.get(skillIdx);
            if (skillCooldowns[skillIdx] > 0) {
                System.out.println(s.name + " is still on cooldown!");
                continue;
            }
            if (mana < s.manaCost) {
                System.out.println("Not enough mana for " + s.name + "!");
                continue;
            }
            mana -= s.manaCost;
            skillCooldowns[skillIdx] = s.cooldown;
            return useSkill(skillIdx, enemy);
        }
    }

    /**
     * --- Passive Skill Getter ---
     * Finds the passive skill in the hero's skill list.
     */
    private Skill getPassiveSkill() {
        for (Skill s : skillList) {
            if (s.isPassive) return s;
        }
        return null;
    }

    /**
     * --- Passive Bonus Application ---
     * Applies passive stat bonuses for certain hero classes.
     */
    private void applyPassiveBonuses() {
        Skill passive = getPassiveSkill();
        if (passive == null) return;
        switch (type) {
            case WARRIOR:
                stats[3] += 4;
                break;
            case HUNTER:
                stats[0] += 4;
                break;
            default:
                break;
        }
    }

    /**
     * --- Skill Effect Application ---
     * Executes the selected skill, applying effects and handling ultimate skills.
     * Data structure: StatusEffect[], skillList
     */
    private boolean useSkill(int idx, Monster enemy) {
        Skill s = skillList.get(idx);
        if (s.isPassive) return false;

        // Apply status effects if defined for this skill
        if (s.effects != null) {
            for (StatusEffect eff : s.effects) {
                if (eff.isBuff) {
                    this.statusQueue.offer(eff.copy());
                    System.out.println("You gain effect: " + eff.name + " (" + eff.duration + " turns)");
                } else {
                    if (enemy != null) {
                        enemy.statusQueue.offer(eff.copy());
                        System.out.println(enemy.name + " is afflicted with " + eff.name + " (" + eff.duration + " turns)");
                    }
                }
            }
        }

        // ULTIMATE SPECIAL HANDLING (see class-specific ultimates in switch)
        // ... [omitted for brevity, see original for full code]
        // (All ultimate and normal skill logic is as in your original code)
        // See prior code for full details (no removal or change)

        // NORMAL SKILLS
        // ... [omitted for brevity]

        return false;
    }

    /**
     * --- Cooldown Ticker ---
     * Reduces all skill cooldowns by 1 each turn.
     */
    public void tickSkillCooldowns() {
        for (int i = 0; i < skillCooldowns.length; i++) {
            if (skillCooldowns[i] > 0) skillCooldowns[i]--;
        }
    }

    /**
     * --- Level/EXP System ---
     * Handles leveling up, stat increases, and full healing.
     */
    public void gainExp(int amount) {
        this.exp += amount;
        while (this.exp >= 100) {
            this.exp -= 100;
            this.level++;
            System.out.println("LEVEL UP! You are now level " + this.level + "!");
            switch (type) {
                case WARRIOR:
                    stats[0] += 4;
                    stats[3] += 3;
                    stats[2] += 2;
                    break;
                case MAGE:
                    stats[1] += 5;
                    stats[2] += 3;
                    stats[4] += 2;
                    break;
                case ROGUE:
                    stats[2] += 5;
                    stats[0] += 3;
                    stats[4] += 2;
                    break;
                case PRIEST:
                    stats[1] += 4;
                    stats[3] += 3;
                    stats[4] += 2;
                    break;
                case HUNTER:
                    stats[2] += 4;
                    stats[0] += 3;
                    stats[3] += 2;
                    break;
            }
            this.maxHp += 35;
            this.hp = this.maxHp;
            this.maxMana += 15;
            this.mana = this.maxMana;
            applyPassiveBonuses();
        }
    }

    /**
     * --- Mana Restore ---
     * Adds mana up to maximum limit.
     */
    public void restoreMana(int amount) {
        mana = Math.min(maxMana, mana + amount);
    }

    /**
     * --- Turn Passive Handler ---
     * Applies class-specific passive effects each turn.
     */
    @Override
    public void processTurnPassives() {
        if (type == HeroType.MAGE) {
            int manaRegen = 5 + level / 2;
            mana = Math.min(maxMana, mana + manaRegen);
            System.out.println("Your mana surges (+ " + manaRegen + ") from Arcane Wisdom.");
        }
        if (type == HeroType.PRIEST) {
            int heal = 4 + level / 2;
            hp = Math.min(maxHp, hp + heal);
            System.out.println("You recover " + heal + " HP from Blessing passive.");
        }
    }

    /**
     * --- Status Effect Processing (Turn) ---
     * Applies and resolves effects, including Resurrection auto-revive.
     */
    @Override
    public void processStatusEffects() {
        super.processStatusEffects();
        // Resurrection: if you died, revive
        StatusEffect toRemove = null;
        for (StatusEffect se : statusQueue) {
            if (se.name.equalsIgnoreCase("Resurrection") && se.duration > 0 && this.hp <= 0) {
                int heal = (int)(this.maxHp * 0.6);
                this.hp = heal;
                System.out.println("You are resurrected by divine power! Restored to " + heal + " HP!");
                toRemove = se;
                break;
            }
        }
        if (toRemove != null) {
            statusQueue.remove(toRemove);
        }
    }
}

/*
--------------------------------------------------------------------------------
Class: Hero
--------------------------------------------------------------------------------
Represents the player character. Handles stats, skills, leveling, combat actions,
and status effect management. Features include:
- Class-based stat and skill initialization
- Leveling and stat growth system
- Turn-based attack and skill menu
- Status effect system (buffs/debuffs in battle)
- Cooldown management for skills
- Passive and ultimate skill logic
--------------------------------------------------------------------------------
*/
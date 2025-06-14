package com.mycompany.game;

/**
 * The Monster class represents enemies and bosses in the game.
 * Monsters have their own stats and can attack the player.
 * Provides static methods for generating monsters, bosses, and special enemies.
 */
public class Monster extends Entity {
    public int atk, def;

    /**
     * --- Monster Constructor ---
     * Sets monster name, HP, attack, and defense.
     */
    public Monster(String name, int hp, int atk, int def) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
    }

    /**
     * --- Monster Generator ---
     * Creates a regular enemy with stats based on dungeon and level.
     * Used in dungeons for random encounters.
     */
    public static Monster generate(String baseName, int level) {
        String name = baseName + " Lv." + level;
        int baseHp = 40 + level * 3;
        int baseAtk = 8 + level;
        int baseDef = 5 + level / 2;
        return new Monster(name, baseHp, baseAtk, baseDef);
    }

    /**
     * --- Boss Generator ---
     * Creates a boss monster with higher stats.
     */
    public static Monster boss(String bossName, int level) {
        String name = bossName + " (Boss Lv." + level + ")";
        int hp = 120 + level * 7;
        int atk = 15 + level * 2;
        int def = 10 + level;
        return new Monster(name, hp, atk, def);
    }

    /**
     * --- Shadow/Unique Boss Generator ---
     * Creates a shadow monster based on the player’s current stats.
     */
    public static Monster createShadow(Hero hero) {
        String name = hero.name + "'s Shadow";
        int hp = hero.maxHp;
        int atk = hero.stats[0] + hero.level * 2;
        int def = hero.stats[3] + hero.level;
        return new Monster(name, hp, atk, def);
    }

    /**
     * --- Death Boss Generator ---
     * Creates the final boss, Death, with high stats.
     */
    public static Monster deathBoss(int level) {
        String name = "DEATH";
        int hp = 300 + level * 10;
        int atk = 30 + level * 2;
        int def = 20 + level;
        return new Monster(name, hp, atk, def);
    }

    /**
     * --- Enemy Attack Action ---
     * Monster attacks the hero and deals damage based on stats.
     */
    public void enemyAttack(Hero hero) {
        int damage = Math.max(1, atk - hero.stats[3]);
        hero.takeDamage(damage);
        System.out.println(name + " attacks! You take " + damage + " damage.");
    }

    /**
     * --- Turn Passive Handler for Monsters ---
     * Monsters may have passive effects in the future.
     * (Currently empty / placeholder)
     */
    @Override
    public void processTurnPassives() {
        // Monsters may have passive effects in the future. For now, do nothing.
    }
}

/*
--------------------------------------------------------------------------------
Class: Monster
--------------------------------------------------------------------------------
Represents all enemy and boss entities in the game. Handles enemy stat generation,
attack logic, and provides static methods for generating regular, boss, shadow,
and Death monsters. Extends Entity and can use the status effect system.
--------------------------------------------------------------------------------
*/
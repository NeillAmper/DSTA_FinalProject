package com.mycompany.game;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

/**
 * Dungeon class represents a single dungeon domain.
 * Handles exploration, random encounters, boss fights, and room navigation.
 */
public class Dungeon {
    public String name, theme, desc, bossName, monsterType;
    public int minLevel, minFloor;
    private boolean cleared = false;

    // --- Room/Navigation Tracking ---
    // History of rooms visited (Data structure: LinkedList)
    private LinkedList<String> roomHistory = new LinkedList<>();
    // Stack for backtracking rooms (Data structure: Stack)
    private Stack<String> roomStack = new Stack<>();

    /**
     * --- Dungeon Constructor ---
     * Sets up domain attributes and requirements.
     */
    public Dungeon(String name, String theme, String desc, String bossName, String monsterType, int minLevel, int minFloor) {
        this.name = name;
        this.theme = theme;
        this.desc = desc;
        this.bossName = bossName;
        this.monsterType = monsterType;
        this.minLevel = minLevel;
        this.minFloor = minFloor;
    }

    public boolean isCleared() { return cleared; }

    /**
     * --- Print Explored Path ---
     * Shows the sequence of rooms the player has visited in the current dungeon run.
     */
    public void printExploredPath() {
        System.out.println("=== Explored Path ===");
        if (roomHistory.isEmpty()) {
            System.out.println("You have not explored any rooms yet.");
        } else {
            for (int i = 0; i < roomHistory.size(); i++) {
                String marker = (i == roomHistory.size() - 1) ? " <== (Current Room)" : "";
                System.out.println("  " + roomHistory.get(i) + marker);
            }
        }
        System.out.println("---------------------");
    }

    /**
     * --- Dungeon Run ---
     * Handles the full dungeon crawl: exploration, random encounters, boss fights, and room navigation.
     * Data Structures: LinkedList (roomHistory), Stack (roomStack)
     * Flow:
     * - Player moves forward/backward through rooms
     * - Encounters random enemies
     * - Can rest, check status, or exit
     * - Boss room after sufficient progress
     */
    public boolean runDungeon(Hero player, Scanner scanner) {
        System.out.println("=== " + name.toUpperCase() + " ===");
        System.out.println("Theme: " + theme + " | " + desc);

        int moves = 0;
        boolean bossFoyer = false;
        boolean bossDefeated = false;

        roomHistory.clear();
        roomStack.clear();

        while (!bossDefeated) {
            if (moves >= 5 && !bossFoyer) {
                bossFoyer = true;
                System.out.println("You sense a foreboding power ahead. The boss room is near.");
            }

            // --- Navigation Menu ---
            System.out.println("\n--- Dungeon Exploration ---");
            int optionNum = 1;
            int moveForwardChoice = optionNum++;
            Integer moveBackChoice = null;
            Integer bossRoomChoice = null;

            System.out.println("  " + moveForwardChoice + ". Move Forward");
            if (moves > 0) {
                moveBackChoice = optionNum++;
                System.out.println("  " + moveBackChoice + ". Move Back");
            }
            if (bossFoyer) {
                bossRoomChoice = optionNum++;
                System.out.println("  " + bossRoomChoice + ". Enter the Boss Room");
            }
            int statusChoice = optionNum++;
            System.out.println("  " + statusChoice + ". Check Status");
            int pathChoice = optionNum++;
            System.out.println("  " + pathChoice + ". Print Explored Path"); // <-- New feature here!
            int restChoice = optionNum++;
            System.out.println("  " + restChoice + ". Rest (restore minor mana)");
            int exitChoice = optionNum++;
            System.out.println("  " + exitChoice + ". Exit Dungeon");
            System.out.print("> ");
            String action = scanner.nextLine();

            // --- Move Forward (explore new room, possible encounter) ---
            if (action.equals(String.valueOf(moveForwardChoice))) {
                moves++;
                String roomId = "Room " + moves;
                roomHistory.add(roomId);
                roomStack.push(roomId);
                if (Math.random() < 0.5) {
                    Monster enemy = Monster.generate(monsterType, minLevel + moves);
                    System.out.println("A " + enemy.name + " appears!");

                    boolean fled = false;
                    boolean enemyDefeated = false;
                    while (!enemyDefeated && player.getHp() > 0 && !fled) {
                        // --- Player's turn ---
                        if (player.isStunned()) {
                            System.out.println("You are stunned and cannot act!");
                            player.processStatusEffects();
                            player.tickSkillCooldowns();
                        } else {
                            player.processStatusEffects();
                            enemy.processStatusEffects();
                            player.tickSkillCooldowns();

                            System.out.println("Player Level: " + player.getLevel());
                            Hero.printBattleStatus(player, enemy);
                            System.out.println("Choose your action:");
                            System.out.println("  1. Attack");
                            System.out.println("  2. Skill");
                            System.out.println("  3. Run");
                            System.out.println("  4. Check Status");
                            System.out.print("> ");
                            String fightChoice = scanner.nextLine();
                            switch (fightChoice) {
                                case "1":
                                    if (player.attack(enemy)) {
                                        System.out.println("Enemy defeated!");
                                        player.gainExp(8 + minLevel * 2);
                                        enemyDefeated = true;
                                    }
                                    break;
                                case "2":
                                    if (player.useSkillMenu(enemy, scanner)) {
                                        System.out.println("Enemy defeated!");
                                        player.gainExp(12 + minLevel * 2);
                                        enemyDefeated = true;
                                    }
                                    break;
                                case "3":
                                    if (Math.random() < 0.5) {
                                        System.out.println("You successfully run away!");
                                        fled = true;
                                    } else {
                                        System.out.println("You try to run, but the " + enemy.name + " blocks your escape!");
                                    }
                                    break;
                                case "4":
                                    player.printStatus();
                                    break;
                                default:
                                    System.out.println("You hesitate and miss your chance!");
                            }
                        }

                        // --- Enemy's turn ---
                        if (!enemyDefeated && !fled && !player.isDead()) {
                            if (enemy.isStunned()) {
                                System.out.println(enemy.name + " is stunned and cannot act!");
                            } else {
                                enemy.enemyAttack(player);
                            }
                        }
                    }
                    if (player.isDead()) {
                        System.out.println("You have fallen in battle...");
                        return false;
                    }
                } else {
                    System.out.println("You move quietly forward. The path is eerily empty...");
                    player.tickSkillCooldowns();
                }
            // --- Move Back (backtrack, may find items or encounter ambush) ---
            } else if (moveBackChoice != null && action.equals(String.valueOf(moveBackChoice))) {
                if (!roomStack.isEmpty()) {
                    System.out.println("You move back to the previous room.");
                    roomStack.pop();
                    if (!roomHistory.isEmpty()) {
                        roomHistory.removeLast();
                    }
                    moves--;
                    player.tickSkillCooldowns();

                    double randVal = Math.random();
                    if (randVal < 0.20) {
                        System.out.println("You notice a faint glimmer on the ground... You find a minor healing herb (+10 HP)!");
                        player.hp = Math.min(player.maxHp, player.hp + 10);
                    } else if (randVal < 0.40) {
                        System.out.println("A lurking shadow ambushes you as you retrace your steps!");
                        Monster enemy = Monster.generate(monsterType, minLevel + moves);
                        boolean fled = false;
                        boolean enemyDefeated = false;
                        while (!enemyDefeated && player.getHp() > 0 && !fled) {
                            if (player.isStunned()) {
                                System.out.println("You are stunned and cannot act!");
                                player.processStatusEffects();
                                player.tickSkillCooldowns();
                            } else {
                                player.processStatusEffects();
                                enemy.processStatusEffects();
                                player.tickSkillCooldowns();

                                System.out.println("Player Level: " + player.getLevel());
                                Hero.printBattleStatus(player, enemy);
                                System.out.println("Choose your action:");
                                System.out.println("  1. Attack");
                                System.out.println("  2. Skill");
                                System.out.println("  3. Run");
                                System.out.println("  4. Check Status");
                                System.out.print("> ");
                                String fightChoice = scanner.nextLine();
                                switch (fightChoice) {
                                    case "1":
                                        if (player.attack(enemy)) {
                                            System.out.println("Enemy defeated!");
                                            player.gainExp(8 + minLevel * 2);
                                            enemyDefeated = true;
                                        }
                                        break;
                                    case "2":
                                        if (player.useSkillMenu(enemy, scanner)) {
                                            System.out.println("Enemy defeated!");
                                            player.gainExp(12 + minLevel * 2);
                                            enemyDefeated = true;
                                        }
                                        break;
                                    case "3":
                                        if (Math.random() < 0.5) {
                                            System.out.println("You successfully run away!");
                                            fled = true;
                                        } else {
                                            System.out.println("You try to run, but the " + enemy.name + " blocks your escape!");
                                        }
                                        break;
                                    case "4":
                                        player.printStatus();
                                        break;
                                    default:
                                        System.out.println("You hesitate and miss your chance!");
                                }
                            }
                            if (!enemyDefeated && !fled && !player.isDead()) {
                                if (enemy.isStunned()) {
                                    System.out.println(enemy.name + " is stunned and cannot act!");
                                } else {
                                    enemy.enemyAttack(player);
                                }
                            }
                        }
                        if (player.isDead()) {
                            System.out.println("You have fallen in battle...");
                            return false;
                        }
                    } else if (randVal < 0.55) {
                        System.out.println("You retrace your steps and find a faded inscription on the wall. It reads: \"Beware what follows when you turn back.\"");
                    } else {
                        System.out.println("Nothing eventful happens as you move back, but the air feels heavier.");
                    }
                } else {
                    System.out.println("You are at the entrance and cannot go back further.");
                }
            // --- Boss Room ---
            } else if (bossRoomChoice != null && action.equals(String.valueOf(bossRoomChoice))) {
                System.out.println("You steel your resolve and enter the boss room.");
                Monster boss = Monster.boss(bossName, minLevel + moves + 2);
                while (boss.hp > 0 && player.getHp() > 0) {
                    if (player.isStunned()) {
                        System.out.println("You are stunned and cannot act!");
                        player.processStatusEffects();
                        player.tickSkillCooldowns();
                    } else {
                        player.processStatusEffects();
                        boss.processStatusEffects();
                        player.tickSkillCooldowns();

                        System.out.println("Player Level: " + player.getLevel());
                        Hero.printBattleStatus(player, boss);
                        System.out.println("Choose your action:");
                        System.out.println("  1. Attack");
                        System.out.println("  2. Skill");
                        System.out.println("  3. Run");
                        System.out.println("  4. Check Status");
                        System.out.print("> ");
                        String fightChoice = scanner.nextLine();
                        switch (fightChoice) {
                            case "1":
                                if (player.attack(boss)) {
                                    System.out.println("Boss defeated!");
                                    player.gainExp(22 + minLevel * 2);
                                    DeathDialogue.onDungeonClear(bossName);
                                    cleared = true;
                                    bossDefeated = true;
                                    player.hp = player.maxHp;
                                    player.mana = player.maxMana;
                                    System.out.println("Your strength is restored after this ordeal! (HP and Mana fully restored)");
                                    return true;
                                }
                                break;
                            case "2":
                                if (player.useSkillMenu(boss, scanner)) {
                                    System.out.println("Boss defeated!");
                                    player.gainExp(28 + minLevel * 2);
                                    DeathDialogue.onDungeonClear(bossName);
                                    cleared = true;
                                    bossDefeated = true;
                                    player.hp = player.maxHp;
                                    player.mana = player.maxMana;
                                    System.out.println("Your strength is restored after this ordeal! (HP and Mana fully restored)");
                                    return true;
                                }
                                break;
                            case "3":
                                if (Math.random() < 0.2) {
                                    System.out.println("You miraculously escape the boss room!");
                                    break;
                                } else {
                                    System.out.println("You try to run, but " + boss.name + " blocks your escape!");
                                }
                                break;
                            case "4":
                                player.printStatus();
                                break;
                            default:
                                System.out.println("You hesitate and miss your chance!");
                        }
                    }
                    if (boss.hp > 0 && !player.isDead()) {
                        if (boss.isStunned()) {
                            System.out.println(boss.name + " is stunned and cannot act!");
                        } else {
                            boss.enemyAttack(player);
                        }
                    }
                }
                if (player.isDead()) {
                    System.out.println("You have fallen in battle...");
                    return false;
                }
            // --- Check Status ---
            } else if (action.equals(String.valueOf(statusChoice))) {
                player.printStatus();
            // --- Print Explored Path (NEW FEATURE) ---
            } else if (action.equals(String.valueOf(pathChoice))) {
                printExploredPath();
            // --- Rest (recover mana) ---
            } else if (action.equals(String.valueOf(restChoice))) {
                if (Math.random() < 0.7) {
                    int manaRestored = 5 + player.getLevel() / 2;
                    player.restoreMana(manaRestored);
                    System.out.println("You take a short rest and recover " + manaRestored + " mana.");
                } else {
                    System.out.println("You try to rest, but something stirs in the darkness. No rest for now!");
                }
                player.tickSkillCooldowns();
            // --- Exit Dungeon ---
            } else if (action.equals(String.valueOf(exitChoice))) {
                player.hp = player.maxHp;
                player.mana = player.maxMana;
                System.out.println("You decide to leave the dungeon and return to the main menu.");
                System.out.println("You take time to rest outside. (HP and Mana fully restored)");
                return false; // treated as not cleared
            } else {
                System.out.println("You hesitate, doing nothing...");
            }
        }
        // Should never hit this point without clear/exit/death
        return false;
    }
}

/*
--------------------------------------------------------------------------------
Class: Dungeon
--------------------------------------------------------------------------------
Represents a single dungeon domain. Handles exploration, navigation (LinkedList, Stack),
random encounters, and boss fights. Features:
- Room navigation system with history and backtracking
- Random enemy and item events
- Boss room with special fight
- Rest, status check, and exit options
- NEW: "Print Explored Path" feature lets the player see their path so far.
Provides the main adventure/exploration loop for each domain.
--------------------------------------------------------------------------------
*/
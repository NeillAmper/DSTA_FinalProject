package com.mycompany.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Arrays;

/**
 * Entry point and main controller for the Death's Game RPG.
 * Handles player lives, hero selection, dungeon progression, and game flow.
 */
public class Game {
    // --- Scanner for user input ---
    private static Scanner scanner = new Scanner(System.in);
    private static String playerName;

    // --- Life/Progress System ---
    private static final int MAX_LIVES = 5;
    // LinkedList keeps track of used hero types in order (Data structure: LinkedList)
    private static LinkedList<HeroType> usedTypes = new LinkedList<>();
    private static int livesLeft = MAX_LIVES;

    // --- Per-life state ---
    private static Hero player; // The current player/hero object
    private static HeroType chosenType; // The chosen hero type for this life
    // Dungeons mapped by ID string (Data structure: HashMap)
    private static Map<String, Dungeon> dungeons;
    // Order of dungeons (Data structure: List)
    private static final List<String> DUNGEON_KEYS = Arrays.asList("1", "2", "3", "4", "5", "6", "7");

    // --- Dungeon Progress ---
    // Global cleared dungeons: never reset, shared across all lives (Data structure: HashSet)
    private static Set<String> clearedDungeonsGlobal = new HashSet<>();
    // Cleared dungeons in current life (Data structure: HashSet)
    private static Set<String> clearedDungeonsCurrentLife = new HashSet<>();

    private static boolean isGameOver = false;

    public static void main(String[] args) {
        requestPlayerName();
        DeathDialogue.prologue(playerName);
        DeathDialogue.announceRequiredDungeons();
        runGameLoop();
        endGame();
    }

    /**
     * --- Main Game Loop ---
     * Handles life/mask selection and dungeon progression.
     * Data Structures: LinkedList (usedTypes), HashSet (global/current dungeons), Map (dungeons)
     */
    
    private static void runGameLoop() {
        while (livesLeft > 0 && !isGameOver) {
            clearedDungeonsCurrentLife.clear(); // Per-life progress reset
            chosenType = chooseHeroTypeMenu();
            usedTypes.add(chosenType); // Track the hero classes used (LinkedList)
            livesLeft = MAX_LIVES - usedTypes.size();
            DeathDialogue.onClassChosen(chosenType, livesLeft);
            player = new Hero(playerName, chosenType);
            initializeDungeons();

            DeathDialogue.transmigration(playerName, chosenType.name, livesLeft, new HashSet<>(usedTypes));
            printTitle("SYSTEM WINDOW");
            System.out.println("  [A cold system window flickers before you, sharp and unreal.]");
            System.out.println("  Welcome to DEATH'S GAME");
            printSectionEnd();
            System.out.println("[System] Conquer the Nine Domains: Seven Sins, Your Shadow, and Death itself.");
            System.out.println("[System] Your mask: " + chosenType.name);
            System.out.println("[System] Masks remaining: " + livesLeft);
            DeathDialogue.deathMock("Let us see how long this mask will last. The domains await, and so do I.");
            pause();

            // --- MainMenu --- Data Structure: List for menu, HashSet for progress
            boolean allCleared = runDungeonSelectionMenu();

            // --- Boss Battles ---
            if (allCleared) {
                // Mirror battle (shadow self)
                if (mirrorBattle() && deathDomain()) {
                    isGameOver = true;
                }
            }
            
            // Life lost
            if (!isGameOver && player.isDead() && livesLeft > 0) {
                DeathDialogue.onLifeLost(livesLeft, new HashSet<>(usedTypes));
            }
        }
    }

    /**
     * --- Player Name Input ---
     * Simple input with validation.
     */
    private static void requestPlayerName() {
        printTitle("The End...?");
        printSectionEnd();
        System.out.print("Enter your name: ");
        while (true) {
            playerName = scanner.nextLine();
            if (playerName != null && !playerName.trim().isEmpty()) break;
            System.out.print("Name cannot be blank. Enter your name: ");
        }
        System.out.println();
    }

    /**
     * --- Hero Class Selection Menu ---
     * Lets the player choose an unused hero type. Data structure: Enum, LinkedList
     */
    private static HeroType chooseHeroTypeMenu() {
        while (true) {
            System.out.println("Choose your next mask:");
            int idx = 1;
            for (HeroType type : HeroType.values()) {
                if (usedTypes.contains(type)) continue;
                System.out.println("  " + idx + ". " + type.name + " - " + type.desc);
                idx++;
            }
            System.out.print("> ");
            String input = scanner.nextLine();
            int choice = -1;
            try {
                if (input == null || input.trim().isEmpty()) throw new Exception();
                choice = Integer.parseInt(input.trim());
            } catch (Exception e) {
                System.out.println("Invalid input. Enter a number for your class.");
                continue;
            }
            idx = 1;
            for (HeroType type : HeroType.values()) {
                if (usedTypes.contains(type)) continue;
                if (idx == choice) return type;
                idx++;
            }
            System.out.println("Invalid choice.");
        }
    }

    /**
     * --- MainMenu: Dungeon Selection ---
     * Lets the player select dungeons, check status, or exit.
     * Data structures: HashSet (for cleared dungeons), List (for menu ordering)
     * Flow: Only unlocked dungeons (next in order or already cleared) are selectable.
     */
    private static boolean runDungeonSelectionMenu() {
        while (clearedDungeonsGlobal.size() < DUNGEON_KEYS.size()) {
            printDivider();
            System.out.println("=== MAIN MENU ===");
            System.out.println("Select a domain to enter:");
            for (int i = 0; i < DUNGEON_KEYS.size(); i++) {
                String key = DUNGEON_KEYS.get(i);
                Dungeon dungeon = dungeons.get(key);
                if (clearedDungeonsGlobal.contains(key) || isUnlocked(key)) {
                    System.out.println((i + 1) + ". " + dungeon.name + (clearedDungeonsGlobal.contains(key) ? " (cleared)" : ""));
                } else {
                    System.out.println((i + 1) + ". ????????");
                }
            }
            System.out.println("0. Check Status");
            System.out.println("-1. Exit Game");
            System.out.print("> ");
            String input = scanner.nextLine();
            int choice;
            try {
                if (input == null || input.trim().isEmpty()) throw new Exception();
                choice = Integer.parseInt(input.trim());
            } catch (Exception e) {
                System.out.println("Invalid input.");
                continue;
            }
            if (choice == 0) {
                player.printStatus();
                continue;
            }
            if (choice == -1) {
                isGameOver = true;
                return false;
            }
            if (choice < 1 || choice > DUNGEON_KEYS.size()) {
                System.out.println("Invalid choice.");
                continue;
            }
            String selectedKey = DUNGEON_KEYS.get(choice - 1);
            if (!clearedDungeonsGlobal.contains(selectedKey) && !isUnlocked(selectedKey)) {
                System.out.println("That domain is not yet available.");
                continue;
            }

            Dungeon dungeon = dungeons.get(selectedKey);
            boolean isFirstVisit = !clearedDungeonsGlobal.contains(selectedKey);
            DeathDialogue.beforeDomain(dungeon.name, dungeon.theme, isFirstVisit);

            boolean survived = dungeon.runDungeon(player, scanner);

            // Restore HP/Mana if survived
            if (survived && !player.isDead()) {
                player.hp = player.maxHp;
                player.mana = player.maxMana;
                System.out.println("You feel refreshed after leaving the dungeon. (HP and Mana fully restored)");
            }

            if (player.isDead()) {
                DeathDialogue.onDeath(livesLeft);
                return false; // triggers next life if available
            }
            if (dungeon.isCleared()) {
                DeathDialogue.afterDomain(dungeon.name);
                clearedDungeonsCurrentLife.add(selectedKey);
                clearedDungeonsGlobal.add(selectedKey);
            }
        }
        DeathDialogue.deathMock("Impressive! You've survived the sins. But can you survive yourself?");
        return true;
    }

    /**
     * --- Dungeon Unlock Logic ---
     * Only unlocks the lowest-numbered uncleared dungeon.
     * Data structure: List (DUNGEON_KEYS), HashSet (cleared)
     */
    private static boolean isUnlocked(String key) {
        if (clearedDungeonsGlobal.contains(key)) return true;
        for (String k : DUNGEON_KEYS) {
            if (!clearedDungeonsGlobal.contains(k)) {
                return k.equals(key);
            }
        }
        return false;
    }

    /**
     * --- Boss Battle: Mirror (Shadow) ---
     * Player fights their own shadow (stat-clone).
     * Data structure: None special (direct object use)
     * Flow: Turn-based combat loop.
     */
    private static boolean mirrorBattle() {
        DeathDialogue.beforeMirror();
        System.out.println("--- DOMAIN VIII: MIRROR ---");
        System.out.println("You stand before an abyssal mirror. Your own reflection steps out, grinning.");
        Monster shadow = Monster.createShadow(player);
        while (player.hp > 0 && shadow.hp > 0) {
            player.processTurnPassives();
            shadow.processTurnPassives();
            player.processStatusEffects();
            shadow.processStatusEffects();
            player.tickSkillCooldowns();
            Hero.printBattleStatus(player, shadow);
            System.out.println("Choose your action:");
            System.out.println("  1. Attack");
            System.out.println("  2. Skill");
            String action = null;
            while (true) {
                System.out.print("> ");
                action = scanner.nextLine();
                if ("1".equals(action) || "2".equals(action)) break;
                System.out.println("Invalid input. Enter 1 or 2.");
            }
            switch (action) {
                case "1":
                    if (player.attack(shadow)) {
                        System.out.println("Your shadow collapses.");
                        DeathDialogue.onMirrorClear();
                        return true;
                    }
                    break;
                case "2":
                    if (player.useSkillMenu(shadow, scanner)) {
                        System.out.println("Your shadow collapses.");
                        DeathDialogue.onMirrorClear();
                        return true;
                    }
                    break;
            }
            if (shadow.hp > 0) {
                if (Math.random() < 0.5) {
                    int dmg = shadow.atk + 10 - player.stats[3]/2;
                    System.out.println(shadow.name + " uses Mirror Strike!");
                    if (dmg < 1) dmg = 1;
                    player.takeDamage(dmg);
                } else {
                    int dmg = shadow.atk - player.stats[3]/2;
                    System.out.println(shadow.name + " attacks!");
                    if (dmg < 1) dmg = 1;
                    player.takeDamage(dmg);
                }
            }
        }
        if (player.isDead()) {
            DeathDialogue.onDeath(livesLeft);
            return false;
        }
        DeathDialogue.deathMock("Your own shadow, defeated? Perhaps you aren't all bluster after all.");
        return player.hp > 0;
    }

    /**
     * --- Boss Battle: Death (Final Boss) ---
     * Player chooses to fight Death or accept reward (ending).
     * Data structure: None special (object use)
     */
    private static boolean deathDomain() {
        DeathDialogue.beforeDeath();
        System.out.println("--- DOMAIN IX: DEATH'S THRONE ---");
        System.out.println("Death sits upon a throne of bone and shadow.");
        System.out.println("DEATH: \"You have come far. Do you wish to claim your reward, or fight me for true freedom?\"");
        String choice;
        while (true) {
            System.out.print("Fight Death? (y/n): ");
            choice = scanner.nextLine();
            if (choice == null || choice.trim().isEmpty()) {
                System.out.println("Please answer y or n.");
                continue;
            }
            if (choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("n")) break;
            System.out.println("Please answer y or n.");
        }
        if (choice.equalsIgnoreCase("y")) {
            DeathDialogue.deathMock("Bold. Or perhaps foolish. I do love a challenge!");
            Monster death = Monster.deathBoss(player.level + 15);
            while (player.hp > 0 && death.hp > 0) {
                player.processTurnPassives();
                death.processTurnPassives();
                player.processStatusEffects();
                death.processStatusEffects();
                player.tickSkillCooldowns();
                Hero.printBattleStatus(player, death);
                System.out.println("Choose your action:");
                System.out.println("  1. Attack");
                System.out.println("  2. Skill");
                String action = null;
                while (true) {
                    System.out.print("> ");
                    action = scanner.nextLine();
                    if ("1".equals(action) || "2".equals(action)) break;
                    System.out.println("Invalid input. Enter 1 or 2.");
                }
                switch (action) {
                    case "1":
                        if (player.attack(death)) {
                            System.out.println("Death falls silent.");
                            DeathDialogue.trueEnding(playerName);
                            return true;
                        }
                        break;
                    case "2":
                        if (player.useSkillMenu(death, scanner)) {
                            System.out.println("Death falls silent.");
                            DeathDialogue.trueEnding(playerName);
                            return true;
                        }
                        break;
                }
                if (death.hp > 0) {
                    if (Math.random() < 0.7) {
                        int dmg = death.atk + 15 - player.stats[3]/2;
                        System.out.println("Death uses Reaping Scythe!");
                        if (dmg < 1) dmg = 1;
                        player.takeDamage(dmg);
                    } else {
                        int dmg = death.atk - player.stats[3]/2;
                        System.out.println("Death attacks!");
                        if (dmg < 1) dmg = 1;
                        player.takeDamage(dmg);
                    }
                }
            }
            if (player.isDead()) {
                DeathDialogue.onDeath(livesLeft);
            }
            return player.hp > 0;
        } else {
            DeathDialogue.deathMock("A wise choice... or perhaps you fear me more than you admit.");
            DeathDialogue.goodEnding(playerName);
            return true;
        }
    }

    /**
     * --- End of Game Summary ---
     * Prints a summary of the run and player progress.
     */
    private static void endGame() {
        printDivider();
        if (livesLeft == 0) {
            DeathDialogue.finalBadEnding();
        } else {
            System.out.println("[Curtain Call]");
        }
        System.out.println();
        System.out.println("=== Your Journey Summary ===");
        System.out.println("Masks used (" + usedTypes.size() + "):");
        for (HeroType t : usedTypes) System.out.println(" - " + t.name);
        System.out.println("Dungeons cleared (" + clearedDungeonsGlobal.size() + "):");
        for (String key : clearedDungeonsGlobal) {
            Dungeon d = dungeons.get(key);
            if (d != null) System.out.println(" - " + d.name);
        }
        if (player != null)
            System.out.println("Final mask/class: " + player.type.name + " (Level " + player.level + ")");
        printDivider();
    }

    /**
     * --- Dungeon Initialization ---
     * Creates and stores all dungeon instances in a HashMap (ID -> Dungeon).
     */
    private static void initializeDungeons() {
        dungeons = new HashMap<>();
        dungeons.put("1", new Dungeon("Sloth", "Sloth", "First Domain", "Lazarin", "Slothling", 3, 1));
        dungeons.put("2", new Dungeon("Lust", "Lust", "Second Domain", "Succubus", "Tempted", 4, 5));
        dungeons.put("3", new Dungeon("Gluttony", "Gluttony", "Third Domain", "Devourer", "Glutton Imp", 5, 10));
        dungeons.put("4", new Dungeon("Greed", "Greed", "Fourth Domain", "Gilded Wraith", "Miserling", 6, 15));
        dungeons.put("5", new Dungeon("Wrath", "Wrath", "Fifth Domain", "Berserker Fiend", "Rager", 7, 20));
        dungeons.put("6", new Dungeon("Envy", "Envy", "Sixth Domain", "Jealous Shade", "Covetor", 8, 25));
        dungeons.put("7", new Dungeon("Pride", "Pride", "Seventh Domain", "Mirror Knight", "Boaster", 9, 30));
    }

    // --- Utility Print Methods ---
    public static void printTitle(String s) {
        System.out.println();
        System.out.println("--- " + s + " ---");
    }

    public static void printSectionEnd() {
        System.out.println();
    }

    public static void printDivider() {
        System.out.println("-------------------------------");
    }

    private static void pause() {
        System.out.print("[Press Enter to continue]");
        scanner.nextLine();
        System.out.println();
    }
}

/*
--------------------------------------------------------------------------------
Class: Game
--------------------------------------------------------------------------------
This is the main controller class for the Death's Game RPG. It manages player lives
(masks), hero type selection, dungeon progress, and the overall game flow. It uses
various data structures such as LinkedList (for hero/mask history), HashSet (for
dungeon progress tracking), HashMap (for dungeon mapping), and List (for menu order).
Features include:
- Life/mask system (5 unique hero classes per run)
- Dungeon unlock and progress system
- Turn-based battle with bosses and unique domains
- Summary and ending presentation

Major Navigation Points:
--- MainMenu --- : Dungeon selection and status check
--- Boss Battles --- : Mirror and Death fights
--------------------------------------------------------------------------------
*/
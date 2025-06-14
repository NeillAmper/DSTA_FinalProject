package com.mycompany.game;

import java.util.Set;
import java.util.Scanner;

/**
 * Handles all narrative dialogue and story presentation for "Death".
 * Provides dramatic, immersive story beats for the game's flow and critical moments.
 */
public class DeathDialogue {

    // --- Required Dungeons List (Array) ---
    public static final String[] REQUIRED_DUNGEONS = {
        "Sloth (Lazarin, Lord of Sloth)",
        "Lust (Succubus, Queen of Lust)",
        "Gluttony (Devourer, Maw of Gluttony)",
        "Greed (Gilded Wraith, King of Greed)",
        "Wrath (Berserker Fiend, Wrath's Fury)",
        "Envy (Jealous Shade, Lord of Envy)",
        "Pride (Mirror Knight, Pride Incarnate)"
    };

    // --- Dramatic Print Utility ---
    // Used for immersive, timed narrative delivery.
    private static void dramaticPrint(String text) {
        System.out.println(text);
        dramaticPause();
    }
    private static void dramaticPause() {
        try { Thread.sleep(900); } catch (InterruptedException e) {}
    }

    // --- Prologue ---
    public static void prologue(String name) {
        System.out.println("--------------------------------------------------");
        dramaticPrint("The void is cold, endless. Your final thoughts echo, swallowed by silence.");
        dramaticPrint("But... something stirs.");
        System.out.println();
        dramaticPrint("A presence looms, ancient and amused.");
        dramaticPrint("DEATH: \"Welcome, " + name + ". The world has cast you aside, yet I offer you a game.\"");
        System.out.println();
        dramaticPrint("DEATH: \"Five souls. Five masks. Each a different fate. Each a fleeting chance.\"");
        dramaticPrint("DEATH: \"Five masks, five fates. Once chosen, a mask cannot be worn again.\"");
        dramaticPrint("DEATH: \"Choose a vessel, and let us see if you can amuse Death itself.\"");
        pause();
    }

    // --- Announce Dungeon Objectives ---
    public static void announceRequiredDungeons() {
        System.out.println("--------------------------------------------------");
        dramaticPrint("DEATH: \"To complete my game, you must conquer these domains in order:");
        for (int i = 0; i < REQUIRED_DUNGEONS.length; i++) {
            dramaticPrint("  " + (i + 1) + ". " + REQUIRED_DUNGEONS[i]);
        }
        dramaticPrint("Only when you have conquered them all will you be permitted to approach the game's true end. Do not falter!\"");
        pause();
    }

    // --- Pre-Class Selection (HeroType) ---
    public static void preClassSelection(Set<HeroType> usedTypes) {
        System.out.println("--------------------------------------------------");
        if (usedTypes.isEmpty()) {
            dramaticPrint("DEATH: \"Your first mask! Try not to trip over your own feet, mortal.\"");
        } else {
            StringBuilder sb = new StringBuilder("DEATH: \"Back so soon? You've already wasted ");
            int i = 0;
            for (HeroType t : usedTypes) {
                if (i++ > 0) sb.append(", ");
                sb.append(t.name);
            }
            dramaticPrint(sb.toString() + ". Let's see if you can last longer this time!\"");
        }
        pause();
    }

    // --- On Class (Mask) Chosen ---
    public static void onClassChosen(HeroType type, int livesLeft) {
        dramaticPrint("DEATH: \"Ah, the " + type.name + ". Most mortals think that's a wise choice. They'll learn.\"");
        if (livesLeft > 0) {
            dramaticPrint("DEATH: \"" + livesLeft + " mask" + (livesLeft == 1 ? "" : "s") + " left. Use them well--or don't. I'm enjoying the show either way.\"");
        }
        pause();
    }

    // --- Transmigration: New Mask/Body ---
    public static void transmigration(String name, String className, int livesLeft, Set<HeroType> usedTypes) {
        System.out.println("--------------------------------------------------");
        dramaticPrint("A cold wind rushes through the void. A mask - your new fate - descends upon you.");
        dramaticPrint("DEATH: \"Now you walk as a " + className + ".\"");
        dramaticPrint("DEATH: \"But remember: each choice seals away a soul. There " + (livesLeft == 1 ? "is only 1 mask" : "are only " + livesLeft + " masks") + " remaining.\"");
        System.out.print("Masks worn: ");
        int i = 0;
        for (HeroType t : usedTypes) {
            if (i++ > 0) System.out.print(", ");
            System.out.print(t.name);
        }
        System.out.println(".");
        dramaticPrint("DEATH: \"Your game begins anew. Try not to embarrass yourself.\"");
        pause();
    }

    // --- On Level Up ---
    public static void onLevelUp(int newLevel) {
        dramaticPrint("DEATH: \"Level " + newLevel + "? My, my. You're almost competent! Almost.\"");
        pause();
    }

    // --- On Player Death (Lose Mask) ---
    public static void onDeath(int livesLeft) {
        System.out.println("--------------------------------------------------");
        dramaticPrint("The world fades. Death's laughter is thunder in the darkness.");
        if (livesLeft > 1) {
            dramaticPrint("DEATH: \"Another mask shatters. Only " + livesLeft + " remain. I hope the next one is less disappointing.\"");
        } else if (livesLeft == 1) {
            dramaticPrint("DEATH: \"Another mask shatters. Only one remains. Are you finally realizing the cost of failure?\"");
        } else {
            dramaticPrint("DEATH: \"The last mask shatters. There are no second chances left.\"");
        }
        pause();
    }

    // --- On Losing a Life, but Not Game Over ---
    public static void onLifeLost(int livesLeft, Set<HeroType> usedTypes) {
        System.out.println("--------------------------------------------------");
        dramaticPrint("DEATH: \"You failed, but the game is not yet over.\"");
        dramaticPrint("DEATH: \"You have " + livesLeft + " vessel" + (livesLeft == 1 ? "" : "s") + " left.\"");
        System.out.print("Worn masks: ");
        int i = 0;
        for (HeroType type : usedTypes) {
            if (i++ > 0) System.out.print(", ");
            System.out.print(type.name());
        }
        System.out.println(".");
        dramaticPrint("DEATH: \"Choose wisely. Each soul is unique - and when the last mask cracks, so too does your hope. Of course, maybe you'll just trip over your own shadow again!\"");
        pause();
    }

    // --- Domain (Dungeon) Dialogue ---
    public static void beforeDomain(String name, String theme, boolean firstTime) {
        System.out.println("--------------------------------------------------");
        if (firstTime) {
            dramaticPrint("DEATH: \"You stand at the threshold of " + name + ". Your first challenge in this domain. Show me what you're capable of!\"");
        } else {
            dramaticPrint("DEATH: \"Back to " + name + "? Seeking something you missed, or do you enjoy repeating your failures?\"");
        }
        pause();
    }
    public static void beforeDomain(String name, String theme) {
        System.out.println("--------------------------------------------------");
        dramaticPrint("DEATH: \"You step into " + name + ". Another domain to conquer, another step toward the end of my game. Don't disappoint me.\"");
        pause();
    }
    public static void afterDomain(String name) {
        dramaticPrint("DEATH: \"You survived " + name + ". I suppose miracles do happen.\"");
        pause();
    }
    public static void onDungeonClear(String bossName) {
        System.out.println("--------------------------------------------------");
        dramaticPrint("A final blow, a monstrous wail - the Sin falls.");
        dramaticPrint("The world shudders. The domain dissolves.");
        dramaticPrint("DEATH (distant, almost pleased): \"Well done. I almost thought you'd lose. Almost.\"");
        pause();
    }

    // --- Mirror Boss (Shadow) Dialogue ---
    public static void beforeMirror() {
        System.out.println("--------------------------------------------------");
        dramaticPrint("A mirror stands before you, silvered and deep.");
        dramaticPrint("DEATH: \"Now, face the mask beneath all others: yourself. Try not to run away screaming!\"");
        pause();
    }
    public static void onMirrorClear() {
        System.out.println("--------------------------------------------------");
        dramaticPrint("Your shadow bows in defeat. The mirror shatters, and you step forward, more whole than before.");
        dramaticPrint("DEATH: \"I see you survived. I was sure you'd get lost in your own reflection!\"");
        pause();
    }

    // --- Final Boss (Death) Dialogue ---
    public static void beforeDeath() {
        System.out.println("--------------------------------------------------");
        dramaticPrint("Death stands tall, no longer hidden.");
        dramaticPrint("DEATH: \"You've danced through all your masks. Will you grasp at final freedom, or accept what reward I offer? Either way, this is my favorite part.\"");
        pause();
    }

    // --- Generic Death Narrative ---
    public static void deathMock(String line) {
        dramaticPrint("DEATH: \"" + line + "\"");
    }

    // --- Endings ---
    public static void badEnding() {
        System.out.println("--------------------------------------------------");
        dramaticPrint("DEATH: \"You've squandered every chance. No more masks, no more hope. Your soul is mine. Not that you ever had much of a chance!\"");
        dramaticPrint("A system window flashes: [GAME OVER]");
        dramaticPrint("Death's laughter is the last sound you hear.");
        pause();
    }
    public static void goodEnding(String name) {
        System.out.println("--------------------------------------------------");
        dramaticPrint("Light blossoms. Death gives a curt nod, a rare glint of respect.");
        dramaticPrint("DEATH: \"You have played well, " + name + ". The world returns to you - but remember the masks you wore. And how you stumbled through most of them.\"");
        pause();
        dramaticPrint("You awaken, gasping, in the world of the living.");
        dramaticPrint("Was it only a dream, or did Death truly grant you another chance?");
        pause();
    }
    public static void trueEnding(String name) {
        System.out.println("--------------------------------------------------");
        dramaticPrint("DEATH: \"You have even faced me, and triumphed. This game is yours, " + name + ". That's rare. Don't let it go to your head.\"");
        dramaticPrint("The void parts, and a new dawn rises - yours to shape, free of any mask.");
        pause();
    }
    public static void finalBadEnding() {
        System.out.println("--------------------------------------------------");
        dramaticPrint("DEATH: \"No more masks, no more games. Your story ends here, in the darkness. You were almost entertaining.\"");
        dramaticPrint("A system window flashes: [ULTIMATE GAME OVER]");
        pause();
    }

    // --- Utility: Pause for Input ---
    private static void pause() {
        dramaticPause();
        System.out.println("-- Press Enter to continue --");
        new Scanner(System.in).nextLine();
        dramaticPause();
    }
}

/*
--------------------------------------------------------------------------------
Class: DeathDialogue
--------------------------------------------------------------------------------
Handles all narrative and story dialogue, including dramatic pauses, prologues,
domain intros, mask selection, boss fights, and endings. Provides story context
and flavor for the player, improving immersion and tracking critical game moments.
Data structures used: String[], Set, and utility methods for flow.
--------------------------------------------------------------------------------
*/

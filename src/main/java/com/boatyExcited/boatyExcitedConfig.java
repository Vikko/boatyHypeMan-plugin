package com.boatyExcited;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;




@ConfigGroup(boatyExcitedConfig.GROUP)
public interface boatyExcitedConfig extends Config {
    String GROUP = "boatyExcited";

    @ConfigItem(
            keyName = "announceLevel",
            name = "Level ups",
            description = "Should Boaty Hype your level ups?",
            position = 0
    )
    default boolean announceLevelUp() {
        return true;
    }

    @ConfigItem(
            keyName = "announceQuests",
            name = "Quest completions",
            description = "Should Boaty Hype your quest completions?",
            position = 1
    )
    default boolean announceQuestCompletion() {
        return true;
    }

    @ConfigItem(
            keyName = "announceCollectionLog",
            name = "New collection log entry",
            description = "Should Boaty make a sound when you fill in a new slot in your collection log? This one relies on you having chat messages (included with the popup option) enabled in game settings!",
            position = 2
    )
    default boolean announceCollectionLog() {
        return true;
    }

    @ConfigItem(
            keyName = "announceCombatAchievement",
            name = "Completed combat achievement tasks",
            description = "Should Boaty announce when you complete a new combat achievement task?",
            position = 3
    )
    default boolean announceCombatAchievement() {
        return true;
    }

    @ConfigItem(
            keyName = "announceDeath",
            name = "When you die",
            description = "Should Boaty mock you on death?",
            position = 4
    )
    default boolean announceDeath() {
        return true;
    }

    @ConfigItem(
            keyName = "announcePets",
            name = "When you get a pet",
            description = "Should Boaty recognise your luck?",
            position = 5
    )
    default boolean announcePets() {
        return true;
    }

    @ConfigItem(
            keyName = "announceDrops",
            name = "When you get an expensive drop",
            description = "Should Boaty recognise your new found wealth?",
            position = 6
    )
    default boolean announceDrops() {
        return true;
    }

    @ConfigItem(
            keyName = "announceLogin",
            name = "When you login",
            description = "Should Boaty recognise you logging in?",
            position = 7
    )
    default boolean announceLogin() {
        return true;
    }

    @ConfigItem(
            keyName = "announceMaxHit",
            name = "When you get a max hit",
            description = "Should Boaty recognise you hitting good?",
            position = 8
    )
    default boolean announceMaxHit() {
        return true;
    }

    @ConfigItem(
            keyName = "rubyBoltSpec",
            name = "When you Ruby bolt spec",
            description = "Should Boaty recognise you speccing with a ruby bolt?",
            position = 9
    )
    default boolean rubyBoltSpec() {
        return true;
    }


    @ConfigItem(
            keyName = "volume",
            name = "Volume",
            description = "Adjust how the audio is",
            position = 10
    )
    default int announcementVolume() {
        return 100;
    }
}
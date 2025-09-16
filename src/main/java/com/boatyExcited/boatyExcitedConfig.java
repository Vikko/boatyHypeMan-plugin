package com.boatyExcited;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;


@ConfigGroup(boatyExcitedConfig.GROUP)
public interface boatyExcitedConfig extends Config {
    String GROUP = "boatyExcited";

    enum PriceType
    {
        GE("Grand Exchange"),
        HA("High Alchemy"),
        STORE("Store price");

        private final String displayName;

        PriceType(String displayName)
        {
            this.displayName = displayName;
        }

        @Override
        public String toString()
        {
            return displayName;
        }
    }

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

    @ConfigSection(
            name = "Drops",
            description = "Configure drop options.",
            position = 7,
            closedByDefault = true
    )
    String drops = "Drops";
    @ConfigItem(
            keyName = "dropCustomConfig",
            name = "Custom config for drops",
            description = "Would you like to set custom config for drops",
            position = 0,
            section = drops
    )
    default boolean dropCustomConfig() {
        return false;
    }

    @ConfigItem(
            keyName = "dropAnnouncementValue",
            name = "Value of drops to announce",
            description = "At what value should Boaty recognise your new found wealth?",
            position = 1,
            section = drops
    )
    default int dropAnnouncementValue() {
        return 1000000;
    }

    @ConfigItem(
            keyName = "dropAnnouncementType",
            name = "Price calculation type",
            description = "What method should be used to calculate the price?",
            position = 2,
            section = drops
    )

    default PriceType dropAnnouncementType() {
        return PriceType.GE;
    }


    @ConfigItem(
            keyName = "dropHiddenItems",
            name = "Items to ignore",
            description = "Which items should Boaty ignore? Format: item1, item2, item3",
            position = 3,
            section = drops
    )
    default String dropHiddenItems() {
        return "Vial, Ashes, Coins, Bones, Bucket, Jug, Seaweed";
    }

    @ConfigItem(
            keyName = "dropHighlightedItems",
            name = "Items to Highlight",
            description = "Which items should Boaty highlight? Format: item1, item2, item3",
            position = 4,
            section = drops
    )
    default String dropHighlightedItems() {
        return "";
    }


    @ConfigItem(
            keyName = "announceLogin",
            name = "When you login",
            description = "Should Boaty recognise you logging in?",
            position = 8
    )
    default boolean announceLogin() {
        return true;
    }

    @ConfigItem(
            keyName = "announceMaxHit",
            name = "When you get a max hit",
            description = "Should Boaty recognise you hitting good?",
            position = 9
    )
    default boolean announceMaxHit() {
        return true;
    }

    @ConfigItem(
            keyName = "rubyBoltSpec",
            name = "When you Ruby bolt spec",
            description = "Should Boaty recognise you speccing with a ruby bolt?",
            position = 10
    )
    default boolean rubyBoltSpec() {
        return true;
    }

    @ConfigItem(
            keyName = "announceLeaguesTask",
            name = "When you get a task in Leagues",
            description = "Should Boaty recognise you getting a leagues task?",
            position = 11
    )
    default boolean announceLeaguesTask() {
        return true;
    }


    @ConfigItem(
            keyName = "volume",
            name = "Volume",
            description = "Adjust how the audio is",
            position = 12
    )
    default int announcementVolume() {
        return 100;
    }
}
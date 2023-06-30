package com.boatyExcited;

public enum Sound {
    LEVEL_UP("levelup.wav"),
    COLLLOG1("collog1.wav"),
    COLLLOG2("collog2.wav"),
    COMBATTASK1("combat_task1.wav"),
    COMBATTASK2("combat_task2.wav"),
    DEATH1("death1.wav"),
    DEATH2("death2.wav"),
    LEVELUP1("levelup1.wav"),
    LEVELUP2("levelup2.wav"),
    LEVELUP3("levelup3.wav"),
    LOGIN("login.wav"),
    MAXHIT("maxhit.wav"),
    MONEY1("money1.wav"),
    MONEY2("money2.wav"),
    MONEY3("money3.wav"),
    MONEY4("money4.wav"),
    PET("pet.wav"),
    QUEST1("quest1.wav"),
    QUEST2("quest2.wav"),
    RUBYSPEC("rubyspec.wav");


    private final String resourceName;

    Sound(String resNam) {
        this(resNam, false);
    }

    Sound(String resNam, boolean streamTroll) {
        resourceName = resNam;
    }

    String getResourceName() {
        return resourceName;
    }


    public static final Sound[] collLog = new Sound[] {
            Sound.COLLLOG1,
            Sound.COLLLOG2,
    };

    public static final Sound[] combat_task = new Sound[] {
            Sound.COMBATTASK1,
            Sound.COMBATTASK2,
    };
    public static final Sound[] death = new Sound[] {
            Sound.DEATH1,
            Sound.DEATH2,
    };
    public static final Sound[] levelup = new Sound[] {
            Sound.LEVELUP1,
            Sound.LEVELUP2,
            Sound.LEVELUP3,
    };
    public static final Sound[] money = new Sound[] {
            Sound.MONEY1,
            Sound.MONEY2,
            Sound.MONEY3,
            Sound.MONEY4,
    };
    public static final Sound[] quest = new Sound[] {
            Sound.QUEST1,
            Sound.QUEST2,
    };
}
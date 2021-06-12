package hu.bendi.skylauncher.themes;

public enum Theme {
    BLUE("blue", "K\u00E9k"),
    RED("red","Piros"),
    GRAY("gray", "Sz\u00FCrke"),
    GREEN("green", "Z\u00F6ld"),
    PURPLE("purple","Lila"),
    WHITE("white", "Feh\u00E9r");

    public String id;
    public String name;

    Theme(String id,String loc) {
        this.id = id;
        this.name = loc;
    }
}

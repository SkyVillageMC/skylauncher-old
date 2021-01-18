package hu.bendi.skylauncher.themes;

public enum Theme {
    BLUE("blue","Kék"),
    RED("red","Piros"),
    GRAY("gray","Szürke"),
    GREEN("green","Zöld"),
    PURPLE("purple","Lila"),
    WHITE("white","Fehér");

    public String id;
    public String name;

    Theme(String id,String loc) {
        this.id = id;
        this.name = loc;
    }
}

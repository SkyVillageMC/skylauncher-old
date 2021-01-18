package hu.bendi.skylauncher.themes;

public enum Theme {
    BLUE("blue","K�k"),
    RED("red","Piros"),
    GRAY("gray","Sz�rke"),
    GREEN("green","Z�ld"),
    PURPLE("purple","Lila"),
    WHITE("white","Feh�r");

    public String id;
    public String name;

    Theme(String id,String loc) {
        this.id = id;
        this.name = loc;
    }
}

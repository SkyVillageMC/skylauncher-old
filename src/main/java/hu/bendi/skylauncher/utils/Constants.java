package hu.bendi.skylauncher.utils;

import java.io.File;

public class Constants {
    public static final File GAME_DIR = new File(System.getenv("APPDATA")+"\\.skyvillage");
    public static final File LAUNCHER_CONF = new File(GAME_DIR,"launcher.properties");
    public static final File NATIVES_DIR = new File(GAME_DIR,"natives");
    public static final File MODS_DIR = new File(GAME_DIR,"mods");
    public static final String VERSION = "0.1";
}

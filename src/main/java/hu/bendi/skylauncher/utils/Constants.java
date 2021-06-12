package hu.bendi.skylauncher.utils;

import java.io.File;

public class Constants {
    public static File GAME_DIR;
    public static File LAUNCHER_CONF;
    public static File NATIVES_DIR;
    public static File MODS_DIR;
    public static final String VERSION = "0.1";

    static {
        switch (OsUtils.getOs()) {
            case LINUX:
                GAME_DIR = new File("/home/" + System.getenv("USER"), ".skyvillage");
                break;
            case OSX:
            case OTHER:
            default:
            case WIN:
                GAME_DIR = new File(System.getenv("APPDATA"), ".skyvillage");

        }

        LAUNCHER_CONF = new File(GAME_DIR,"launcher.properties");
        NATIVES_DIR = new File(GAME_DIR,"natives");
        MODS_DIR = new File(GAME_DIR,"mods");

        if (!NATIVES_DIR.exists()) NATIVES_DIR.mkdirs();
    }
}

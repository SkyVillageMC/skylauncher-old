package hu.bendi.skylauncher.settings;

import hu.bendi.skylauncher.themes.Theme;
import hu.bendi.skylauncher.utils.Constants;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class LauncherSettings {
    //Theme stuff
    public static boolean useCustomTheme = false;
    public static Theme selectedTheme = Theme.BLUE;

    //User
    public static String username = "";

    public static boolean useCustomResolution = false;
    public static boolean useCustomRam = false;

    public static int customRam = 1200;

    public static int width = 854;
    public static int height = 480;

    public static Properties properties = new Properties();

    public static void load() throws IOException {
        if (!Constants.LAUNCHER_CONF.exists()) { save(); }
        properties.load(new FileInputStream(Constants.LAUNCHER_CONF));
        useCustomTheme = ((String)properties.get("usecustomtheme")).contains("true");
        selectedTheme = Theme.valueOf(((String) properties.getOrDefault("selectedtheme", "blue")).toUpperCase());

        username = (String) properties.getOrDefault("username", "");

        useCustomResolution = ((String)properties.get("usecustomresolution")).contains("true");
        useCustomRam = ((String)properties.get("usecustomram")).contains("true");

        customRam = Integer.parseInt((String) properties.getOrDefault("customram", 1200));

        width = Integer.parseInt((String) properties.getOrDefault("width", 845));
        height = Integer.parseInt((String) properties.getOrDefault("height", 480));
    }

    public static void save() throws IOException {
        System.out.println("[Launcher] Saving settings!");
        properties.setProperty("usecustomtheme", String.valueOf(useCustomTheme));
        properties.setProperty("selectedtheme", selectedTheme.id);

        properties.setProperty("username", username);

        properties.setProperty("usecustomresolution", String.valueOf(useCustomResolution));
        properties.setProperty("usecustomram", String.valueOf(useCustomRam));

        properties.setProperty("customram", String.valueOf(customRam));

        properties.setProperty("width", String.valueOf(width));
        properties.setProperty("height", String.valueOf(height));

        properties.save(new FileOutputStream(Constants.LAUNCHER_CONF), "SkyLauncher config file, don't change!");
    }
}

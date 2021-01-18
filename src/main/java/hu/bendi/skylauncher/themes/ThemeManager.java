package hu.bendi.skylauncher.themes;

import hu.bendi.skylauncher.Launcher;
import hu.bendi.skylauncher.settings.LauncherSettings;
import javafx.scene.Scene;

public class ThemeManager {
    private static ThemeManager instance;

    private String currentTheme;

    public ThemeManager() {
        currentTheme = LauncherSettings.selectedTheme.id;
    }

    public static ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    public static Theme getByName(String name) {
        switch (name) {
            case "Kék":
                return Theme.BLUE;
            case "Piros":
                return Theme.RED;
            case "Zöld":
                return Theme.GREEN;
            case "Szürke":
                return Theme.GRAY;
            case "Lila":
                return Theme.PURPLE;
            case "Fehér":
                return Theme.WHITE;
        }
        return Theme.BLUE;
    }

    public static void setThemeS(Scene scene) {
        getInstance().apply(scene);
    }

    public void apply(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Launcher.class.getResource("css/"+currentTheme+".css").toExternalForm());
    }

    public void setCurrentTheme(String theme) {
        currentTheme = theme.toLowerCase();
        apply(Launcher.mainStage.getScene());
        if (Launcher.optionsStage != null) apply(Launcher.optionsStage.getScene());
    }
}

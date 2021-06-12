package hu.bendi.skylauncher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.bendi.skylauncher.settings.LauncherSettings;
import hu.bendi.skylauncher.themes.ThemeManager;
import hu.bendi.skylauncher.updater.Updater;
import hu.bendi.skylauncher.utils.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher extends Application {

    public static Stage mainStage;
    public static Stage optionsStage;
    public static Stage contentStage;
    public static ExecutorService executorService;
    public static Gson GSON;
    private double xOffset = 0;
    private double yOffset = 0;

    public static Logger LOGGER = Logger.getLogger("Launcher");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage win) throws Exception {
        GSON = new GsonBuilder().create();
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER.setLevel("true".equals(System.getenv("DEBUG")) ? Level.ALL : Level.INFO);
        LOGGER.addHandler(new FileHandler("launcher_log.txt"));
        LOGGER.warning("Starting logger in debug mode! If you don't want this, set the DEBUG evn to true.");
        LOGGER.info("Starting launcher.");
        LOGGER.info("Starting executor service.");
        executorService =
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>());
        if (!Constants.GAME_DIR.exists()) {
            LOGGER.info("First time launch.");
            Constants.GAME_DIR.mkdir();
        }
        LOGGER.info("Loading settings...");
        Runnable loadSettings = () -> {
            try {
                LauncherSettings.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        executorService.submit(loadSettings);
        LOGGER.info("Loaded settings.");
        URL fxml = getClass().getResource("Launcher_Main_GUI.fxml");
        assert fxml != null;
        Parent root = FXMLLoader.load(fxml);
        win.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root,450,450);
        win.setScene(scene);
        win.setResizable(false);
        win.setTitle("SkyLauncher");
        win.show();
        mainStage = win;
        initSettings(win,scene);
        ThemeManager.setThemeS(scene);
        LOGGER.info("Checking for updates!");
        //Runnable updater = () -> {
            try {
              new Updater().checkForUpdates();
            } catch (IOException e) {
                LOGGER.severe("Error updating!\n" + e);
            }
        //};
        //executorService.submit(updater);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down executor service.");
            try {
                LauncherSettings.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            executorService.shutdown();
        }));
    }

    private void initSettings(final Stage stage, Scene scene) {
        stage.getIcons().add(new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream("css/images/icon.png"))));
        scene.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }
}

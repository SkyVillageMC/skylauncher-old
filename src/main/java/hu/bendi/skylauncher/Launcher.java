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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Launcher extends Application {

    public static Stage mainStage;
    public static Stage optionsStage;
    public static ExecutorService executorService;
    public static Gson GSON;
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage win) throws Exception {
        GSON = new GsonBuilder().create();
        System.out.println("[Launcher] Starting launcher.");
        System.out.println("[Launcher] Starting executor.");
        executorService =
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>());
        if (!Constants.GAME_DIR.exists()) {
            System.out.println("[Launcher] First time launch.");
            Constants.GAME_DIR.mkdir();
        }
        System.out.println("[Launcher] Loading settings.");
        //Runnable loadSettings = () -> {
        //    try {
                LauncherSettings.load();
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    }
        //};
        //executorService.submit(loadSettings);
        System.out.println("[Launcher] Loaded settings.");
        URL fxml = getClass().getResource("Launcher_Main_GUI.fxml");
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
        System.out.println("[Launcher] Checking for updates...");
        Runnable updater = () -> {
            try {
              new Updater().checkForUpdates();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        executorService.submit(updater);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Launcher] Shutting down executor.");
            try {
                LauncherSettings.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            executorService.shutdown();
        }));
    }

    private void initSettings(final Stage stage, Scene scene) {
        stage.getIcons().add(new Image(Launcher.class.getResourceAsStream("css/images/app_icon_1.png")));
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

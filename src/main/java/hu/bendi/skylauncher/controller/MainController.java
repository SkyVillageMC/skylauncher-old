package hu.bendi.skylauncher.controller;

import hu.bendi.skylauncher.Launcher;
import hu.bendi.skylauncher.launcher.MinecraftLauncher;
import hu.bendi.skylauncher.settings.LauncherSettings;
import hu.bendi.skylauncher.themes.ThemeManager;
import hu.bendi.skylauncher.updater.Updater;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public ProgressBar progress;
    @FXML
    public Label status;
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private ImageView playerAvatarImage;
    @FXML
    private Tooltip tt_username;
    @FXML
    private AnchorPane mainBackground;
    @FXML
    private Tooltip tt_password;
    @FXML
    private Tooltip tt_play;
    @FXML
    private Tooltip tt_options;
    @FXML
    private TextField username;
    @FXML
    private Button launch;
    @FXML
    private PasswordField password;
    @FXML
    private Button minimize;
    @FXML
    private Button exit;
    @FXML
    private Button options;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTextBoxMax();
        setupBg();
        Timeline t = new Timeline(new KeyFrame(Duration.millis(100),(e) -> {
            progress.setProgress(Updater.progress);
            status.setText("Státusz: "+Updater.currentAction);
            launch.setDisable(!Updater.canPlay);
        }));
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
        username.setText(LauncherSettings.username);
    }

    @FXML
    private void launchExit(MouseEvent event) {
        System.out.println("[Launcher] Shutting down!");
        Launcher.mainStage.close();
        System.exit(0);
    }

    @FXML
    private void kt_username(KeyEvent event) {
        if (!event.getCharacter().matches("[A-Za-z0-9\b_]")) {
            Toolkit.getDefaultToolkit().beep();
            event.consume();
        }
        LauncherSettings.username = username.getText();
    }

    private void setTextBoxMax() {
        username.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > oldValue.intValue()) {
                if (username.getText().length() > 16) {
                    username.setText(username.getText().substring(0, 16));
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
    }

    private void setupBg() {
        BackgroundImage bg = new BackgroundImage(new Image(Launcher.class.getResource("css/images/background_1.jpg").toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        Background bgg = new Background(bg);

        mainBackground.setBackground(bgg);

        Random r = new Random(12);

        Timeline rotateBackground = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            int i = r.nextInt(9)+1;

            BackgroundImage bg1 = new BackgroundImage(new Image(
                    Launcher.class.getResource(
                            "css/images/background_"+i+".jpg").toExternalForm()),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);

            Background bgg1 = new Background(bg1);

            mainBackground.setBackground(bgg1);
        }));

        rotateBackground.setCycleCount(Timeline.INDEFINITE);
        rotateBackground.play();
    }

    @FXML
    private void showOptions(MouseEvent event) {
        if (Launcher.optionsStage == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("Launcher_Options_GUI.fxml"));
            Parent optionsGUI = null;
            try {
                optionsGUI = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.getIcons().add(new Image(Launcher.class.getResourceAsStream("css/images/icon.png")));
            stage.setTitle("SkyVillage Launcher - Beállítások");
            Scene sceneOptions = new Scene(optionsGUI);
            stage.setMinWidth(450);
            stage.setMinHeight(330);
            stage.setMaxWidth(450);
            stage.setMaxHeight(330);
            stage.setResizable(false);

            stage.setScene(sceneOptions);
            Launcher.optionsStage = stage;

            ThemeManager.setThemeS(sceneOptions);

            sceneOptions.setOnMousePressed(event13 -> {
                xOffset = stage.getX() - event13.getScreenX();
                yOffset = stage.getY() - event13.getScreenY();
            });

            sceneOptions.setOnMouseDragged(event12 -> {
                stage.setX(event12.getScreenX() + xOffset);
                stage.setY(event12.getScreenY() + yOffset);
            });
            stage.show();
        } else {
            Launcher.optionsStage.show();
        }
    }

    @FXML
    private void minimise(MouseEvent event) {
        Launcher.mainStage.setIconified(true);
    }

    @FXML
    private void play(MouseEvent event) {
        //Play
        Runnable r = () -> {
            try {
                new MinecraftLauncher().play(username.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Launcher.executorService.submit(r);
    }


    public void content(MouseEvent mouseEvent) {

    }
}

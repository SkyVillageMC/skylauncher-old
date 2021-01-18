package hu.bendi.skylauncher.controller;

import hu.bendi.skylauncher.Launcher;
import hu.bendi.skylauncher.settings.LauncherSettings;
import hu.bendi.skylauncher.themes.Theme;
import hu.bendi.skylauncher.themes.ThemeManager;
import hu.bendi.skylauncher.utils.Constants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsController implements Initializable {

    public static boolean useCT;
    @FXML
    private CheckBox optionsKeepLauncherOpen;
    @FXML
    private CheckBox optionsResolution;
    @FXML
    private CheckBox optionsRamAllocation;
    @FXML
    private TextField optionsResolutionWidth;
    @FXML
    private Slider optionsRamAllocationSlider;
    @FXML
    private TextField optionsResolutionHeight;
    @FXML
    private RadioButton optionsJavaVersion;
    @FXML
    private RadioButton optionsJVMArguments;
    @FXML
    private TextField optionsJavaVersionInput;
    @FXML
    private TextField optionsJVMArgumentsInput;
    @FXML
    private Label optionStatus;
    @FXML
    private RadioButton optionsDebugMode;
    @FXML
    private ComboBox<String> themeType;
    @FXML
    private CheckBox useThemeType;
    @FXML
    private Tooltip tt_keepLauncherOpen;
    @FXML
    private Tooltip tt_customTheme;
    @FXML
    private Tooltip tt_resolution;
    @FXML
    private Tooltip tt_ramAllocation;
    @FXML
    private Tooltip tt_javaVersion;
    @FXML
    private Tooltip tt_jvmArgs;
    @FXML
    private Label launcherVersion;
    @FXML
    private Tooltip tt_launcherVersion;
    @FXML
    private Tooltip tt_debugMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //LauncherSettings.useCustomTheme = useCT;
        launcherVersion.setText(Constants.VERSION);
        setupTooltips();
        themeType.getItems().addAll(Theme.BLUE.name, Theme.GRAY.name, Theme.GREEN.name, Theme.PURPLE.name, Theme.RED.name, Theme.WHITE.name);
        if (LauncherSettings.useCustomTheme) {
            useThemeType.setSelected(true);
            themeType.setDisable(false);
        }
        themeType.getSelectionModel().select(LauncherSettings.selectedTheme.name);

        optionsRamAllocationSlider.valueProperty().set(LauncherSettings.customRam);

        optionsRamAllocation.textProperty().setValue("Max Ram: " + LauncherSettings.customRam + "Mb");

        optionsRamAllocation.setSelected(LauncherSettings.useCustomRam);
        optionsRamAllocationSlider.setDisable(!LauncherSettings.useCustomRam);

        optionsResolution.setSelected(LauncherSettings.useCustomResolution);
        if (LauncherSettings.useCustomResolution) {
            optionsResolutionHeight.setDisable(false);
            optionsResolutionWidth.setDisable(false);
        }

        optionsResolutionWidth.setText(String.valueOf(LauncherSettings.width));
        optionsResolutionWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(oldValue)) return;
            try {
                LauncherSettings.width = Integer.parseInt(newValue);
            }catch (NumberFormatException e) {
                try {
                    LauncherSettings.width = Integer.parseInt(oldValue);
                    optionsResolutionWidth.setText(oldValue);
                } catch (NumberFormatException ignored) {
                    LauncherSettings.width = 845;
                    optionsResolutionWidth.setText("845");
                }
            }
        });

        optionsResolutionHeight.setText(String.valueOf(LauncherSettings.height));
        optionsResolutionHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(oldValue)) return;
            try {
                LauncherSettings.width = Integer.parseInt(newValue);
            }catch (NumberFormatException e) {
                try {
                    LauncherSettings.height = Integer.parseInt(oldValue);
                    optionsResolutionHeight.setText(oldValue);
                } catch (NumberFormatException ignored) {
                    LauncherSettings.height = 480;
                    optionsResolutionHeight.setText("480");
                }
            }
        });

        optionsResolution.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == oldValue) return;
            LauncherSettings.useCustomResolution = newValue;
            if (newValue) {
                optionsResolutionHeight.setDisable(false);
                optionsResolutionWidth.setDisable(false);
            }else {
                optionsResolutionHeight.setDisable(true);
                optionsResolutionWidth.setDisable(true);
            }
        });

        optionsRamAllocation.selectedProperty().addListener((observable, oldValue, newValue) -> {
            LauncherSettings.useCustomRam = newValue;
            if (LauncherSettings.useCustomRam) {
                optionsRamAllocationSlider.setDisable(false);
            }
        });

        optionsRamAllocationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            LauncherSettings.customRam = newValue.intValue();
            optionsRamAllocation.textProperty().setValue("Max Ram: " + newValue.intValue() + "Mb");
        });

        Platform.runLater(() -> {
            useThemeType.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == oldValue) return;
                if (newValue) {
                    themeType.setDisable(false);
                    LauncherSettings.useCustomTheme = true;
                }else {
                    themeType.setDisable(true);
                    LauncherSettings.useCustomTheme = false;
                }
            });
        });
    }

    private void setupTooltips() {
        tt_keepLauncherOpen.setText("Pipáld be ezt, hogyha szeretnéd hogy a launcher megnyitva maradjon amíg játszol.");
        tt_customTheme.setText("Itt választhatsz témát.");
        tt_debugMode.setText("Ahoz hogy ezt aktiváld, tagja kell lenned a csapatunknak.");
        tt_javaVersion.setText("A számítógépeden jelenleg telepített Java verziója.");
        tt_launcherVersion.setText("A launcher telepített verziója.");
        tt_jvmArgs.setText("Ezt csak akkor álítsd át, ha értessz hozzá.");
        tt_ramAllocation.setText("A játék által lefoglalható RAM mennyisége.");
        tt_resolution.setText("A játék alapfelbontása.");
    }

    @FXML
    private void optionsExit(ActionEvent event) {
        try {
            LauncherSettings.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Launcher.optionsStage.close();
    }

    @FXML
    private void optionsClose(ActionEvent event) {
        try {
            LauncherSettings.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Launcher.optionsStage.close();
    }

    @FXML
    private void optionsDebugMode(ActionEvent event) {

    }

    @FXML
    private void mp_optionsJavaVersionInput(MouseEvent event) {

    }

    @FXML
    private void optionsJavaVersion(ActionEvent event) {

    }

    @FXML
    private void optionsJVMArguments(ActionEvent event) {

    }

    @FXML
    private void optionsKeepLauncherOpen(ActionEvent event) {

    }

    @FXML
    private void themeType(ActionEvent event) {
        String theme = themeType.getSelectionModel().getSelectedItem();
        ThemeManager.getInstance().setCurrentTheme(ThemeManager.getByName(theme).id);
        LauncherSettings.selectedTheme = ThemeManager.getByName(theme);
    }

    @FXML
    private void mc_launcherVersion(ActionEvent event) {

    }

    @FXML
    private void optionsRamAllocation(ActionEvent event) {

    }

    @FXML
    private void kt_optionsResolutionMin(KeyEvent event) {

    }

    @FXML
    private void kt_optionsRamAllocationMin(KeyEvent event) {

    }

    @FXML
    private void kt_optionsResolutionMax(KeyEvent event) {

    }

    @FXML
    private void kt_optionsRamAllocationMax(KeyEvent event) {

    }

    @FXML
    private void optionsResolution(ActionEvent event) {

    }
}

package hu.bendi.skylauncher.controller;

import hu.bendi.skylauncher.Launcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomizeController implements Initializable {
    @FXML
    public ListView<Pane> content_list;

    public void optionsExit(ActionEvent actionEvent) {
        Launcher.contentStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Pane> pane = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            //Icon
            ImageView iconImg = new ImageView(new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream("css/images/icon.png"))));
            iconImg.setFitWidth(100);
            iconImg.setFitHeight(100);
            Pane iconContainer = new Pane(iconImg);
            iconContainer.setPrefSize(105, 100);

            //Text
            Label lName = new Label("Item#" + i);
            lName.setMinSize(50, 20);
            lName.setFont(Font.font(16));
            Label lDesc = new Label("A really long description.");
            VBox tBox = new VBox(lName, lDesc);
            tBox.setMinSize(275, 150);
            tBox.setMaxSize(275, 150);

            //Checkbox
            Pane spacer = new Pane();
            spacer.setPrefHeight(35);
            CheckBox selected = new CheckBox();
            selected.setScaleX(2);
            selected.setScaleY(2);
            VBox checked = new VBox(spacer, selected);
            checked.setPrefSize(60, 150);

            HBox all = new HBox(iconContainer, tBox, checked);
            Pane item = new Pane(all);
            item.setPrefSize(390, 100);
            pane.add(item);
        }
        content_list.getItems().setAll(pane);
    }
}

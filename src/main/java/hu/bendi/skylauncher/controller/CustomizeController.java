package hu.bendi.skylauncher.controller;

import com.sun.javafx.collections.ObservableListWrapper;
import hu.bendi.skylauncher.Launcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CustomizeController implements Initializable {
    @FXML
    public ListView<Pane> content_list;
    public ImageView ico;

    public void optionsExit(ActionEvent actionEvent) {
        Launcher.contentStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //List<Pane> pane = new ArrayList<>();
        //for (int i = 0; i < 90; i++) {
        //    Label l = new Label("Item#" + i);
        //    Pane p = new Pane(l);
        //    pane.add(p);
        //}
        //content_list.getItems().setAll(pane);
        ico.setImage(new Image(Launcher.class.getResourceAsStream("css/images/icon.png")));
        ico.setFitHeight(100);
        ico.setFitWidth(100);
    }
}

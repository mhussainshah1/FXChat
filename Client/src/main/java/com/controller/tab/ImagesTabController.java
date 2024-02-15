package com.controller.tab;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import org.springframework.stereotype.Component;

@Component
public class ImagesTabController {
    @FXML
    private Tab imagestab;
    @FXML
    private TilePane tilePane;

    @FXML
    private void iconHandler(MouseEvent mouseEvent) {
    }
}

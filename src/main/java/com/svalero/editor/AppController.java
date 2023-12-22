package com.svalero.editor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    @FXML
    private Button btnApply;
    @FXML
    private TextField tfDestinyPath;
    @FXML
    private Button btnHistory;
    @FXML
    private Label lblSavePath;
    @FXML
    private TabPane tpEdits;
    @FXML
    private Button btnEdit;
    @FXML
    private ChoiceBox cb1;
    @FXML
    private ChoiceBox cb2;
    @FXML
    private ChoiceBox cb3;
    @FXML
    private ChoiceBox cb4;
    @FXML
    private TextField tfOriginPath;
    @FXML
    private Button btnBrowseOrigin;
    @FXML
    private Button btnBrowseDestiny;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void launchEdit(ActionEvent event){

    }


}

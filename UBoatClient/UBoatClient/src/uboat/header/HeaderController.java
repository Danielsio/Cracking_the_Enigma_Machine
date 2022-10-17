package uboat.header;

import uboat.app.MainController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HeaderController {

    private FileChooser fileChooser = new FileChooser();

    private String selectedMachineFile;

    private BooleanProperty isMachineLoadedProperty;

    private MainController mainController;

    @FXML
    private Label filePathLoadMachineLabel;

    @FXML
    private Button loadFileButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        filePathLoadMachineLabel.setText("");
    }

    @FXML
    private void loadMachineFile(MouseEvent event) {
        fileChooser.setTitle("Load Machine");
        fileChooser.getExtensionFilters().add((new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml")));
        File chosenFile = fileChooser.showOpenDialog(new Stage());

        if (chosenFile != null) {
            this.selectedMachineFile = chosenFile.getAbsolutePath();
            mainController.loadMachineFromFile(selectedMachineFile);
        }
    }

    @FXML
    void logoutAction(MouseEvent event) {
        mainController.logoutUBoat(event);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void displayFilePath() {
        filePathLoadMachineLabel.setText(selectedMachineFile);
    }

    public void setProperties(BooleanProperty isMachineLoadedProperty) {
        this.isMachineLoadedProperty = isMachineLoadedProperty;
    }

    public void bindComponents(BooleanProperty isMachineLoadedProperty, StringProperty usernameProperty, BooleanProperty isContestActive) {
        loadFileButton.disableProperty().bind(isMachineLoadedProperty);
        usernameLabel.textProperty().bind(usernameProperty);
        logoutButton.disableProperty().bind(isContestActive);
    }

}
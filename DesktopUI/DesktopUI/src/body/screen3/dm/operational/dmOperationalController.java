package body.screen3.dm.operational;

import body.BodyController;
import dm.difficultylevel.DifficultyLevel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class dmOperationalController {

    private BodyController parentController;

    @FXML
    private Slider numOfAgentsSlider;

    @FXML
    private ComboBox<DifficultyLevel> difficultyLevelComboBox;

    @FXML
    private Spinner<Integer> taskSizeSpinner;

    @FXML
    private Button startButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Label numOfAgentLabel;

    private IntegerProperty totalPossibleWindowsPositions;
    private StringProperty textToDecipherProperty;


    @FXML
    public void initialize() {
        difficultyLevelComboBox.getItems().add(DifficultyLevel.EASY);
        difficultyLevelComboBox.getItems().add(DifficultyLevel.MEDIUM);
        difficultyLevelComboBox.getItems().add(DifficultyLevel.HARD);
        difficultyLevelComboBox.getItems().add(DifficultyLevel.IMPOSSIBLE);
        difficultyLevelComboBox.setPromptText("Please Select");

        this.textToDecipherProperty = new SimpleStringProperty();

        numOfAgentLabel.textProperty().bind(Bindings.concat("Number Of Agents: ", Bindings.format("%.0f", numOfAgentsSlider.valueProperty())));
    }

    @FXML
    void pauseBruteForceAction(MouseEvent event) {

    }

    @FXML
    void startBruteForceAction(MouseEvent event) {
        DifficultyLevel difficultyLevel = difficultyLevelComboBox.getValue();
        int taskSize = taskSizeSpinner.getValue(); // need to fix text value not updating if no button pressed
        int numOfAgentSelected = (int) numOfAgentsSlider.getValue();
        String textToDecipher = textToDecipherProperty.getValue();

        parentController.startBruteForce(textToDecipher, difficultyLevel, taskSize, numOfAgentSelected);
    }

    public void setSettings(int maxTaskSize, int maxNumOfAgents) {
        taskSizeSpinner.valueFactoryProperty().set(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxTaskSize));
        numOfAgentsSlider.setMax(maxNumOfAgents);
    }

    public void bindTextToDecipherPropertyToOutputCipher(StringProperty cipheredText) {
        textToDecipherProperty.bind(cipheredText);
    }


    public void setParentController(BodyController parentController) {
        this.parentController = parentController;
    }
}
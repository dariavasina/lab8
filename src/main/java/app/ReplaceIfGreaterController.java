package app;

import app.App;
import app.common.collectionClasses.*;
import app.common.commands.CommandWithResponse;
import app.common.commands.commandObjects.ReplaceIfGreaterCommand;
import app.exceptions.InvalidArgumentsException;
import app.networkStructures.CommandRequest;
import app.networkStructures.CommandResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.*;

public class ReplaceIfGreaterController implements Initializable {
    @FXML
    private ComboBox<String> comboboxFormOfEducation;

    @FXML
    private ComboBox<String> comboboxPersonColor;

    @FXML
    private ComboBox<String> comboboxPersonNationality;

    @FXML
    private ComboBox<String> comboboxSemester;

    @FXML
    private Label labelFormOfEducation;

    @FXML
    private Label labelError;

    @FXML
    private Label labelKey;

    @FXML
    private Label labelPersonColor;

    @FXML
    private Label labelPersonLocation;

    @FXML
    private Label labelPersonLocationX;

    @FXML
    private Label labelPersonLocationY;

    @FXML
    private Label labelPersonLocationZ;

    @FXML
    private Label labelPersonName;

    @FXML
    private Label labelPersonNationality;

    @FXML
    private Label labelPersonPassportID;

    @FXML
    private Label labelSemester;

    @FXML
    private Label labelShouldBeExpelled;

    @FXML
    private Label labelStudentsCount;

    @FXML
    private Label labelStudyGroupCoordinates;

    @FXML
    private Label labelStudyGroupName;

    @FXML
    private Label labelStudyGroupX;

    @FXML
    private Label labelStudyGroupY;

    @FXML
    private TextField textfieldKey;

    @FXML
    private TextField textfieldPersonLocationX;

    @FXML
    private TextField textfieldPersonLocationY;

    @FXML
    private TextField textfieldPersonLocationZ;

    @FXML
    private TextField textfieldPersonName;

    @FXML
    private TextField textfieldPersonPassportID;

    @FXML
    private TextField textfieldShouldBeExpelled;

    @FXML
    private TextField textfieldStudentsCount;

    @FXML
    private TextField textfieldStudyGroupName;

    @FXML
    private TextField textfieldStudyGroupX;

    @FXML
    private TextField textfieldStudyGroupY;

    @FXML
    private Button buttonSend;

    @FXML
    private ResourceBundle resourceBundle;

    private StudyGroup studyGroup;
    private CommandResponse response;
    private String output;

    private ArrayList<TextField> textFieldArrayList = new ArrayList<>(Arrays.asList(
            textfieldKey,
            textfieldStudyGroupName,
            textfieldStudyGroupX,
            textfieldStudyGroupY,
            textfieldStudentsCount,
            textfieldShouldBeExpelled,
            textfieldPersonName,
            textfieldPersonLocationX,
            textfieldPersonLocationY,
            textfieldPersonLocationZ,
            textfieldPersonPassportID

    ));

    private ArrayList<ComboBox<String>> comboboxArrayList = new ArrayList<>(Arrays.asList(
            comboboxFormOfEducation,
            comboboxSemester,
            comboboxPersonColor,
            comboboxPersonNationality
    ));


    private List<String> listFormOfEducation = Arrays.asList("full_time_education",
            "distance_education", "evening_classes");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> listSemester = FXCollections.observableArrayList(
                "3", "4", "8"
        );
        ObservableList<String> listFormOfEducation = FXCollections.observableArrayList(
                "full_time_education", "distance_education", "evening_classes"
        );
        ObservableList<String> listNationality = FXCollections.observableArrayList(
                "Russia", "France", "India", "Vatican", "North Korea"
        );
        ObservableList<String> listColor = FXCollections.observableArrayList(
                "red", "yellow", "orange"
        );

        comboboxSemester.setItems(listSemester);
        comboboxPersonColor.setItems(listColor);
        comboboxFormOfEducation.setItems(listFormOfEducation);
        comboboxPersonNationality.setItems(listNationality);

        setInterfaceTexts();


    }

    public void setInterfaceTexts() {
        labelFormOfEducation.setText(resourceBundle.getString("form_of_education"));
        labelKey.setText(resourceBundle.getString("key"));
        labelPersonColor.setText(resourceBundle.getString("color"));
        labelPersonLocation.setText(resourceBundle.getString("location"));
        labelPersonLocationX.setText(resourceBundle.getString("x"));
        labelPersonLocationY.setText(resourceBundle.getString("y"));
        labelPersonLocationZ.setText(resourceBundle.getString("z"));
        labelPersonName.setText(resourceBundle.getString("name"));
        labelPersonNationality.setText(resourceBundle.getString("country"));
        labelPersonPassportID.setText(resourceBundle.getString("passport"));
        labelSemester.setText(resourceBundle.getString("semester"));
        labelShouldBeExpelled.setText(resourceBundle.getString("should_be_expelled"));
        labelStudentsCount.setText(resourceBundle.getString("students_count"));
        labelStudyGroupCoordinates.setText(resourceBundle.getString("coordinates"));
        labelStudyGroupName.setText(resourceBundle.getString("name"));
        labelStudyGroupX.setText(resourceBundle.getString("x"));
        labelStudyGroupY.setText(resourceBundle.getString("y"));
    }

    @FXML
    public void getCollectionInput(ActionEvent event) throws Exception {
        String studyGroupKey = textfieldKey.getText();

        String textStudyGroupX = textfieldStudyGroupX.getText();
        String textStudyGroupY = textfieldStudyGroupY.getText();
        String textStudyGroupStudentsCount = textfieldStudentsCount.getText();
        String studyGroupName = textfieldStudyGroupName.getText();
        String textShouldBeExpelled = textfieldShouldBeExpelled.getText();

        String personName = textfieldPersonName.getText();
        String textPersonLocationX = textfieldPersonLocationX.getText();
        String textPersonLocationY = textfieldPersonLocationY.getText();
        String textPersonLocationZ = textfieldPersonLocationZ.getText();
        String personPassportID = textfieldPersonPassportID.getText();

        String textFormOfEducation = "";
        String textSemester = "";
        String textColor = "";
        String textCountry = "";
        if (!comboboxFormOfEducation.getSelectionModel().isEmpty()) {
            textFormOfEducation = (String) comboboxFormOfEducation.getValue();
        }

        if (!comboboxSemester.getSelectionModel().isEmpty()) {
            textSemester = (String) comboboxSemester.getValue();
        }

        if (!comboboxPersonColor.getSelectionModel().isEmpty()) {
            textColor = (String) comboboxPersonColor.getValue();
        }

        if (!comboboxPersonNationality.getSelectionModel().isEmpty()) {
            textCountry = (String) comboboxPersonNationality.getValue().toLowerCase();
        }



        try {
            studyGroup = CollectionInputControlller.checkCollectionInput(
                    studyGroupKey,
                    studyGroupName,
                    textStudyGroupX,
                    textStudyGroupY,
                    textStudyGroupStudentsCount,
                    textShouldBeExpelled,
                    textFormOfEducation,
                    textSemester,
                    personName,
                    textColor,
                    textPersonLocationX,
                    textPersonLocationY,
                    textPersonLocationZ,
                    personPassportID,
                    textCountry
            );


            CommandWithResponse command = new ReplaceIfGreaterCommand();
            command.setStudyGroup(studyGroup);
            command.setArgs(new String[]{studyGroupKey});
            CommandRequest request = new CommandRequest(command);
            response = App.networkManager.sendRequest(request);
            output = response.getOutput();
            showInformationWindow("replace_if_greater");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
            labelError.setText(e.getMessage());
        }

    }

    private void showInformationWindow(String commandName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //alert.setTitle();

        alert.setHeaderText(null);
        //alert.setContentText("Команда " + commandName + " завершена успешно");
        alert.setContentText(output);
        alert.showAndWait();
//        alert.setOnHidden(e -> {
//            for (TextField t : textFieldArrayList) {
//                t.clear();
//            }
//
//            for (ComboBox<String> c : comboboxArrayList) {
//                c.getSelectionModel().clearSelection();
//            }
//        });
    }

}


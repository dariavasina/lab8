package app;

import app.common.collectionClasses.Country;
import app.common.collectionClasses.FormOfEducation;
import app.common.collectionClasses.Semester;
import app.common.collectionClasses.StudyGroup;
import app.common.commands.CommandWithResponse;
import app.common.commands.commandObjects.InsertCommand;
import app.common.commands.commandObjects.ShowCommand;
import app.common.commands.commandObjects.UpdateCommand;
import app.exceptions.InvalidArgumentsException;
import app.exceptions.InvalidInputException;
import app.networkStructures.CommandRequest;
import app.networkStructures.CommandResponse;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.*;

public class ShowController implements Initializable {
    @FXML
    private TableColumn<StudyGroup, FormOfEducation> form_of_education;

    @FXML
    private TableColumn<StudyGroup, Long> id;

    @FXML
    private TableColumn<StudyGroup, String> name;

    @FXML
    private TableColumn<StudyGroup, Semester> semester;

    @FXML
    private TableColumn<StudyGroup, Integer> should_be_expelled;

    @FXML
    private TableColumn<StudyGroup, Integer> students_count;

    @FXML
    private TableColumn<StudyGroup, Double> x;

    @FXML
    private TableColumn<StudyGroup, Integer> y;

    @FXML
    private TableView<StudyGroup> table;

    @FXML
    private TableColumn<StudyGroup, String> name_admin;

    @FXML
    private TableColumn<StudyGroup, String> passport;

    @FXML
    private TableColumn<StudyGroup, String> country;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setCellValueFactory(new PropertyValueFactory<StudyGroup, Long>("id"));
        name.setCellValueFactory(new PropertyValueFactory<StudyGroup, String>("name"));
//        x.setCellValueFactory(new PropertyValueFactory<StudyGroup, Double>("coordinates.x"));
//        y.setCellValueFactory(new PropertyValueFactory<StudyGroup, Integer>("coordinates.y"));
        students_count.setCellValueFactory(new PropertyValueFactory<StudyGroup, Integer>("studentsCount"));
        should_be_expelled.setCellValueFactory(new PropertyValueFactory<StudyGroup, Integer>("shouldBeExpelled"));
        form_of_education.setCellValueFactory(new PropertyValueFactory<StudyGroup, FormOfEducation>("formOfEducation"));
        semester.setCellValueFactory(new PropertyValueFactory<StudyGroup, Semester>("semester"));

        x.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            double xValue = studyGroup.getCoordinates().getX();
            return new SimpleDoubleProperty(xValue).asObject();
        });

        y.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            int yValue = studyGroup.getCoordinates().getY();
            return new SimpleIntegerProperty(yValue).asObject();
        });

        name.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            String name = studyGroup.getName();
            return new SimpleStringProperty(name);
        });

        name_admin.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            if (studyGroup.getGroupAdmin() != null) {
                String nameValue = studyGroup.getGroupAdmin().getName();
                return new SimpleStringProperty(nameValue);
            }
            return null;
        });

        passport.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            if (studyGroup.getGroupAdmin() != null) {
                String passportValue = studyGroup.getGroupAdmin().getPassportID();
                return new SimpleStringProperty(passportValue);
            }
            return null;
        });

//        country.setCellValueFactory(cellData -> {
//            StudyGroup studyGroup = cellData.getValue();
//            String countryValue = studyGroup.getGroupAdmin().getNationality().toString();
//            return new SimpleStringProperty(countryValue);
//        });

        table.setEditable(true);
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setEditable(true);
        name.setOnEditCommit(event -> {
            StudyGroup studyGroup = event.getTableView().getItems().get(event.getTablePosition().getRow());
            String oldValue = event.getOldValue();
            String newValue = event.getNewValue();
            try {
                studyGroup.setName(newValue);
            } catch (InvalidInputException e) {
                throw new RuntimeException(e);
            }

            UpdateCommand command = new UpdateCommand();



            try {
                command.setArgs(new String[]{String.valueOf(studyGroup.getId())});
            } catch (InvalidArgumentsException e) {
                throw new RuntimeException(e);
            }
            command.setStudyGroup(studyGroup);

            System.out.println(studyGroup);
            CommandRequest request = new CommandRequest(command);
            String response = App.networkManager.sendRequest(request).getOutput();
            System.out.println(response);

            table.refresh();
        });

        CommandWithResponse command =  new ShowCommand();
        CommandRequest request = new CommandRequest(command);
        CommandResponse response = App.networkManager.sendRequest(request);
        Map<Long, StudyGroup> collectionMap = response.getCollectionMap();


        List<StudyGroup> collection = new ArrayList<>(collectionMap.values());
        ObservableList<StudyGroup> observableCollection = FXCollections.observableList(collection);

        table.setItems(observableCollection);
    }



}

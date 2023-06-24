package app;

import app.common.collectionClasses.*;
import app.common.commands.CommandWithResponse;
import app.common.commands.commandObjects.*;
import app.exceptions.InvalidArgumentsException;
import app.exceptions.InvalidInputException;
import app.networkStructures.CommandRequest;
import app.networkStructures.CommandResponse;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private ListView<String> commandList;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextArea textArea;

    @FXML
    private TableColumn<StudyGroup, Long> idColumn;

    @FXML
    private TableColumn<StudyGroup, String> nameColumn;

    @FXML
    private TableColumn<StudyGroup, Semester> semesterColumn;

    @FXML
    private TableColumn<StudyGroup, Integer> shouldBeExpelledColumn;

    @FXML
    private TableColumn<StudyGroup, Integer> studentsCountColumn;

    @FXML
    private TableColumn<StudyGroup, Double> xColumn;

    @FXML
    private TableColumn<StudyGroup, Integer> yColumn;

    @FXML
    private TableView<StudyGroup> table;

    @FXML
    private TableColumn<StudyGroup, String> adminNameColumn;

    @FXML
    private TableColumn<StudyGroup, String> passportColumn;

    @FXML
    private TableColumn<StudyGroup, String> countryColumn;

    @FXML
    private TableColumn<StudyGroup, FormOfEducation> formOfEducationColumn;

    @FXML
    private TableColumn<Long, Long> keyColumn;

    @FXML
    private Label labelRemoveKey;

    @FXML
    private TextField textfieldRemoveKey;

    @FXML
    private Button buttonRemoveKey;

    @FXML
    private Button buttonClear;

    @FXML
    private AnchorPane visualizationAnchorPane;

    @FXML
    private MenuItem czechMenu;

    @FXML
    private MenuItem englishMenu;

    @FXML
    private Menu languageMenu;

    @FXML
    private MenuItem russianMenu;
    @FXML
    private MenuItem swedishMenu;


    private static final ArrayList<String> items = new ArrayList<>() {{
        add("help");
        add("info");
        add("show");
        add("insert");
        add("execute_script");
        add("replace_if_greater");
        add("replace_if_lower");
        add("remove_greater_key");
        add("count_by_students_count");
        add("visualize");
    }};

    private String chosenCommand;

    @FXML
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = ResourceBundle.getBundle("resources", new Locale("ru", "RU"));

        List<String> localizedCommands = new ArrayList<>();
        for (String command : items) {
            String localizedCommand = this.resourceBundle.getString(command);
            localizedCommands.add(localizedCommand);
        }

        commandList.getItems().addAll(localizedCommands);
        commandList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Update UI based on the selected item
            chosenCommand = commandList.getSelectionModel().getSelectedItem();
            try {
                changeView(chosenCommand);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        setInterfaceTexts();

        czechMenu.setOnAction(this::setLanguage);
        englishMenu.setOnAction(this::setLanguage);
        russianMenu.setOnAction(this::setLanguage);
        swedishMenu.setOnAction(this::setLanguage);


        idColumn.setCellValueFactory(new PropertyValueFactory<StudyGroup, Long>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<StudyGroup, String>("name"));
        studentsCountColumn.setCellValueFactory(new PropertyValueFactory<StudyGroup, Integer>("studentsCount"));
        shouldBeExpelledColumn.setCellValueFactory(new PropertyValueFactory<StudyGroup, Integer>("shouldBeExpelled"));
        formOfEducationColumn.setCellValueFactory(new PropertyValueFactory<StudyGroup, FormOfEducation>("formOfEducation"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<StudyGroup, Semester>("semester"));

        xColumn.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            double xValue = studyGroup.getCoordinates().getX();
            return new SimpleDoubleProperty(xValue).asObject();
        });

        yColumn.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            int yValue = studyGroup.getCoordinates().getY();
            return new SimpleIntegerProperty(yValue).asObject();
        });

        adminNameColumn.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            if (studyGroup.getGroupAdmin() != null) {
                String nameValue = studyGroup.getGroupAdmin().getName();
                return new SimpleStringProperty(nameValue);
            }
            return null;
        });

        passportColumn.setCellValueFactory(cellData -> {
            StudyGroup studyGroup = cellData.getValue();
            if (studyGroup.getGroupAdmin() != null) {
                String passportValue = studyGroup.getGroupAdmin().getPassportID();
                return new SimpleStringProperty(passportValue);
            }
            return null;
        });
        passportColumn.setEditable(true);
        passportColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passportColumn.setOnEditCommit(event -> {
            StudyGroup studyGroup = event.getTableView().getItems().get(event.getTablePosition().getRow());
            String newValue = event.getNewValue();
            try {
                Person admin = studyGroup.getGroupAdmin();
                admin.setPassportID(newValue);
                studyGroup.setGroupAdmin(admin);
            } catch (Exception e) {
                showErrorWindow(e.getMessage());
            }

            updateStudyGroup(studyGroup);

            table.refresh();
        });


        nameColumn.setEditable(true);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
                    StudyGroup studyGroup = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    String newValue = event.getNewValue();
                    try {
                        studyGroup.setName(newValue);
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }

                    updateStudyGroup(studyGroup);

                    table.refresh();
                });



//        country.setCellValueFactory(cellData -> {
//            StudyGroup studyGroup = cellData.getValue();
//            String countryValue = studyGroup.getGroupAdmin().getNationality().toString();
//            return new SimpleStringProperty(countryValue);
//        });

        table.setEditable(true);



        xColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return value != null ? value.toString() : "";
            }

            @Override
            public Double fromString(String text) {
                // Handle the conversion from String to Double
                try {
                    return Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    showErrorWindow("Некорректный формат ввода");
                    return null;
                }
            }
        }));

        xColumn.setEditable(true);
        xColumn.setOnEditCommit(event -> {
            StudyGroup studyGroup = event.getTableView().getItems().get(event.getTablePosition().getRow());
            Double oldValue = event.getOldValue();
            Double newValue = event.getNewValue();
            if (newValue != null) {
                Coordinates coordinates = studyGroup.getCoordinates();
                coordinates.setX(newValue);
                try {
                    studyGroup.setCoordinates(coordinates);
                } catch (InvalidInputException e) {
                    //
                }
            }
            updateStudyGroup(studyGroup);

            table.refresh();
        });

        yColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer value) {
                return value != null ? value.toString() : "";
            }

            @Override
            public Integer fromString(String text) {
                try {
                    return Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    showErrorWindow("Пожалуйста, введите число корректно");
                    return null;
                }
            }
        }));

        yColumn.setEditable(true);
        yColumn.setOnEditCommit(event -> {
            StudyGroup studyGroup = event.getTableView().getItems().get(event.getTablePosition().getRow());
            Integer newValue = event.getNewValue();
            if (newValue != null) {
                Coordinates coordinates = studyGroup.getCoordinates();
                coordinates.setY(newValue);
                try {
                    studyGroup.setCoordinates(coordinates);
                } catch (InvalidInputException e) {
                    //
                }
            }
            updateStudyGroup(studyGroup);


            table.refresh();
        });

        visualizationAnchorPane.setVisible(false);

        openShow();

        loadVisualization();
    }

    private Parent insertForm;
    private Parent removeIfGreaterForm;
    private Parent visualization;

    private void setInterfaceTexts() {

        // Label

        labelRemoveKey.setText(resourceBundle.getString("remove_by_key"));

        // TableView and TableColumn
        idColumn.setText(resourceBundle.getString("id"));
        nameColumn.setText(resourceBundle.getString("name"));
        semesterColumn.setText(resourceBundle.getString("semester"));
        shouldBeExpelledColumn.setText(resourceBundle.getString("should_be_expelled"));
        studentsCountColumn.setText(resourceBundle.getString("students_count"));
        xColumn.setText(resourceBundle.getString("x"));
        yColumn.setText(resourceBundle.getString("y"));
        adminNameColumn.setText(resourceBundle.getString("name_admin"));
        passportColumn.setText(resourceBundle.getString("passport"));
        countryColumn.setText(resourceBundle.getString("country"));
        formOfEducationColumn.setText(resourceBundle.getString("form_of_education"));
        keyColumn.setText(resourceBundle.getString("key"));
        commandList.getItems().setAll();


    }

    @FXML
    private void setLanguage(ActionEvent event) {
        MenuItem selectedLanguage = (MenuItem) event.getSource();
        String languageCode = selectedLanguage.getId();

        switch (languageCode) {
            case "czechMenu":
                resourceBundle = ResourceBundle.getBundle("resources", new Locale("cs", "CZ"));
                break;
            case "englishMenu":
                resourceBundle = ResourceBundle.getBundle("resources", new Locale("en", "US"));
                break;
            case "russianMenu":
                resourceBundle = ResourceBundle.getBundle("resources", new Locale("ru", "RU"));
                break;
            case "swedishMenu":
                resourceBundle = ResourceBundle.getBundle("resources", new Locale("sv", "SE"));
                break;
        }

        setInterfaceTexts();
    }










private void updateStudyGroup(StudyGroup studyGroup) {
        UpdateCommand command = new UpdateCommand();

        try {
            command.setArgs(new String[]{String.valueOf(studyGroup.getId())});
        } catch (InvalidArgumentsException e) {
            throw new RuntimeException(e);
        }
        command.setStudyGroup(studyGroup);

        CommandRequest request = new CommandRequest(command);
        String response = App.networkManager.sendRequest(request).getOutput();
    }

    private void showErrorWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");

        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformationWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Результат");

        alert.setHeaderText(null);
        alert.setContentText("Команда выполнена успешно");
        alert.showAndWait();
    }

    private void openInsertForm() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("insert.fxml"));
            insertForm = loader.load();

            anchorPane.getChildren().setAll(insertForm);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openReplaceIfGreaterForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("replace_if_greater.fxml"));
            removeIfGreaterForm = loader.load();

            anchorPane.getChildren().setAll(removeIfGreaterForm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openShow() {
        table.setVisible(true);
        CommandWithResponse command =  new ShowCommand();
        CommandRequest request = new CommandRequest(command);
        CommandResponse response = App.networkManager.sendRequest(request);
        Map<Long, StudyGroup> collectionMap = response.getCollectionMap();


        List<StudyGroup> collection = new ArrayList<>(collectionMap.values());

        ObservableList<StudyGroup> observableCollection = FXCollections.observableList(collection);
        //ObservableList<Long> keys = FXCollections.observableArrayList(collectionMap.keySet());

        //table.setItems(keys);
        table.setItems(observableCollection);


    }

    public void hideAll() {
        textArea.setVisible(false);
        anchorPane.getChildren().removeAll(insertForm, removeIfGreaterForm);
        labelRemoveKey.setVisible(false);
        textfieldRemoveKey.setVisible(false);
        buttonRemoveKey.setVisible(false);
        buttonClear.setVisible(false);
        table.setVisible(false);
        visualizationAnchorPane.setVisible(false);
    }

    @FXML
    protected void removeByKey(ActionEvent event) throws IOException {
        String keyText = textfieldRemoveKey.getText();

        RemoveKeyCommand command = new RemoveKeyCommand();
        try {
            command.setArgs(new String[]{keyText});
        } catch (InvalidArgumentsException e) {
            showErrorWindow(e.getMessage());
        }

        CommandRequest request = new CommandRequest(command);
        String response = App.networkManager.sendRequest(request).getOutput().toString();
        table.refresh();
        showInformationWindow(response);



    }

    public void clear() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setContentText("Вы уверены, что хотите очистить коллекцию?");

        ButtonType yesButton = new ButtonType("Да");
        ButtonType noButton = new ButtonType("Нет");

        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the dialog and wait for a response
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        // Return true if "Yes" button is clicked, false otherwise
        if (result == yesButton) {
            ClearCommand command = new ClearCommand();
            CommandRequest request = new CommandRequest(command);
            String response = App.networkManager.sendRequest(request).getOutput();

            showInformationWindow(response);
        }

        table.refresh();
        loadVisualization();
    }


    public void changeView(String command) throws IOException {

        if (command.equals("info")) {
            hideAll();
            textArea.setVisible(true);
            CommandRequest request = new CommandRequest(new InfoCommand());
            String response = App.networkManager.sendRequest(request).getOutput();
            textArea.setText(response);
        } else if (command.equals("help")) {
            hideAll();
            textArea.setVisible(true);
            CommandRequest request = new CommandRequest(new HelpCommand());
            String response = App.networkManager.sendRequest(request).getOutput();
            textArea.setText(response);
        } else if (command.equals("insert")) {
            anchorPane.setVisible(true);
            hideAll();
            openInsertForm();
            loadVisualization();
        } else if (command.equals("replace_if_greater")) {
            hideAll();
            anchorPane.setVisible(true);
            openReplaceIfGreaterForm();
        } else if (command.equals("show")) {
            hideAll();
            openShow();
            labelRemoveKey.setVisible(true);
            textfieldRemoveKey.setVisible(true);
            buttonRemoveKey.setVisible(true);
            buttonClear.setVisible(true);

        } else if (command.equals("visualize")) {
            openShow();
            labelRemoveKey.setVisible(false);
            textfieldRemoveKey.setVisible(false);
            buttonRemoveKey.setVisible(false);
            buttonClear.setVisible(false);

            visualizationAnchorPane.setVisible(true);





//            Scene scene = new Scene(fxmlLoader.load());
//            Stage stage = new Stage();
//            stage.setX(100);
//            stage.setY(0);
//            stage.setResizable(false);
//            stage.setScene(scene);
//            stage.show();


        }

    }

    public void loadVisualization() {
        CommandWithResponse commandShow = new ShowCommand();
        CommandRequest request = new CommandRequest(commandShow);
        CommandResponse response = App.networkManager.sendRequest(request);
        Map<Long, StudyGroup> collectionMap = response.getCollectionMap();

        List<StudyGroup> collection = new ArrayList<>(collectionMap.values());


        for (StudyGroup group : collection) {
            Circle circle = new Circle();
            circle.setCenterX(group.getCoordinates().getX());
            circle.setCenterY(group.getCoordinates().getY());
            circle.setRadius(group.getStudentsCount()); // Радиус зависит от количества учеников
            circle.setFill(Color.BLUE); // Цвет окружности
            visualizationAnchorPane.getChildren().add(circle);

            Text text = new Text(group.getCoordinates().getX() - 15, group.getCoordinates().getY() - group.getStudentsCount() - 5, group.getName());
            visualizationAnchorPane.getChildren().add(text);
        }
    }

    public void addToVisualization(StudyGroup group) {
        Circle circle = new Circle();
        circle.setCenterX(group.getCoordinates().getX());
        circle.setCenterY(group.getCoordinates().getY());
        circle.setRadius(group.getStudentsCount()); // Радиус зависит от количества учеников
        circle.setFill(Color.BLUE); // Цвет окружности
        visualizationAnchorPane.getChildren().add(circle);

        Text text = new Text(group.getCoordinates().getX() - 15, group.getCoordinates().getY() - group.getStudentsCount() - 5, group.getName());
        visualizationAnchorPane.getChildren().add(text);
    }



}

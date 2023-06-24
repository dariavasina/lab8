package app;

import app.common.collectionClasses.StudyGroup;
import app.common.commands.CommandWithResponse;
import app.common.commands.commandObjects.ShowCommand;
import app.networkStructures.CommandRequest;
import app.networkStructures.CommandResponse;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class VisualizationController implements Initializable {

    @FXML
    private AnchorPane visualizationAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //
    }

    public void visualize() {
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
}
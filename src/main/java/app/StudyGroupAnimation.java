package app;

import app.common.collectionClasses.Coordinates;
import app.common.collectionClasses.StudyGroup;
import app.common.commands.CommandWithResponse;
import app.common.commands.commandObjects.ShowCommand;
import app.networkStructures.CommandRequest;
import app.networkStructures.CommandResponse;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class StudyGroupAnimation extends Application {

    private static final double STUDY_GROUP_WIDTH = 80;
    private static final double STUDY_GROUP_HEIGHT = 40;
    private static final Color STUDY_GROUP_COLOR = Color.BLUE;
    private static final Color SELECTED_STUDY_GROUP_COLOR = Color.RED;
    private static final double ANIMATION_DURATION_MS = 200;

    private List<StudyGroup> studyGroups = getStudyGroupCollection();
    private List<Rectangle> rectangles = new ArrayList<>();

    private Pane root;

    public StudyGroupAnimation() throws UnknownHostException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Study Group Visualization");
        primaryStage.show();

        createStudyGroupRectangles();
        animateStudyGroupsToPositions();

        // Update the study groups and redraw the rectangles every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateStudyGroups()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private List<StudyGroup> createSampleStudyGroups() {
        List<StudyGroup> studyGroups = new ArrayList<>();
        studyGroups.add(new StudyGroup("Group 1", new Coordinates(100.0, 100)));
        studyGroups.add(new StudyGroup("Group 2", new Coordinates(200.0, 200)));
        studyGroups.add(new StudyGroup("Group 3", new Coordinates(300.0, 300)));
        return studyGroups;
    }

    private List<StudyGroup> getStudyGroupCollection() throws UnknownHostException {

        CommandWithResponse command = new ShowCommand();
        CommandRequest request = new CommandRequest(command);
        CommandResponse response = new NetworkManager(8888, InetAddress.getByName("localhost")).sendRequest(request);
        //CommandResponse response = App.networkManager.sendRequest(request);
        Map<Long, StudyGroup> collectionMap = response.getCollectionMap();
        List<StudyGroup> collection = new ArrayList<>(collectionMap.values());

        return collection;

    }

    private void createStudyGroupRectangles() {
        for (StudyGroup studyGroup : studyGroups) {
            Rectangle rectangle = new Rectangle(STUDY_GROUP_WIDTH, STUDY_GROUP_HEIGHT, STUDY_GROUP_COLOR);
            rectangle.setUserData(studyGroup);
            rectangles.add(rectangle);
            root.getChildren().add(rectangle);

            rectangle.setOnMouseClicked(event -> showStudyGroupInfo(studyGroup));
        }
    }

    private void animateStudyGroupsToPositions() {
        for (int i = 0; i < studyGroups.size(); i++) {
            StudyGroup studyGroup = studyGroups.get(i);
            Rectangle rectangle = rectangles.get(i);

            double startX = studyGroup.getCoordinates().getX();
            double startY = 0; // Set initial startY value to the top of the screen

            rectangle.setLayoutX(startX);
            rectangle.setLayoutY(startY);

            double targetY = studyGroup.getCoordinates().getY(); // Get the target Y coordinate

            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(ANIMATION_DURATION_MS),
                            event -> {
                                double newY = rectangle.getLayoutY() + 2; // Move the rectangle by a small increment
                                rectangle.setLayoutY(newY);
                                if (newY >= targetY) {
                                    timeline.stop(); // Stop the animation when the rectangle reaches the target position
                                }
                            }
                    )
            );
            timeline.setCycleCount(Timeline.INDEFINITE);

            timeline.play();
        }
    }



    private void updateStudyGroups() {
        // Simulating changes in study groups (e.g., coordinates update)
        studyGroups.forEach(studyGroup -> {
            //while (studyGroup.getCoordinates().getY() <)
            studyGroup.getCoordinates().setY(studyGroup.getCoordinates().getY() + 10);
        });
    }
    private void showStudyGroupInfo(StudyGroup studyGroup) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Study Group Information");
        alert.setHeaderText(null);
        alert.setContentText(studyGroup.toString());

        alert.showAndWait();
    }

}

        // Redraw the rectangles with updated


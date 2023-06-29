package app;

import app.common.collectionClasses.StudyGroup;
import app.common.commands.CommandWithResponse;
import app.common.commands.commandObjects.ShowCommand;
import app.networkStructures.CommandRequest;
import app.networkStructures.CommandResponse;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class VisualizationController implements Initializable {

    private static final double STUDY_GROUP_WIDTH = 80;
    private static final double STUDY_GROUP_HEIGHT = 40;
    private static final Color STUDY_GROUP_COLOR = Color.BLUE;
    private static final Color SELECTED_STUDY_GROUP_COLOR = Color.RED;
    private static final double ANIMATION_DURATION_MS = 200;

    private List<StudyGroup> studyGroups = getStudyGroupCollection();
    private List<StudyGroup> updatedStudyGroups = getStudyGroupCollection();
    private List<Rectangle> rectangles = new ArrayList<>();

    @FXML
    private AnchorPane visualizationAnchorPane;

    private ResourceBundle resourceBundle;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createStudyGroupRectangles();
        animateStudyGroupsToPositions();

        Thread thread = new Thread(() -> {
            Duration durationShow = Duration.seconds(1);
            Timeline timelineShow = new Timeline(new KeyFrame(durationShow, event -> {
                    checkForUpdates();
            }));

            timelineShow.setCycleCount(Timeline.INDEFINITE);
            timelineShow.play();
        });

        thread.start();
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    private synchronized List<StudyGroup> getStudyGroupCollection() {

        CommandWithResponse command = new ShowCommand();
        CommandRequest request = new CommandRequest(command);
        CommandResponse response = App.networkManager.sendRequest(request);
        Map<Long, StudyGroup> collectionMap = response.getCollectionMap();
        List<StudyGroup> collection = new ArrayList<>(collectionMap.values());

        return collection;

    }

    private void createStudyGroupRectangles() {
        for (StudyGroup studyGroup : studyGroups) {
            Rectangle rectangle = new Rectangle(STUDY_GROUP_WIDTH, STUDY_GROUP_HEIGHT, STUDY_GROUP_COLOR);
            rectangle.setUserData(studyGroup);
            rectangles.add(rectangle);
            visualizationAnchorPane.getChildren().add(rectangle);

            rectangle.setOnMouseClicked(event -> showStudyGroupInfo(studyGroup));
        }
    }

    private void drawNewStudyGroup(StudyGroup studyGroup) {
        Rectangle rectangle = new Rectangle(STUDY_GROUP_WIDTH, STUDY_GROUP_HEIGHT, STUDY_GROUP_COLOR);
        rectangle.setUserData(studyGroup);
        rectangles.add(rectangle);
        visualizationAnchorPane.getChildren().add(rectangle);

        rectangle.setOnMouseClicked(event -> showStudyGroupInfo(studyGroup));

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


    private void redrawStudyGroup(StudyGroup updatedStudyGroup, int index) {
        studyGroups.set(index, updatedStudyGroup); // Update the study group in the studyGroups list

        Rectangle rectangle = rectangles.get(index);
        double targetY = updatedStudyGroup.getCoordinates().getY(); // Get the updated target Y coordinate

        // Check if the rectangle is already in the correct position
        if (rectangle.getLayoutY() == targetY) {
            return; // No need to redraw or animate
        }

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(ANIMATION_DURATION_MS),
                        event -> {
                            double currentY = rectangle.getLayoutY();
                            double increment = Math.signum(targetY - currentY) * 2; // Calculate the increment based on the direction
                            double newY = currentY + increment; // Move the rectangle by the increment

                            if (Math.abs(targetY - currentY) <= Math.abs(increment)) {
                                // Rectangle is close enough to the target position, set it exactly to the target
                                rectangle.setLayoutY(targetY);
                                timeline.stop(); // Stop the animation
                            } else {
                                rectangle.setLayoutY(newY);
                            }
                        }
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();
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

    private void checkForUpdates() {
        List<StudyGroup> newStudyGroups = getStudyGroupCollection();
        for (int i = 0; i < newStudyGroups.size(); i++) {
            StudyGroup studyGroup = newStudyGroups.get(i);
            boolean isNewStudyGroup = true;
            for (int j = 0; j < studyGroups.size(); j++) {
                if (Objects.equals(studyGroup.getId(), studyGroups.get(j).getId())) {
                    isNewStudyGroup = false;
                    if (!studyGroup.equals(studyGroups.get(j))) {
                        // Study group has been updated, redraw it if necessary
                        if (!updatedStudyGroups.contains(studyGroup)) {
                            updatedStudyGroups.add(studyGroup);
                            redrawStudyGroup(studyGroup, i);
                        }
                    }
                    break;
                }
            }
            if (isNewStudyGroup) {
                // New study group detected, draw it
                drawNewStudyGroup(studyGroup);
                studyGroups.add(studyGroup);
            }
        }
    }





    private void showStudyGroupInfo(StudyGroup studyGroup) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Study Group Information");
        alert.setHeaderText(null);
        alert.setContentText(studyGroup.toString());

        alert.showAndWait();
    }
}

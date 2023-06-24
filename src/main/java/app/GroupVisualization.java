package app;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GroupVisualization extends Application {
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;

    // Пример коллекции учебных групп
    private List<EducationalGroup> educationalGroups;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Создаем некоторые тестовые учебные группы
        educationalGroups = createTestEducationalGroups();

        Group root = new Group();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        // Отображаем учебные группы в виде окружностей и добавляем их имена
        for (EducationalGroup group : educationalGroups) {
            Circle circle = new Circle();
            circle.setCenterX(group.getX());
            circle.setCenterY(group.getY());
            circle.setRadius(group.getNumberOfStudents()); // Радиус зависит от количества учеников
            circle.setFill(Color.BLUE); // Цвет окружности
            root.getChildren().add(circle);

            Text text = new Text(group.getX() - 15, group.getY() - group.getNumberOfStudents() - 5, group.getName());
            root.getChildren().add(text);
        }

        primaryStage.setTitle("Group Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Пример создания тестовых учебных групп
    private List<EducationalGroup> createTestEducationalGroups() {
        List<EducationalGroup> groups = new ArrayList<>();

        groups.add(new EducationalGroup("Group 1", 100, 200, 50));
        groups.add(new EducationalGroup("Group 2", 300, 400, 30));
        groups.add(new EducationalGroup("Group 3", 500, 100, 70));

        return groups;
    }

    // Пример класса учебной группы
    private class EducationalGroup {
        private String name;
        private double x;
        private double y;
        private int numberOfStudents;

        public EducationalGroup(String name, double x, double y, int numberOfStudents) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.numberOfStudents = numberOfStudents;
        }

        public String getName() {
            return name;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public int getNumberOfStudents() {
            return numberOfStudents;
        }
    }
}

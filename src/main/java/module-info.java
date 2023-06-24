module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires slf4j.api;
    requires java.sql;
    requires json;


    opens app to javafx.fxml;
    opens app.common.collectionClasses to javafx.base;
    exports app;
}
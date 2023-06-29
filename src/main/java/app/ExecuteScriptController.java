package app;

import app.client.ScriptExecutor;
import app.exceptions.FileAccessException;
import app.exceptions.ScriptRecursionException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ExecuteScriptController implements Initializable {

    @FXML
    private Label labelEnterPath;

    @FXML
    private TextField textFieldPath;

    @FXML
    private Button buttonExecuteScript;

    @FXML
    private TextArea textAreaOutput;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonExecuteScript.setOnAction(event -> {
            textAreaOutput.clear();
            ScriptExecutor scriptExecutor = new ScriptExecutor();
            try {
                scriptExecutor.executeScript(textFieldPath.getText(), textAreaOutput);
            } catch (ScriptRecursionException | FileAccessException e) {
                showErrorWindow(e.getMessage());
            }
        });
    }

    private void showErrorWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(this.resourceBundle.getString("error"));

        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private ResourceBundle resourceBundle;

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
}

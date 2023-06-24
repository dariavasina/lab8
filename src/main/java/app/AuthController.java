package app;

import app.App;
import app.client.Authenticator;
import app.networkStructures.AuthenticationRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.spi.ResourceBundleControlProvider;

public class AuthController {
    @FXML
    private PasswordField passwordInput;
    @FXML
    private TextField usernameInput;
    @FXML
    private Button loginButton;
    @FXML
    private Label labelSuccess;
    @FXML
    private Button buttonSignUp;
    @FXML
    private Menu menuBar;

    @FXML
    private MenuItem czech;

    @FXML
    private MenuItem english;
    @FXML
    private MenuItem russian;

    @FXML
    private MenuItem swedish;

    private ResourceBundle resourceBundle;

    @FXML
    private Label labelLogin;

    @FXML
    private Label labelPassword;

    @FXML
    public void initialize() {
        setupTabTraversal();
        resourceBundle = ResourceBundle.getBundle("resources", new Locale("ru", "RU"));
        setInterfaceTexts();

        czech.setOnAction(this::setLanguage);
        english.setOnAction(this::setLanguage);
        russian.setOnAction(this::setLanguage);
        swedish.setOnAction(this::setLanguage);

    }

    private void setupTabTraversal() {
        usernameInput.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case TAB:
                    event.consume();
                    passwordInput.requestFocus();
                    break;
            }
        });

        passwordInput.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case TAB:
                    event.consume();
                    usernameInput.requestFocus();
                    break;
                case ENTER: // Handle Enter key press
                    event.consume();
                    loginButton.fire(); // Simulate button click
                    break;
            }
        });
    }

    private void setInterfaceTexts() {

        loginButton.setText(resourceBundle.getString("logIn"));
        labelLogin.setText(resourceBundle.getString("username"));
        labelPassword.setText(resourceBundle.getString("password"));
        labelSuccess.setText(resourceBundle.getString("wrongPassword"));
        buttonSignUp.setText(resourceBundle.getString("signUp"));
        menuBar.setText(resourceBundle.getString("language"));
        czech.setText(resourceBundle.getString("czech"));
        english.setText(resourceBundle.getString("english"));
        russian.setText(resourceBundle.getString("russian"));
        swedish.setText(resourceBundle.getString("swedish"));

        labelSuccess.setVisible(false);
    }

    @FXML
    private void setLanguage(ActionEvent event) {
        MenuItem selectedLanguage = (MenuItem) event.getSource();
        String languageCode = selectedLanguage.getId();

        switch (languageCode) {
            case "czech":
                resourceBundle = ResourceBundle.getBundle("resources", new Locale("cs", "CZ"));
                break;
            case "english":
                resourceBundle = ResourceBundle.getBundle("resources", new Locale("en", "US"));
                break;
            case "russian":
                resourceBundle = ResourceBundle.getBundle("resources", new Locale("ru", "RU"));
                break;
            case "swedish":
                resourceBundle = ResourceBundle.getBundle("resources", new Locale("sv", "SE"));
                break;
        }

        setInterfaceTexts();
    }







    @FXML
    protected void login(ActionEvent event) throws IOException {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        Authenticator authenticator = App.authenticator;
        password = authenticator.encodePassword(password);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(false, username, password);
        App.authenticator.authenticate(authenticationRequest);

        boolean flag = App.authenticator.isAuthFlag();
        if (flag) {
            App.networkManager.setUser(username, password);
            changeStage(event);
        } else {
            labelSuccess.setText("Неверный пароль. Попробуйте снова");
        }
    }

    @FXML
    protected void signUp(ActionEvent event) throws IOException {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        Authenticator authenticator = App.authenticator;
        password = authenticator.encodePassword(password);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(true, username, password);
        App.authenticator.authenticate(authenticationRequest);

        App.networkManager.setUser(username, password);

        changeStage(event);
    }

    public void changeStage(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setX(100);
        stage.setY(10);
        stage.show();
    }
 }
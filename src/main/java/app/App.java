package app;

import app.client.Authenticator;
import app.common.collectionManagement.StudyGroupCollectionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.NoSuchElementException;

public class App extends Application {
    public static Authenticator authenticator;
    public static NetworkManager networkManager;
    public static StudyGroupCollectionManager collectionManager;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("auth.fxml"));
        AuthController authController = new AuthController();
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("App");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Введите адрес сервера и порт");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[1]);
            InetAddress address = InetAddress.getByName(args[0]);
            authenticator = new Authenticator(address, port);
            networkManager = new NetworkManager(port, address);
            launch();


        } catch (NumberFormatException e) {
            System.out.println("Порт должен быть числом");
            System.exit(1);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host. Please try again");
            System.exit(0);
        } catch (NoSuchElementException e) {
            System.exit(0);
        }

    }
}
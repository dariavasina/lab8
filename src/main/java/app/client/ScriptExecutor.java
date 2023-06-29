package app.client;

import app.App;
import app.ExecuteScriptController;
import app.common.collectionClasses.StudyGroup;
import app.common.commands.CommandWithResponse;
import app.common.dataStructures.Triplet;
import app.exceptions.*;
import app.common.io.consoleIO.CommandParser;
import app.networkStructures.CommandRequest;
import app.networkStructures.CommandResponse;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;


import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;

public class ScriptExecutor {
    private HashSet<String> executedScripts = new HashSet<>();

    public void executeScript(String filename, TextArea textAreaOutput) throws ScriptRecursionException, FileAccessException {
        executedScripts.add(filename);

        CommandParser commandParser = new CommandParser();
        try {
            File file = new File(filename);
            if (!file.exists() || !file.canRead()) {
                throw new FileAccessException("Cannot read file: " + filename);
            }
            Scanner scanner = new Scanner(file);
            while (true) {
                try {
                    Triplet<String, String[], StudyGroup> parsedCommand = commandParser.readCommand(scanner, true);
                    String commandName = parsedCommand.getFirst();
                    String[] args = parsedCommand.getSecond();
                    if (commandName.equals("execute_script")) {
                        if (executedScripts.contains(args[0])) {
                            throw new ScriptRecursionException("You should not call execute_script recursively!");
                        }
                        executedScripts.add(args[0]);
                        executeScript(args[0], textAreaOutput);
                    } else {
                        CommandParser cp = new CommandParser();
                        CommandWithResponse command = cp.pack(parsedCommand);
                        CommandRequest request = new CommandRequest(command);
                        request.setUsername(App.authenticator.getUsername());
                        request.setPassword(App.authenticator.getPassword());

                        CommandResponse response = App.networkManager.sendRequest(request);

                        if (response == null) {
                            textAreaOutput.appendText("Server is down\nPlease try again later");
                        } else {
                            textAreaOutput.appendText(response.getOutput());
                        }
                    }
                } catch (CommandDoesNotExistException | ScriptRecursionException | InvalidInputException |
                         KeyDoesNotExistException
                         | InvalidArgumentsException | KeyAlreadyExistsException e) {
                    showErrorWindow(e.getMessage());

                } catch (Exception e) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showErrorWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erorr");

        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


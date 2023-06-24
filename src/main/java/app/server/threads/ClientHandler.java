package app.server.threads;

import app.common.collectionManagement.StudyGroupCollectionManager;
import app.common.commands.CommandExecutor;
import app.common.commands.CommandWithResponse;
import app.exceptions.InvalidInputException;
import app.networkStructures.*;
import databaseManagement.DataLoader;
import databaseManagement.DatabaseHandler;
import databaseManagement.DatabaseManager;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final CommandExecutor commandExecutor;
    private final StudyGroupCollectionManager collectionManager;
    private final ExecutorService commandExecutionThreadPool;
    private final DatabaseHandler databaseHandler;
    private final Logger logger;

    public ClientHandler(Socket clientSocket, CommandExecutor commandExecutor, StudyGroupCollectionManager collectionManager, ExecutorService commandExecutionThreadPool, DatabaseHandler databaseHandler, Logger logger) {
        this.clientSocket = clientSocket;
        this.commandExecutor = commandExecutor;
        this.collectionManager = collectionManager;
        this.commandExecutionThreadPool = commandExecutionThreadPool;
        this.databaseHandler = databaseHandler;
        this.logger = logger;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            Request object = null;
            DataLoader dataLoader = new DataLoader();
            try {
                object = (Request) in.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
            if (object.getClass() == AuthenticationRequest.class) {
                AuthenticationResponse response = new AuthenticationResponse();
                AuthenticationRequest request = (AuthenticationRequest) object;
                String username = request.getUsername();
                String password = request.getPassword();

                logger.info("Received authentication request from user {}", username);

                try {
                    if (request.isNewUser()) {
                        boolean registrationResult = databaseHandler.registerUser(username, password);
                        response.setAuthenticated(registrationResult);

                        if (!registrationResult) {
                            response.setException(new InvalidInputException("User with the entered username already exists"));
                        }

                    } else {
                        response.setAuthenticated(true);

                        if (!databaseHandler.userExists(username)) {
                            response.setAuthenticated(false);
                            response.setException(new InvalidInputException("Unknown username"));
                        }

                        if (databaseHandler.userExists(username) && !databaseHandler.getUsersPassword(username).equals(password)) {
                            response.setAuthenticated(false);
                            response.setException(new InvalidInputException("Invalid password"));
                        }
                    }

                } catch (SQLException e) {
                    response.setAuthenticated(false);
                    response.setException(e);
                }
                try {
                    out.writeObject(response);
                    out.flush();
                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                }

                //System.out.println(response.getOutput());
                logger.info("Response to authentication successfully sent to app.client");


            } else {
                CommandRequest request = (CommandRequest) object;
                logger.info("Received request: " + request.getCommand());

                try {
                    if (!request.getPassword().equals(databaseHandler.getUsersPassword(request.getUsername()))) {
                        Response response = new CommandResponse("Exception: Authentication error ");

                        try {
                            out.writeObject(response);
                            out.flush();
                            logger.info("Response to a command successfully sent to app.client");
                        } catch (IOException e) {
                            logger.error("Error sending response: " + e.getMessage());
                        }
                        return;
                    }
                } catch (SQLException e) {
                    logger.error("SQL Exception: " + e.getMessage());
                }


                CommandWithResponse command = request.getCommand();
                command.setDatabaseHandler(databaseHandler);

                commandExecutor.setCollection(collectionManager);
                command.setCollection(collectionManager);


                Future<CommandResponse> futureResponse = commandExecutionThreadPool.submit(() -> {
                    try {
                        command.setDatabaseManager(new DatabaseManager(databaseHandler, dataLoader));
                        command.setUsername(request.getUsername());

                        commandExecutor.execute(command);
                        return commandExecutor.getCommandResponse();
                    } catch (Exception e) {
                        logger.error("Command " +  command.getClass() + " threw the exception: " + e.getClass() +
                                "\n" + e.getMessage());
                        return new CommandResponse("Exception: " + e.getMessage());
                    }
                });

                try {
                    out.writeObject(futureResponse.get());
                    out.flush();
                    logger.info("Response to a command successfully sent to app.client");
                } catch (IOException e) {
                    logger.error("Error sending response: " + e.getMessage());
                } catch (ExecutionException | InterruptedException ex) {
                    logger.error("Error receiving response: " + ex.getMessage());
                }

            }
        } catch (IOException e) {
           logger.error("SQL Error: " + e.getMessage());
        }
    }
}
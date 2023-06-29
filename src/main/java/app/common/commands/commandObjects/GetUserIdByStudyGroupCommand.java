package app.common.commands.commandObjects;

import app.common.collectionClasses.StudyGroup;
import app.common.commands.CommandWithResponse;
import app.exceptions.EmptyCollectionException;
import app.exceptions.InvalidArgumentsException;
import app.exceptions.ObjectAccessException;
import app.networkStructures.CommandResponse;
import databaseManagement.DatabaseManager;

import java.sql.SQLException;
import java.util.Map;

public class GetUserIdByStudyGroupCommand extends CommandWithResponse {

    private StringBuilder output;

    @Override
    public void setArgs(String[] args) throws InvalidArgumentsException {
        Long key;
        try {
            key = Long.parseLong(args[0]);
            super.setArgs(new String[]{String.valueOf(key)});
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("The key must be a number! Please Try to enter a command again");
        }
    }


    @Override
    public void execute() throws EmptyCollectionException, SQLException {

        output = new StringBuilder();

        long id = Long.parseLong(getArgs()[0]);
        DatabaseManager databaseManager = getDatabaseManager();

        output.append(databaseManager.getOwner(id));
    }

    @Override
    public CommandResponse getCommandResponse() {
        return new CommandResponse(output.toString());
    }
}

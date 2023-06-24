package app.common.commands.commandObjects;

import app.common.collectionClasses.StudyGroup;
import app.common.collectionManagement.StudyGroupCollectionManager;
import app.common.commands.CommandWithResponse;
import app.exceptions.InvalidArgumentsException;
import app.exceptions.ObjectAccessException;
import app.networkStructures.CommandResponse;
import databaseManagement.DatabaseManager;

import java.sql.SQLException;
import java.util.Map;

public class RemoveKeyCommand extends CommandWithResponse {
    public RemoveKeyCommand(StudyGroupCollectionManager collection) {
        super(collection);
    }

    public RemoveKeyCommand() {
    }

    @Override
    public void setArgs(String[] args) throws InvalidArgumentsException {
        try {
            Long key = Long.parseLong(args[0]);
            super.setArgs(new String[]{String.valueOf(key)});
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("The key must be a number! Please try to enter a command again");
        }


    }

    @Override
    public void execute() throws ObjectAccessException, SQLException {
        Long key = Long.parseLong(getArgs()[0]);
        try {
            getCollection().getWriteLock().lock();
            Map<Long, StudyGroup> data = getCollection().getMap();

            if (!getCollection().getElementsOwners().get(data.get(key).getId()).equals(getUsername())) {
                throw new ObjectAccessException();
            }

            DatabaseManager dbm = getDatabaseManager();
            dbm.removeStudyGroup(data.get(key).getId());
            getCollection().removeByKey(key);

            Map<Long, StudyGroup> changedCollection = getCollection().getMap();
            for (long k: changedCollection.keySet()) {
                System.out.println(k);
                System.out.println(changedCollection.get(k));
            }

        } finally {
            getCollection().getWriteLock().unlock();
        }
    }

    @Override
    public CommandResponse getCommandResponse() {
        return new CommandResponse("remove_key finished successfully");
    }
}

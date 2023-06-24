package app.common.commands.commandObjects;

import app.common.collectionClasses.StudyGroup;
import app.common.collectionManagement.StudyGroupCollectionManager;
import app.common.commands.CommandWithResponse;
import app.exceptions.IdDoesNotExistException;
import app.exceptions.InvalidArgumentsException;
import app.exceptions.ObjectAccessException;
import app.networkStructures.CommandResponse;
import databaseManagement.DatabaseManager;

import java.sql.SQLException;
import java.util.Map;

public class UpdateCommand extends CommandWithResponse {
    private Long id;
    private StudyGroup value;

    public UpdateCommand(StudyGroupCollectionManager collection) {
        super(collection);
    }

    public UpdateCommand() {
    }

    @Override
    public void setArgs(String[] args) throws InvalidArgumentsException {
        try {
            id = Long.parseLong(args[0]);
            super.setArgs(new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("The id must be a number! Please try to enter a command again");
        }
    }

    @Override
    public CommandResponse getCommandResponse() {
        return new CommandResponse("update finished successfully");
    }

    @Override
    public void execute() throws IdDoesNotExistException, InvalidArgumentsException, ObjectAccessException, SQLException {
        try {
            getCollection().getWriteLock().lock();
            Map<Long, StudyGroup> data = getCollection().getMap();

            StudyGroup newStudyGroup = getStudyGroup();
            newStudyGroup.setId(id);

            boolean containsId = data.values().stream().anyMatch(studyGroup -> studyGroup.getId().equals(id));
            //System.out.println(containsId);
            if (!containsId) {
                throw new InvalidArgumentsException("Can't find the entered id in collection\nPlease try to enter the command again");
            }

            if (!getCollection().getElementsOwners().get(id).equals(getUsername())) {
                throw new ObjectAccessException();
            }

            DatabaseManager databaseManager = getDatabaseManager();

            for (Long key : data.keySet()) {
                StudyGroup studyGroup = data.get(key);

                if (id.equals(studyGroup.getId())) {
                    databaseManager.removeStudyGroup(studyGroup.getId());
                    databaseManager.insertStudyGroupWithId(newStudyGroup, getUsername(), key);
                }
            }

            getCollection().updateByID(id, newStudyGroup);

            System.out.println("after update on map");
            for (Long key: getCollection().getCollection().keySet()) {
                System.out.println(key);
                System.out.println(getCollection().getCollection().get(key));
            }
        } finally {
            getCollection().getWriteLock().unlock();
        }
    }
}

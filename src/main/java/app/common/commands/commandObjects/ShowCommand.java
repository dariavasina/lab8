package app.common.commands.commandObjects;

import app.common.collectionClasses.StudyGroup;
import app.common.collectionManagement.StudyGroupCollectionManager;
import app.common.commands.CommandWithResponse;
import app.exceptions.EmptyCollectionException;
import app.networkStructures.CommandResponse;

import java.util.Map;

public class ShowCommand extends CommandWithResponse {
    private StringBuilder output;
    public ShowCommand(StudyGroupCollectionManager collection) {
        super(collection);
    }

    public ShowCommand() {
    }

    @Override
    public void execute() throws EmptyCollectionException {
        try {
            getCollection().getReadLock().lock();
            output = getCollection().show();
        } catch (EmptyCollectionException e) {
            output = new StringBuilder(e.getMessage());
        } finally {
            getCollection().getReadLock().unlock();
        }
    }

    @Override
    public CommandResponse getCommandResponse() {
        CommandResponse response = new CommandResponse(output.toString());
        response.setCollectionMap(getCollection().getMap());
        return response;
    }
}

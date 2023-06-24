package app.common.commands.commandObjects;

import app.common.collectionManagement.StudyGroupCollectionManager;
import app.common.commands.CommandWithResponse;
import app.networkStructures.CommandResponse;

public class InfoCommand extends CommandWithResponse {
    public InfoCommand(StudyGroupCollectionManager collection) {
        super(collection);
    }
    private StringBuilder output;

    public InfoCommand() {
    }

    @Override
    public void execute() {
        try {
            getCollection().getReadLock().lock();
            output = getCollection().info();
        } finally {
            getCollection().getReadLock().unlock();
        }
    }

    @Override
    public CommandResponse getCommandResponse() {
        return new CommandResponse(output.toString());
    }
}

package app.common.commands.commandObjects;

import app.common.collectionManagement.StudyGroupCollectionManager;
import app.common.commands.CommandWithResponse;
import app.networkStructures.CommandResponse;

public class PrintFieldDescendingStudentsCountCommand extends CommandWithResponse {
    StringBuilder output = new StringBuilder();
    public PrintFieldDescendingStudentsCountCommand(StudyGroupCollectionManager collection) {
        super(collection);
    }

    public PrintFieldDescendingStudentsCountCommand() {
    }

    @Override
    public void execute() {
        try {
            getCollection().getReadLock().lock();
            output = getCollection().printFieldDescendingStudentsCount();
        } finally {
            getCollection().getReadLock().unlock();
        }
    }

    @Override
    public CommandResponse getCommandResponse() {
        return new CommandResponse(output.toString());
    }
}

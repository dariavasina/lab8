package app.common.commands;

import app.common.collectionManagement.StudyGroupCollectionManager;
import app.networkStructures.CommandResponse;

public abstract class CommandWithResponse extends Command{

    public CommandWithResponse(StudyGroupCollectionManager collection) {
        super(collection);
    }

    public CommandWithResponse() {};

    public abstract CommandResponse getCommandResponse();
}

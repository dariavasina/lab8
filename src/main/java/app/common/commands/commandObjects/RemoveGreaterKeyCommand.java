package app.common.commands.commandObjects;

import app.common.collectionManagement.StudyGroupCollectionManager;
import app.common.commands.CommandWithResponse;
import app.exceptions.InvalidArgumentsException;
import app.networkStructures.CommandResponse;

public class RemoveGreaterKeyCommand extends CommandWithResponse {
    public RemoveGreaterKeyCommand(StudyGroupCollectionManager collection) {
        super(collection);
    }

    public RemoveGreaterKeyCommand() {
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
    public void execute() {
        Long key = Long.parseLong(getArgs()[0]);
        try {
            getCollection().getWriteLock().lock();
            getCollection().removeGreaterKey(key);
        } finally {
            getCollection().getWriteLock().unlock();
        }
    }

    @Override
    public CommandResponse getCommandResponse() {
        return new CommandResponse("remove_greater_key finished successfully");
    }
}

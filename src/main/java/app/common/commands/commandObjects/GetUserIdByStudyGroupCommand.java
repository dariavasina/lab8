package app.common.commands.commandObjects;

import app.common.collectionClasses.StudyGroup;
import app.common.commands.CommandWithResponse;
import app.exceptions.ObjectAccessException;
import app.networkStructures.CommandResponse;

import java.util.Map;

public class GetUserIdByStudyGroupCommand extends CommandWithResponse {
    @Override
    public void execute() throws Exception {
        Long key = Long.parseLong(getArgs()[0]);
        try {
            getCollection().getReadLock().lock();
            Map<Long, StudyGroup> data = getCollection().getMap();

            getCollection().getElementsOwners().get(data.get(key).getId()).equals(getUsername());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public CommandResponse getCommandResponse() {
        return null;
    }
}

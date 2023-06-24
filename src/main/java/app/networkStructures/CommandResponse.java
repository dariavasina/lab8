package app.networkStructures;

import app.common.collectionClasses.StudyGroup;

import java.io.Serializable;
import java.util.Map;

public class CommandResponse extends Response {
    public String command;
    public String output;
    public String[] args;
    public Serializable object;

    public CommandResponse(String output) {
        this.output = output;
    }

    public CommandResponse(String command, String[] args, String output) {
        this.command = command;
        this.output = output;
    }

    public CommandResponse(String command, String[] args, String output, Serializable object) {
        this.command = command;
        this.output = output;
        //this.object = object;
    }

    public String getCommand() {
        return command;
    }
    public String getOutput() {
        return output;
    }

    Map<Long, StudyGroup> collectionMap;

    public void setCollectionMap(Map<Long, StudyGroup> collectionMap) {
        this.collectionMap = collectionMap;
    }

    public Map<Long, StudyGroup> getCollectionMap() {
        return collectionMap;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}

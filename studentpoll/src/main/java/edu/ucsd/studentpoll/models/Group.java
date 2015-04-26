package edu.ucsd.studentpoll.models;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kbuzsaki on 4/26/15.
 */
public class Group {

    private String name;

    private List<String> members;

    private String lastPoll;

    public Group() {
        this.name = "Group Name";
        this.members = Arrays.asList("John Smith", "Jane Doe", "Earl Warren");
        this.lastPoll = "What should we have for dinner?";
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getLastPoll() {
        return lastPoll;
    }

}

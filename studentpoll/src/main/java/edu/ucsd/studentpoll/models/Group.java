package edu.ucsd.studentpoll.models;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kbuzsaki on 4/26/15.
 */
public class Group implements Model {

    private long id;

    private String name;

    private List<User> members;

    private List<Poll> polls;

    public Group() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return members;
    }

    public List<Poll> getPolls() {
        return polls;
    }

}

package edu.ucsd.studentpoll.models;

import java.util.Date;
import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class Poll {

    private Group group;

    private User creator;

    private Date creationTime;

    private String name;

    private List<Question> questions;

    public Poll() {

    }

    public Group getGroup() {
        return group;
    }

    public User getCreator() {
        return creator;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

}

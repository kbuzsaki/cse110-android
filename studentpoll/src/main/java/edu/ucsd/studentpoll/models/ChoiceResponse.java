package edu.ucsd.studentpoll.models;

import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ChoiceResponse implements Response {

    private long id;

    private User responder;

    private ChoiceQuestion question;

    private List<String> choices;

    public ChoiceResponse() {

    }

    public long getId() {
        return id;
    }

    @Override
    public User getResponder() {
        return responder;
    }

    @Override
    public ChoiceQuestion getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }
}

package edu.ucsd.studentpoll.models;

import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ChoiceResponse implements Response {

    private User responder;

    private ChoiceQuestion question;

    private List<String> choices;

    public ChoiceResponse() {

    }

    public User getResponder() {
        return responder;
    }

    public ChoiceQuestion getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }
}

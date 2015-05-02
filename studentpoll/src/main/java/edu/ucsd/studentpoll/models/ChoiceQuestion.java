package edu.ucsd.studentpoll.models;

import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ChoiceQuestion implements Question {

    private Poll poll;

    private String title;

    private List<String> options;

    private List<ChoiceResponse> responses;

    public ChoiceQuestion() {

    }

    @Override
    public Poll getPoll() {
        return poll;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return options;
    }

    @Override
    public List<ChoiceResponse> getResponses() {
        return responses;
    }

}

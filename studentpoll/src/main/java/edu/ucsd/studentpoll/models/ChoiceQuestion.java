package edu.ucsd.studentpoll.models;

import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ChoiceQuestion implements Question {

    private Poll poll;

    private String title;

    private List<String> options;

    public ChoiceQuestion() {

    }

    public Poll getPoll() {
        return poll;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return options;
    }

}

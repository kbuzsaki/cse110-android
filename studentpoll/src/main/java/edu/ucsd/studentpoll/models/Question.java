package edu.ucsd.studentpoll.models;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public abstract class Question extends Model {

    public abstract Poll getPoll();

    public abstract String getTitle();

    public abstract List<? extends Response> getResponses();

}

package edu.ucsd.studentpoll.models;

import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public interface Question extends Model {

    public Poll getPoll();

    public String getTitle();

    public List<? extends Response> getResponses();

}

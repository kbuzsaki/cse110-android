package edu.ucsd.studentpoll.models;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public abstract class Response extends Model {

    protected Response() {
    }

    protected Response(Long id) {
        super(id);
    }

    public abstract User getResponder();

    public abstract Question getQuestion();

}

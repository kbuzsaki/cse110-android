package edu.ucsd.studentpoll.models;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public interface Response extends Model {

    public User getResponder();

    public Question getQuestion();

}
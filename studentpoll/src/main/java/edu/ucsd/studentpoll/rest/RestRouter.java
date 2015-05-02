package edu.ucsd.studentpoll.rest;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class RestRouter {

    private static String URL_BASE = "http://137.110.91.79:5000";

    private static String API_URL = URL_BASE + "/api";

    private RestRouter() {

    }

    public static String getGroup(long groupId) {
        return API_URL + "/groups/" + groupId;
    }

    public static String getUser(long userId) {
        return API_URL + "/users/" + userId;
    }

    public static String getPoll(long pollId) {
        return API_URL + "/polls/" + pollId;
    }

    public static String postPoll() {
        return API_URL + "/polls/create";
    }

    public static String getQuestion(long questionId) {
        return API_URL + "/questions/" + questionId;
    }

    public static String getResponse(long responseId) {
        return API_URL + "/responses/" + responseId;
    }

    public static String putResponse() {
        return API_URL + "/response/create";
    }

}

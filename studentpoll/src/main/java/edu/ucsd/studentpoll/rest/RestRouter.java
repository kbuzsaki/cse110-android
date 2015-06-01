package edu.ucsd.studentpoll.rest;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class RestRouter {

    private static String URL_BASE = "http://kbuzsaki.ddns.net";
    private static String PORT = "";
    private static String API_URL = URL_BASE + PORT + "/api";

    private RestRouter() {

    }

    public static String joinPoll() {
        return API_URL + "/join";
    }

    public static String getGroup(long groupId) {
        return API_URL + "/group/" + groupId;
    }

    public static String getUser(long userId) {
        return API_URL + "/user/groups/" + userId;
    }

    public static String updateUser(long userId) {
        return API_URL + "/user/update/" + userId;
    }

    public static String postUser() {
        return API_URL + "/user/create";
    }

    public static String getPoll(long pollId) {
        return API_URL + "/poll/" + pollId;
    }

    public static String postPoll() {
        return API_URL + "/poll/create";
    }

    public static String getQuestion(long questionId) {
        return API_URL + "/question/" + questionId;
    }

    public static String getResponse(long questionId, long responseId) {
        return API_URL + "/question/" + questionId + "/response/" + responseId;
    }

    public static String putResponse() {
        return API_URL + "/question/response/create";
    }

    public static String startBroadcast() {
        return API_URL + "/accesscode/create";
    }
}

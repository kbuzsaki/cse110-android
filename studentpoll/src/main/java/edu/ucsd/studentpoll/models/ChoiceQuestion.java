package edu.ucsd.studentpoll.models;

import android.util.Log;
import edu.ucsd.studentpoll.rest.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ChoiceQuestion implements Question {

    private static final String TAG = "ChoiceQuestion";
    private static final Map<Long, ChoiceQuestion> CACHE = new HashMap<>();

    private long id;

    private Poll poll;

    private String title;

    private List<String> options;

    private List<ChoiceResponse> responses;

    private ChoiceQuestion() {
        this(UNINITIALIZED);
    }

    private ChoiceQuestion(long id) {
        this.id = id;
    }

    public static ChoiceQuestion getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new ChoiceQuestion(id));
        }

        return CACHE.get(id);
    }

    ChoiceQuestion initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            poll = Poll.getOrStub(json.getLong("poll"));
            title = json.getString("title");
            options = JsonUtils.toListOfString(json.getJSONArray("options"));
            List<Long> responseIds = JsonUtils.toListOfLong(json.optJSONArray("responses"));
            responses = new ArrayList<>(responseIds.size());
            for(Long responseId : responseIds) {
                responses.add(ChoiceResponse.getOrStub(responseId));
            }
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }

        return this;
    }

    public long getId() {
        return id;
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

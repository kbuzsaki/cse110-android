package edu.ucsd.studentpoll.models;

import android.util.Log;
import com.google.common.collect.ImmutableMap;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ChoiceResponse extends Response {

    private static final String TAG = "ChoiceResponse";
    private static final Map<Long, ChoiceResponse> CACHE = new HashMap<>();

    private User responder;

    private ChoiceQuestion question;

    private List<String> choices;

    private ChoiceResponse() {
    }

    private ChoiceResponse(Long id) {
        super(id);
    }

    public static ChoiceResponse getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new ChoiceResponse(id));
        }

        return CACHE.get(id);
    }

    @Override
    public void inflate() {
        if(this.id == UNINITIALIZED) {
            throw new AssertionError("Attempting to inflate uninitialized Model!");
        }

        if(!inflated) {
            AndrestClient client = new AndrestClient();
            JSONObject response = client.get(RestRouter.getResponse(id));
            initFromJson(response);
            inflated = true;
        }
    }

    @Override
    ChoiceResponse initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            responder = User.getOrStub(json.getLong("responder"));
            question = ChoiceQuestion.getOrStub(json.getLong("question"));
            choices = JsonUtils.toListOfString(json.getJSONArray("choices"));
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }

        return this;
    }

    @Override
    JSONObject toJson() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public User getResponder() {
        return responder;
    }

    @Override
    public ChoiceQuestion getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }
}

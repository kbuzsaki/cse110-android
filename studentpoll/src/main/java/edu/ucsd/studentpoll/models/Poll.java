package edu.ucsd.studentpoll.models;

import android.util.Log;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class Poll extends Model {

    private static final String TAG = "Poll";
    private static Map<Long, Poll> CACHE = new HashMap<>();

    private boolean inflated = false;

    private Group group;

    private User creator;

    private Date creationTime;

    private String name;

    private List<Question> questions;

    private Poll() {
    }

    private Poll(Long id) {
        super(id);
    }

    public static Poll getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new Poll(id));
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
            JSONObject response = client.get(RestRouter.getPoll(id));
            initFromJson(response);
            inflated = true;
        }
    }

    @Override
    Poll initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            group = Group.getOrStub(json.getLong("group"));
            creator = User.getOrStub(json.getLong("creator"));
            creationTime = null;
            name = json.getString("name");
            List<Long> questionIds = JsonUtils.toListOfLong(json.optJSONArray("questions"));
            questions = new ArrayList<>(questionIds.size());
            for(Long questionId : questionIds) {
                // TODO: make this work for any type of question
                questions.add(ChoiceQuestion.getOrStub(questionId));
            }
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

    public Group getGroup() {
        return group;
    }

    public User getCreator() {
        return creator;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

}

package edu.ucsd.studentpoll.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ChoiceQuestion extends Question {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<ChoiceQuestion>() {
        @Override
        public ChoiceQuestion createFromParcel(Parcel source) {
            Long id = source.readLong();
            return ChoiceQuestion.getOrStub(id);
        }

        @Override
        public ChoiceQuestion[] newArray(int size) {
            return new ChoiceQuestion[size];
        }
    };

    private static final String TAG = "ChoiceQuestion";
    private static final Map<Long, ChoiceQuestion> CACHE = new HashMap<>();

    private static final String QUESTION_TYPE = "choice";

    private Poll poll;

    private String title;

    private List<String> options;

    private List<ChoiceResponse> responses;

    private ChoiceQuestion() {
    }

    private ChoiceQuestion(Long id) {
        super(id);
    }

    public static ChoiceQuestion getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new ChoiceQuestion(id));
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
            JSONObject response = client.get(RestRouter.getQuestion(id));
            initFromJson(response);
            inflated = true;
        }
    }

    @Override
    ChoiceQuestion initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            poll = Poll.getOrStub(json.getLong("poll"));
            title = json.getString("title");

            JSONObject content = json.getJSONObject("content");
            options = JsonUtils.toListOfString(content.getJSONArray("options"));
            List<Long> responseIds = JsonUtils.toListOfLong(content.optJSONArray("responses"));
            responses = new ArrayList<>(responseIds.size());
            for(Long responseId : responseIds) {
                responses.add(ChoiceResponse.getOrStub(responseId));
            }
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }

        return this;
    }

    @Override
    JSONObject toJson() {
        return JsonUtils.builder()
                .put("id", getId())
                .put("poll", getPoll().getId())
                .put("title", getTitle())
                .put("type", QUESTION_TYPE)
                .put("content", JsonUtils.builder().put("options", getOptions()).build())
                .put("responses", Model.mapIds(responses))
                .build();
    }

    @Override
    public Long getId() {
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

    public static ChoiceQuestion makeQuestion(Poll poll, String title, List<String> options) {
        ChoiceQuestion question = new ChoiceQuestion();
        question.poll = poll;
        question.title = title;
        question.options = options;
        question.responses = Collections.emptyList();
        return question;
    }

}

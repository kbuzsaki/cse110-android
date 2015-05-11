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

    private boolean allowMultiple;

    private boolean allowCustom;

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
            allowMultiple = content.getBoolean("allow_multiple");
            allowCustom = content.getBoolean("allow_custom");
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
                .put("content", JsonUtils.builder()
                    .put("options", getOptions())
                    .put("allow_multiple", getAllowMultiple())
                    .put("allow_custom", getAllowCustom())
                    .put("responses", Model.mapIds(responses))
                    .build())
                .build();
    }

    @Override
    public Long getId() {
        return id;
    }

    public ChoiceQuestion(String title, List<String> options, boolean allowMultiple) {
        this.title = title;
        this.options = options;
        this.allowMultiple = allowMultiple;
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

    public boolean getAllowMultiple() {
        return allowMultiple;
    }

    public boolean getAllowCustom() {
        return allowCustom;
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

    public static ChoiceQuestion fakeChoiceQuestionOne() {
        List<String> options = new ArrayList<>();
        options.add("Cat");
        options.add("Dog");
        ChoiceQuestion choiceQuestion = new ChoiceQuestion("Favorite Pet?", options, true);

        return choiceQuestion;
    }

    public static ChoiceQuestion fakeChoiceQuestionTwo() {
        List<String> options = new ArrayList<>();
        options.add("Pelican");
        options.add("Platypus");
        options.add("Polar Bear");
        options.add("Pig");
        ChoiceQuestion choiceQuestion = new ChoiceQuestion("Best Animal?", options, false);

        return choiceQuestion;
    }

    public static ChoiceQuestion fakeChoiceQuestionThree() {
        List<String> options = new ArrayList<>();
        options.add("No Fun");
        options.add("Fun");
        options.add("Double No Fun");
        options.add("Double Fun");
        options.add("Mega Fun");
        options.add("Mega No Fun");
        ChoiceQuestion choiceQuestion = new ChoiceQuestion("Fun or no Fun?", options, true);

        return choiceQuestion;
    }

    public static Map<String, Integer> fakeResponses() {

        Map<String, Integer> responses = new HashMap<>();
        responses.put("CatMan", 2);
        responses.put("BatMan", 4);
        responses.put("RatMan", 1);
        responses.put("HatMan", 6);
        responses.put("WatMan", 0);

        return responses;
    }

}

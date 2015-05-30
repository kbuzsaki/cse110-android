package edu.ucsd.studentpoll.models;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ChoiceQuestion extends Question {

    private static final byte TRUE_BYTE = 1;
    private static final byte FALSE_BYTE = 0;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<ChoiceQuestion>() {
        @Override
        public ChoiceQuestion createFromParcel(Parcel source) {
            byte INITIALIZED_BYTE = source.readByte();
            
            if(INITIALIZED_BYTE == TRUE_BYTE) {
                Long id = source.readLong();
                return ChoiceQuestion.getOrStub(id);
            }
            else {
                ChoiceQuestion question = new ChoiceQuestion();
                question.title = source.readString();
                question.options = new ArrayList<>();
                source.readStringList(question.options);
                question.allowMultiple = source.readByte() == TRUE_BYTE;
                question.allowCustom = source.readByte() == TRUE_BYTE;
                return question;
            }
        }

        @Override
        public ChoiceQuestion[] newArray(int size) {
            return new ChoiceQuestion[size];
        }
    };
    public static final ModelInstantiator<ChoiceQuestion> INSTANTIATOR = new ModelInstantiator<ChoiceQuestion>() {
        @Override
        public ChoiceQuestion fromId(Long id) {
            return ChoiceQuestion.getOrStub(id);
        }

        @Override
        public ChoiceQuestion fromJson(JSONObject object) throws JSONException {
            return ChoiceQuestion.getOrStub(JsonUtils.ripId(object)).initFromJson(object);
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
        }
    }

    @Override
    ChoiceQuestion initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            poll = Model.ripModel(json.get("poll"), Poll.INSTANTIATOR);
            title = json.getString("title");

            JSONObject content = json.getJSONObject("content");
            options = JsonUtils.toListOfString(content.getJSONArray("options"));
            allowMultiple = content.getBoolean("allow_multiple");
            allowCustom = content.getBoolean("allow_custom");
            responses = Model.ripModelList(content.optJSONArray("responses"), ChoiceResponse.INSTANTIATOR);

            markRefreshed();
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isInitialized() ? TRUE_BYTE : FALSE_BYTE);

        if(isInitialized()) {
            dest.writeLong(getId());
        }
        else {
            dest.writeString(title);
            dest.writeStringList(options);
            dest.writeByte(allowMultiple ? TRUE_BYTE : FALSE_BYTE);
            dest.writeByte(allowCustom   ? TRUE_BYTE : FALSE_BYTE);
        }
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

    public static ChoiceQuestion makeTemporaryQuestion(String title, List<String> options, boolean allowMultiple, boolean allowCustom) {
        ChoiceQuestion question = new ChoiceQuestion();
        question.title = title;
        question.options = new ArrayList<String>(new LinkedHashSet<String>(options));
        question.responses = Collections.emptyList();
        question.allowMultiple = allowMultiple;
        question.allowCustom = allowCustom;
        return question;
    }

    public static ChoiceQuestion makeQuestion(Poll poll, ChoiceQuestion question) {
        ChoiceQuestion newQuestion = new ChoiceQuestion();
        newQuestion.poll = poll;
        newQuestion.title = question.title;
        newQuestion.options = new ArrayList<String>(new LinkedHashSet<String>(question.options));
        newQuestion.responses = Collections.emptyList();
        newQuestion.allowMultiple = question.allowMultiple;
        newQuestion.allowCustom = question.allowCustom;
        return newQuestion;
    }

}

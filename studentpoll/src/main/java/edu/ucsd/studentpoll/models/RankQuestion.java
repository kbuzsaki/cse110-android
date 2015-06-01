package edu.ucsd.studentpoll.models;


import android.os.Parcel;
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
public class RankQuestion extends Question {

    private static final byte TRUE_BYTE = 1;
    private static final byte FALSE_BYTE = 0;

    public static final Creator CREATOR = new Creator<RankQuestion>() {
        @Override
        public RankQuestion createFromParcel(Parcel source) {
            byte INITIALIZED_BYTE = source.readByte();

            if(INITIALIZED_BYTE == TRUE_BYTE) {
                Long id = source.readLong();
                return RankQuestion.getOrStub(id);
            }
            else {
                RankQuestion question = new RankQuestion();
                question.title = source.readString();
                question.options = new ArrayList<>();
                source.readStringList(question.options);
                return question;
            }
        }

        @Override
        public RankQuestion[] newArray(int size) {
            return new RankQuestion[size];
        }
    };
    public static final ModelInstantiator<RankQuestion> INSTANTIATOR = new ModelInstantiator<RankQuestion>() {
        @Override
        public RankQuestion fromId(Long id) {
            return RankQuestion.getOrStub(id);
        }

        @Override
        public RankQuestion fromJson(JSONObject object) throws JSONException {
            return RankQuestion.getOrStub(JsonUtils.ripId(object)).initFromJson(object);
        }
    };

    private static final String TAG = "RankQuestion";
    private static final Map<Long, RankQuestion> CACHE = new HashMap<>();

    private static final String QUESTION_TYPE = "rank";

    private Poll poll;

    private String title;

    private List<String> options;

    private List<RankResponse> responses;

    private RankQuestion() {
    }

    private RankQuestion(Long id) {
        super(id);
    }

    public static RankQuestion getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new RankQuestion(id));
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
    RankQuestion initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            poll = Model.ripModel(json.get("poll"), Poll.INSTANTIATOR);
            title = json.getString("title");

            JSONObject content = json.getJSONObject("content");
            options = JsonUtils.toListOfString(content.getJSONArray("options"));
            responses = Model.ripModelList(content.optJSONArray("responses"), RankResponse.INSTANTIATOR);

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

    @Override
    public List<RankResponse> getResponses() {
        return responses;
    }

    public static RankQuestion makeTemporaryQuestion(String title, List<String> options) {
        RankQuestion question = new RankQuestion();
        question.title = title;
        question.options = new ArrayList<String>(new LinkedHashSet<String>(options));
        question.responses = Collections.emptyList();
        return question;
    }

    public static RankQuestion makeQuestion(Poll poll, RankQuestion question) {
        RankQuestion newQuestion = new RankQuestion();
        newQuestion.poll = poll;
        newQuestion.title = question.title;
        newQuestion.options = new ArrayList<String>(new LinkedHashSet<String>(question.options));
        newQuestion.options.remove(""); //prevent empty option
        newQuestion.responses = Collections.emptyList();
        return newQuestion;
    }

}

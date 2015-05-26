package edu.ucsd.studentpoll.models;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.common.collect.ImmutableMap;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class Poll extends Model {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Poll>() {
        @Override
        public Poll createFromParcel(Parcel source) {
            Long id = source.readLong();
            return Poll.getOrStub(id);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };
    public static final ModelInstantiator<Poll> INSTANTIATOR = new ModelInstantiator<Poll>() {
        @Override
        public Poll fromId(Long id) {
            return Poll.getOrStub(id);
        }

        @Override
        public Poll fromJson(JSONObject object) throws JSONException {
            return Poll.getOrStub(JsonUtils.ripId(object)).initFromJson(object);
        }
    };

    private static final String TAG = "Poll";
    private static Map<Long, Poll> CACHE = new HashMap<>();

    private Group group;

    private User creator;

    private Date creationTime;

    private String name;

    private List<? extends Question> questions;

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
        }
    }

    @Override
    Poll initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            group = Model.ripModel(json.get("group"), Group.INSTANTIATOR);
            creator = Model.ripModel(json.get("creator"), User.INSTANTIATOR);
            creationTime = null;
            name = json.getString("name");
            questions = Model.ripModelList(json.optJSONArray("questions"), Question.INSTANTIATOR);

            markRefreshed();
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }

        if(CACHE.containsKey(id) && CACHE.get(id) != this) {
            Log.w(TAG, "Initializing a poll that is already cached!");
        }
        else {
            CACHE.put(id, this);
        }

        return this;
    }

    @Override
    JSONObject toJson() {
        return JsonUtils.builder()
                .put("id", getId())
                .put("creator", getCreator().getId())
                .put("group", getGroup().getId())
                .put("name", getName())
                .put("questions", Model.mapJson(getQuestions()))
                .build();
    }

    @Override
    public Long getId() {
        return id;
    }

    public Poll(String name, List<Question> questions) {
        this.name = name;
        this.questions = questions;
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

    public List<? extends Question> getQuestions() {
        return questions;
    }

    public static class Builder {

        private Poll poll;
        private List<Question> questions;

        public Builder() {
            this.poll = new Poll();
            this.questions = new ArrayList<>();
        }

        public Builder withGroup(Group group) {
            poll.group = group;
            return this;
        }

        public Builder withTitle(String name) {
            poll.name = name;
            return this;
        }

        public Builder withChoiceQuestion(String title, List<String> options) {
            questions.add(ChoiceQuestion.makeQuestion(poll, title, options));
            return this;
        }

        public Builder withQuestions(List<Question> newQuestions) {
            for(Question question : newQuestions) {
                if(question instanceof ChoiceQuestion) {
                    questions.add(ChoiceQuestion.makeQuestion(poll, (ChoiceQuestion)question));
                }
                else if(question instanceof RankQuestion) {
                    questions.add(RankQuestion.makeQuestion(poll, (RankQuestion)question));
                }
                else {
                    throw new IllegalArgumentException("Question is not a valid question type: " + question);
                }
            }

            return this;
        }

        public Poll build() {
            if(poll.group == null) {
                poll.group = Group.getOrStub(UNINITIALIZED);
            }
            poll.creator = User.getDeviceUser();
            poll.questions = questions;
            return poll;
        }
    }

    public static Poll postPoll(Poll poll) {
        AndrestClient client = new AndrestClient();
        Map<String, JSONObject> data = ImmutableMap.of("poll", poll.toJson());
        JSONObject response = client.post(RestRouter.postPoll(), data);
        try {
            return Poll.INSTANTIATOR.fromJson(response);
        }
        catch (JSONException e) {
            Log.wtf(TAG, e);
            throw new RESTException(e);
        }
    }

    public static Poll joinPoll(String accessCode) {
        AndrestClient client = new AndrestClient();
        JSONObject response = client.put(RestRouter.joinPoll(accessCode), Collections.<String, JSONObject>emptyMap());
        try {
            return Poll.INSTANTIATOR.fromJson(response);
        }
        catch (JSONException e) {
            Log.wtf(TAG, e);
            throw new RESTException(e);
        }
    }

    public static String startBroadcast(Poll poll) {
            AndrestClient client = new AndrestClient();
            Map<String, String> data = ImmutableMap.of("poll", "" + poll.getId(), "user", "" + User.getDeviceUser().getId());
        try {
            String url = RestRouter.startBroadcast() + AndrestClient.escapeParameters(data);
            JSONObject response = client.post(url, Collections.<String, JSONObject>emptyMap());
            return response.getString("code");
        } catch(RESTException|UnsupportedEncodingException|JSONException e) {
            Log.e(TAG, "Failed to start broadcast", e);
            throw new RESTException(e);
        }
    }

    public static void stopBroadcast(Poll poll) {
        Log.w(TAG, "stopping a broadcast is stubbed out!");
    }
}

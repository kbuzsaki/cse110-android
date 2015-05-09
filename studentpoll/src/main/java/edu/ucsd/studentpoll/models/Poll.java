package edu.ucsd.studentpoll.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.common.collect.ImmutableMap;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RestRouter;
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

            // extra crap to make wildcards work properly
            List<Long> questionIds = JsonUtils.toListOfLong(json.optJSONArray("questions"));
            List<Question> localQuestions = new ArrayList<>();
            for(Long questionId : questionIds) {
                // TODO: make this work for any type of question
                Question question = (Question)ChoiceQuestion.getOrStub(questionId);
                localQuestions.add(question);
            }
            questions = localQuestions;
        } catch (JSONException e) {
            Log.wtf(TAG, e);
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
        return new Poll().initFromJson(response);
    }

}

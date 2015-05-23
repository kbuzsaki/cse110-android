package edu.ucsd.studentpoll.models;

import android.os.Parcel;
import android.util.Log;
import com.google.common.collect.ImmutableMap;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class RankResponse extends Response {

    public static final Creator CREATOR = new Creator<RankResponse>() {
        @Override
        public RankResponse createFromParcel(Parcel source) {
            Long id = source.readLong();
            return RankResponse.getOrStub(id);
        }

        @Override
        public RankResponse[] newArray(int size) {
            return new RankResponse[size];
        }
    };
    public static final ModelInstantiator<RankResponse> INSTANTIATOR = new ModelInstantiator<RankResponse>() {
        @Override
        public RankResponse fromId(Long id) {
            return RankResponse.getOrStub(id);
        }

        @Override
        public RankResponse fromJson(JSONObject object) throws JSONException {
            return RankResponse.getOrStub(JsonUtils.ripId(object)).initFromJson(object);
        }
    };

    private static final String TAG = "RankResponse";
    private static final Map<Long, RankResponse> CACHE = new HashMap<>();

    private User responder;

    private RankQuestion question;

    private List<String> choices;

    private RankResponse() {
    }

    private RankResponse(Long id) {
        super(id);
    }

    public static RankResponse getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new RankResponse(id));
        }

        return CACHE.get(id);
    }

    public static RankResponse getOrStubHack(RankQuestion question, Long id) {
        RankResponse response = getOrStub(id);
        response.question = question;
        return response;
    }

    @Override
    public void inflate() {
        if(this.id == UNINITIALIZED) {
            throw new AssertionError("Attempting to inflate uninitialized Model!");
        }

        if(!inflated) {
            AndrestClient client = new AndrestClient();
            JSONObject response = client.get(RestRouter.getResponse(question.getId(), id));
            initFromJson(response);
        }
    }

    @Override
    RankResponse initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            responder = Model.ripModel(json.get("responder"), User.INSTANTIATOR);
            question = Model.ripModel(json.get("question"), RankQuestion.INSTANTIATOR);
            choices = JsonUtils.toListOfString(json.getJSONArray("choices"));

            markRefreshed();
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }

        return this;
    }

    @Override
    JSONObject toJson() {
        return JsonUtils.builder()
                .put("responder", getResponder().getId())
                .put("question", getQuestion().getId())
                .put("choices", new JSONArray(getChoices()))
                .build();
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
    public RankQuestion getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }

    public static Map<String, Integer> aggregateResponses(Collection<RankResponse> responses) {
        Map<String, Integer> aggregate = new HashMap<>();
        return aggregate;
    }

    public static RankResponse putResponse(RankQuestion question, List<String> choices) {
        AndrestClient client = new AndrestClient();
        RankResponse rankResponse = new RankResponse();
        rankResponse.responder = User.getDeviceUser();
        rankResponse.question = question;
        rankResponse.choices = choices;
        Map<String, JSONObject> data = ImmutableMap.of("response", rankResponse.toJson());
        JSONObject response = client.put(RestRouter.putResponse(question.getId()), data);
        return new RankResponse().initFromJson(response);
    }

}

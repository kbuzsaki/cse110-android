package edu.ucsd.studentpoll.models;

import android.os.Parcel;
import android.util.Log;
import com.google.common.collect.ImmutableMap;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

    public static List<String> aggregateResponses(List<String> options, Collection<RankResponse> responses) {
        final Map<String, Integer> aggregate = new HashMap<>();

        for(String option : options) {
            aggregate.put(option, 0);
        }

        for(RankResponse response : responses) {
            for(int i = 0; i < response.getChoices().size(); i++) {
                String choice = response.getChoices().get(i);
                Integer count = aggregate.containsKey(choice) ? aggregate.get(choice) : 0;

                aggregate.put(choice, count + i);
            }
        }

        List<String> ranking = new ArrayList<>(aggregate.keySet());
        Collections.sort(ranking, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                Integer lhsVal = aggregate.get(lhs);
                Integer rhsVal = aggregate.get(rhs);
                return Integer.compare(lhsVal != null ? lhsVal : 0, rhsVal != null ? rhsVal : 0);
            }
        });

        Log.d(TAG, "final aggregate: " + aggregate);
        Log.d(TAG, "final ranking: " + ranking);

        return ranking;
    }

    public static RankResponse putResponse(RankQuestion question, List<String> choices) {
        AndrestClient client = new AndrestClient();
        RankResponse rankResponse = new RankResponse();
        rankResponse.responder = User.getDeviceUser();
        rankResponse.question = question;
        rankResponse.choices = choices;
        Map<String, JSONObject> data = ImmutableMap.of("response", rankResponse.toJson());
        JSONObject response = client.put(RestRouter.putResponse(), data);
        try {
            return RankResponse.INSTANTIATOR.fromJson(response);
        }
        catch(JSONException e) {
            Log.wtf(TAG, e);
            throw new RESTException(e);
        }
    }

}

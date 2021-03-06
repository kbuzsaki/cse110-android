package edu.ucsd.studentpoll.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.common.collect.ImmutableMap;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RESTException;
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
public class ChoiceResponse extends Response {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<ChoiceResponse>() {
        @Override
        public ChoiceResponse createFromParcel(Parcel source) {
            Long id = source.readLong();
            return ChoiceResponse.getOrStub(id);
        }

        @Override
        public ChoiceResponse[] newArray(int size) {
            return new ChoiceResponse[size];
        }
    };
    public static final ModelInstantiator<ChoiceResponse> INSTANTIATOR = new ModelInstantiator<ChoiceResponse>() {
        @Override
        public ChoiceResponse fromId(Long id) {
            return ChoiceResponse.getOrStub(id);
        }

        @Override
        public ChoiceResponse fromJson(JSONObject object) throws JSONException {
            return ChoiceResponse.getOrStub(JsonUtils.ripId(object)).initFromJson(object);
        }
    };

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

    public static ChoiceResponse getOrStubHack(ChoiceQuestion question, Long id) {
        ChoiceResponse response = getOrStub(id);
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
    ChoiceResponse initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            responder = Model.ripModel(json.get("responder"), User.INSTANTIATOR);
            question = Model.ripModel(json.get("question"), ChoiceQuestion.INSTANTIATOR);
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
    public ChoiceQuestion getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }

    public static Map<String, Integer> aggregateResponses(Collection<ChoiceResponse> responses) {
        Map<String, Integer> aggregate = new HashMap<>();

        Log.d(TAG, "" + responses.size());

        for(ChoiceResponse response : responses) {
            for(String choice : response.getChoices()) {
                Integer count = aggregate.get(choice);
                count = count != null ? count : 0;
                aggregate.put(choice, count + 1);
            }
        }

        return aggregate;
    }

    public static ChoiceResponse putResponse(ChoiceQuestion question, List<String> choices) {
        AndrestClient client = new AndrestClient();
        ChoiceResponse choiceResponse = new ChoiceResponse();
        choiceResponse.responder = User.getDeviceUser();
        choiceResponse.question = question;
        choiceResponse.choices = choices;
        Map<String, JSONObject> data = ImmutableMap.of("response", choiceResponse.toJson());
        JSONObject response = client.put(RestRouter.putResponse(), data);
        try {
            return ChoiceResponse.INSTANTIATOR.fromJson(response);
        }
        catch (JSONException e) {
            Log.wtf(TAG, e);
            throw new RESTException(e);
        }
    }

}

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 4/26/15.
 */
public class Group extends Model {

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            Long id = source.readLong();
            return Group.getOrStub(id);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
    public static final ModelInstantiator<Group> INSTANTIATOR = new ModelInstantiator<Group>() {
        @Override
        public Group fromId(Long id) {
            return Group.getOrStub(id);
        }

        @Override
        public Group fromJson(JSONObject object) throws JSONException {
            return Group.getOrStub(JsonUtils.ripId(object)).initFromJson(object);
        }
    };

    private static final String TAG = "Group";
    private static Map<Long, Group> CACHE = new HashMap<>();

    private String name;

    private List<User> members;

    private List<Poll> polls;

    private Group() {
    }

    private Group(Long id) {
        super(id);
    }

    public static Group getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new Group(id));
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
            JSONObject response = client.get(RestRouter.getGroup(id));
            initFromJson(response);
        }
    }

    @Override
    Group initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            name = json.getString("name");
            members = Model.ripModelList(json.optJSONArray("members"), User.INSTANTIATOR);
            polls = Model.ripModelList(json.optJSONArray("polls"), Poll.INSTANTIATOR);

            markRefreshed();
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

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return members;
    }

    public List<Poll> getPolls() {
        return polls;
    }

}

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private Date creationTime;

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
            DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
            String creationTimeString = json.getString("createdAt");
            creationTime = iso8601Format.parse(creationTimeString);

            markRefreshed();
        } catch (JSONException|ParseException e) {
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

    public Date getCreationTime() {
        return creationTime;
    }

    public static Group updateGroupName(Group group, String newName) {
        AndrestClient client = new AndrestClient();
        Map<String, ?> data = ImmutableMap.of("name", newName);
        JSONObject response = client.put(RestRouter.updateGroup(group.getId()), data);
        group.initFromJson(response);
        return group;
    }

}

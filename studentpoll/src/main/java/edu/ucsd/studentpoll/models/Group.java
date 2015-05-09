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
            inflated = true;
        }
    }

    @Override
    Group initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            name = json.getString("name");
            List<Long> memberIds = JsonUtils.toListOfLong(json.optJSONArray("members"));
            members = new ArrayList<>(memberIds.size());
            for(Long memberId : memberIds) {
                members.add(User.getOrStub(memberId));
            }
            List<Long> pollIds = JsonUtils.toListOfLong(json.optJSONArray("polls"));
            polls = new ArrayList<>(pollIds.size());
            for(Long pollId : pollIds) {
                polls.add(Poll.getOrStub(pollId));
            }
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

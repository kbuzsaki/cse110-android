package edu.ucsd.studentpoll.models;

import android.util.Log;
import edu.ucsd.studentpoll.rest.JsonUtils;
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
public class Group implements Model {

    private static final String TAG = "Group";
    private static Map<Long, Group> CACHE = new HashMap<>();

    private long id;

    private String name;

    private List<User> members;

    private List<Poll> polls;

    private Group() {
        this(UNINITIALIZED);
    }

    private Group(long id) {
        this.id = id;
    }

    public static Group getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new Group(id));
        }

        return CACHE.get(id);
    }

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

    public long getId() {
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

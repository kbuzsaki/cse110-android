package edu.ucsd.studentpoll.models;

import android.graphics.drawable.Drawable;
import android.util.Log;
import edu.ucsd.studentpoll.rest.AndrestClient;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RestRouter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class User implements Model {

    private static final String TAG = "User";
    private static final Map<Long, User> CACHE = new HashMap<>();

    private long id;

    private boolean inflated = false;

    private String name;

    private Drawable avatar;

    private List<Group> groups;

    private User() {
        this(UNINITIALIZED);
    }

    private User(long id) {
        this.id = id;
    }

    public static User getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new User(id));
        }

        return CACHE.get(id);
    }

    public void inflate() {
        if(this.id == UNINITIALIZED) {
            throw new AssertionError("Attempting to inflate uninitialized Model!");
        }

        if(!inflated) {
            AndrestClient client = new AndrestClient();
            JSONObject response = client.get(RestRouter.getUser(id));
            initFromJson(response);
            inflated = true;
        }
    }

    User initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            name = json.getString("name");
            avatar = null;
            List<Long> groupIds = JsonUtils.toListOfLong(json.optJSONArray("groups"));
            groups = new ArrayList<>(groupIds.size());
            for(Long groupId : groupIds) {
                groups.add(Group.getOrStub(groupId));
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

    public Drawable getAvatar() {
        return avatar;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public static User getDeviceUser() {
        return null;
    }

}

package edu.ucsd.studentpoll.models;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
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
public class User extends Model {

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            Long id = source.readLong();
            return User.getOrStub(id);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private static final String TAG = "User";
    private static final Map<Long, User> CACHE = new HashMap<>();

    private String name;

    private Drawable avatar;

    private List<Group> groups;

    private User() {
    }

    private User(Long id) {
        super(id);
    }

    public static User getOrStub(Long id) {
        if(!CACHE.containsKey(id)) {
            CACHE.put(id, new User(id));
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
            JSONObject response = client.get(RestRouter.getUser(id));
            initFromJson(response);
            inflated = true;
        }
    }

    @Override
    User initFromJson(JSONObject json) {
        try {
            id = json.getLong("id");
            name = json.getString("name");
            avatar = null;
            List<Long> groupIds = JsonUtils.ripIdList(json.optJSONArray("groups"));
            groups = new ArrayList<>(groupIds.size());
            for(Long groupId : groupIds) {
                groups.add(Group.getOrStub(groupId));
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

    public Drawable getAvatar() {
        return avatar;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public static User getDeviceUser() {
        return User.getOrStub(getDeviceUserId());
    }

    private static long getDeviceUserId() {
        return UNINITIALIZED;
    }

}

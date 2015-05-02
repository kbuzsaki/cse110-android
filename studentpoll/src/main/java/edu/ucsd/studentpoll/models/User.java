package edu.ucsd.studentpoll.models;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class User implements Model {

    private long id;

    private String name;

    private Drawable avatar;

    private List<Group> groups;

    public User() {

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

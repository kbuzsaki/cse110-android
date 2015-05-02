package edu.ucsd.studentpoll.rest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class JsonUtils {

    private JsonUtils() {

    }

    public static List<Long> toListOfLong(JSONArray array) throws JSONException {
        if(array == null) {
            return Collections.emptyList();
        }

        List<Long> list = new ArrayList<>(array.length());

        for(int i = 0; i < array.length(); i++) {
            list.add(array.getLong(i));
        }

        return list;
    }

    public static List<String> toListOfString(JSONArray array) throws JSONException {
        if(array == null) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>(array.length());

        for(int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }

        return list;
    }

}

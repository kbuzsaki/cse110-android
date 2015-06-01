package edu.ucsd.studentpoll.rest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class JsonUtils {

    private static final String TAG = "JsonUtils";

    private JsonUtils() {
    }

    public static List<Long> toListOfLong(JSONArray array) throws JSONException {
        Log.v(TAG, "Converting to list of longs: " + array);
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
        Log.v(TAG, "Converting to list of strings: " + array);
        if(array == null) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>(array.length());

        for(int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }

        return list;
    }

    public static Long ripId(Object object) throws JSONException {
        if(object instanceof Number) {
            return ((Number)object).longValue();
        }
        else if(object instanceof JSONObject) {
            JSONObject json = (JSONObject) object;
            return json.getLong("id");
        }
        else {
            throw new IllegalArgumentException("object is neither a long nor a json object: " + object);
        }
    }

    public static List<Long> ripIdList(JSONArray array) throws JSONException {
        if(array == null || array.length() == 0) {
            return Collections.emptyList();
        }

        List<Long> list = new ArrayList<>(array.length());

        for(int i = 0; i < array.length(); i++) {
            list.add(ripId(array.get(i)));
        }

        return list;
    }

    public static String encodeBitmap(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        String imageEncoded = Base64.encodeToString(bytes, Base64.DEFAULT);

        Log.v(TAG, "encoded image: " + imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeBitmap(String encoded) {
        if(encoded == null) {
            return null;
        }

        byte[] decodedByte = Base64.decode(encoded, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Map<String, Object> data;

        public Builder() {
            this.data = new HashMap<>();
        }

        public Builder put(String key, Object value) {
            if(value != null) {
                this.data.put(key, value);
            }
            else {
                this.data.remove(key);
            }

            return this;
        }

        public JSONObject build() {
            JSONObject json = new JSONObject(this.data);
            Log.d(TAG, "building json: " + json);
            return json;
        }

    }
}

package edu.ucsd.studentpoll.models;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public abstract class Model implements Parcelable {

    static final Long UNINITIALIZED = null;

    protected Long id;

    protected boolean inflated;

    protected Model() {
        this(UNINITIALIZED);
    }

    protected Model(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public abstract void inflate();

    public void refresh() {
        this.inflated = false;
        this.inflate();
    }

    abstract <M extends Model> M initFromJson(JSONObject json);

    abstract JSONObject toJson();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
    }

    @Override
    public String toString() {
        return "class=" + this.getClass() + ", id=" + getId() + ", inflated=" + inflated;
    }

    public static void inflateAll(Collection<? extends Model> models) {
        for(Model model : models) {
            model.inflate();
        }
    }

    public static void refreshAll(Collection<? extends Model> models) {
        for(Model model : models) {
            model.refresh();
        }
    }

    public static List<Long> mapIds(List<? extends Model> models) {
        if(models == null) {
            return Collections.emptyList();
        }

        List<Long> ids = new ArrayList<>(models.size());

        for(Model model : models) {
            ids.add(model.getId());
        }

        return ids;
    }

    public static List<JSONObject> mapJson(List<? extends Model> models) {
        if(models == null) {
            return Collections.emptyList();
        }

        List<JSONObject> jsons = new ArrayList<>(models.size());

        for(Model model : models) {
            jsons.add(model.toJson());
        }

        return jsons;
    }

}

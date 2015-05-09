package edu.ucsd.studentpoll.models;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public abstract class Model {

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

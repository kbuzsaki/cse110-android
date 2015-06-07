package edu.ucsd.studentpoll.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import edu.ucsd.studentpoll.rest.JsonUtils;
import edu.ucsd.studentpoll.rest.RESTException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public abstract class Model implements Parcelable {

    private static final String TAG = "Model";
    private static final int TIMEOUT_MINUTES = 1;

    static final Long UNINITIALIZED = null;

    protected Long id;

    protected boolean inflated;

    protected long timeRefreshed;

    protected Model() {
        this(UNINITIALIZED);
    }

    protected Model(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isInitialized() {
        return this.id != UNINITIALIZED;
    }

    protected void markRefreshed() {
        inflated = true;
        timeRefreshed = SystemClock.uptimeMillis();
    }

    protected boolean isOlderThan(long thresholdTime) {
        return timeRefreshed < thresholdTime;
    }

    public abstract void inflate();

    public void refresh() {
        this.inflated = false;
        this.inflate();
    }

    public void refreshIfOlder(long thresholdTime) {
        if (isOlderThan(thresholdTime)) {
            refresh();
        }
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

    public static void refreshAllIfOlder(Collection<? extends Model> models, final long thresholdTime) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<Future<?>> futures = new ArrayList<>();
        for(final Model model : models) {
            if(model.isOlderThan(thresholdTime)) {
                Future<?> future = executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        long start = SystemClock.uptimeMillis();
                        model.refreshIfOlder(thresholdTime);
                        long end = SystemClock.uptimeMillis();
                        long delta = end - start;
                        Log.v(TAG, "time for request: " + delta);
                    }
                });
                futures.add(future);
            }
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Log.e(TAG, "Failed to load!", e);
        }

        for(Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException|ExecutionException e) {
                Log.e(TAG, "Error while loading content", e);
                throw new RESTException(e);
            }
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

    public static <M extends Model> M ripModel(Object object, ModelInstantiator<M> instantiator) throws JSONException {
        if(object instanceof Number) {
            return instantiator.fromId(((Number)object).longValue());
        }
        else if(object instanceof JSONObject) {
            return instantiator.fromJson((JSONObject)object);
        }
        else {
            throw new IllegalArgumentException("object is neither a long nor a json object: " + object);
        }
    }

    public static <M extends Model> List<M> ripModelList(JSONArray array, ModelInstantiator<M> instantiator) throws JSONException {
        if(array == null || array.length() == 0) {
            return Collections.emptyList();
        }

        List<M> models = new ArrayList<>(array.length());

        for(int i = 0; i < array.length(); i++) {
            models.add(ripModel(array.get(i), instantiator));
        }

        return models;
    }

    public interface ModelInstantiator<M extends Model> {
        M fromId(Long id);
        M fromJson(JSONObject object) throws JSONException;
    }

}

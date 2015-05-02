package edu.ucsd.studentpoll.models;

import java.util.Collection;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public class ModelUtils {

    public static void inflateAll(Collection<? extends Model> models) {
        for(Model model : models) {
            model.inflate();
        }
    }

}

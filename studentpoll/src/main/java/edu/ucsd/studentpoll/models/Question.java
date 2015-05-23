package edu.ucsd.studentpoll.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kbuzsaki on 5/1/15.
 */
public abstract class Question extends Model {

    public static ModelInstantiator<Question> INSTANTIATOR = new ModelInstantiator<Question>() {
        @Override
        public Question fromId(Long id) {
            if(ChoiceQuestion.getOrStub(id).inflated) {
                return ChoiceQuestion.getOrStub(id);
            }
            else {
                throw new IllegalArgumentException("Ambiguous question id: " + id);
            }
        }

        @Override
        public Question fromJson(JSONObject object) throws JSONException {
            String type = object.getString("type");
            switch(type) {
                case "choice":
                    return ChoiceQuestion.INSTANTIATOR.fromJson(object);
                case "rank":
                    return RankQuestion.INSTANTIATOR.fromJson(object);
                case "schedule":
                default:
                    throw new IllegalArgumentException("Unknown question type: " + type);
            }
        }
    };

    protected Question() {
    }

    protected Question(Long id) {
        super(id);
    }

    public abstract Poll getPoll();

    public abstract String getTitle();

    public abstract List<? extends Response> getResponses();

}

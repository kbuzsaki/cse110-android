package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.RankQuestion;
import edu.ucsd.studentpoll.models.RankResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class RankResultFragment extends ResultFragment {

    private static final String TAG = "RankResultFragment";

    private static final String SAVED_QUESTION = TAG + ".question";

    private LinearLayout resultList;

    private RankQuestion rankQuestion;

    private Map<String, Integer> results = Collections.emptyMap();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superView = super.onCreateView(inflater, container, savedInstanceState);

        resultList = (LinearLayout) inflater.inflate(R.layout.rank_result_content, getContentContainer(), false);
        getContentContainer().addView(resultList);

        return superView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVED_QUESTION, rankQuestion);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            rankQuestion = savedInstanceState.getParcelable(SAVED_QUESTION);
            results = RankResponse.aggregateResponses(rankQuestion.getResponses());
        }
    }

    @Override
    public Question getQuestion() {
        return rankQuestion;
    }

    @Override
    public void setQuestion(Question question) {
        if(question == null) {
            Log.w(TAG, "Setting a null question!");
        }
        else if(question instanceof RankQuestion) {
            this.rankQuestion = (RankQuestion) question;
            this.results = RankResponse.aggregateResponses(rankQuestion.getResponses());
        }
        else {
            throw new AssertionError("Question is not a choice question: " + question);
        }
    }

    private int getCountForOption(String option) {
        if(results.containsKey(option)) {
            return results.get(option);
        }
        else {
            return 0;
        }
    }

    public void refreshView() {
        if(rootView == null) {
            Log.w(TAG, "rootView null!");
            return;
        }
        if(getActivity() == null) {
            Log.w(TAG, "detached fragment!");
            return;
        }
        if(rankQuestion == null) {
            Log.w(TAG, "attempting to render null fragment");
            return;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();

        this.results = RankResponse.aggregateResponses(rankQuestion.getResponses());

        TextView pollTitle = (TextView) rootView.findViewById(R.id.pollTitle);
        pollTitle.setText(rankQuestion.getTitle());

        List<String> orderedResults = new ArrayList<>(results.keySet());
        Collections.sort(orderedResults, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return Integer.compare(results.get(lhs), results.get(rhs));
            }
        });

        resultList.removeAllViews();
        for(String choice : orderedResults) {
            LinearLayout option = (LinearLayout) inflater.inflate(R.layout.rank_result_option, null, false);

            TextView choiceText = (TextView) option.findViewById(R.id.voteOption);
            TextView choiceCounter = (TextView) option.findViewById(R.id.voteCount);

            choiceText.setText(choice);
            choiceCounter.setText("" + getCountForOption(choice));

            resultList.addView(option);
        }
    }
}

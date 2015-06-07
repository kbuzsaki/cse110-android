package edu.ucsd.studentpoll;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.RankQuestion;
import edu.ucsd.studentpoll.models.RankResponse;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.view.DragSortRecycler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kdhuynh on 5/1/15.
 */
public class RankResponseFragment extends ResponseFragment {

    private static final String TAG = "RankResponseFragment";

    private static final String SAVED_QUESTION = TAG + ".question";

    private View hiddenView;

    private RecyclerView optionsView;

    private RankOptionAdapter rankOptionAdapter;

    private DragSortRecycler dragSortRecycler;

    private RankQuestion rankQuestion;

    private RankResponse latestResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superView = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.rank_response_content, getContentContainer(), false);

        hiddenView = layout.findViewById(R.id.hiddenView);

        optionsView = (RecyclerView) layout.findViewById(R.id.rank_content_view);
        getContentContainer().addView(layout);

        rankOptionAdapter = new RankOptionAdapter(getActivity());
        optionsView.setAdapter(rankOptionAdapter);
        optionsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        optionsView.setItemAnimator(null);

        dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.rankOptionElement); //View you wish to use as the handle

        dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                String fromOption = rankOptionAdapter.options.remove(from);
                rankOptionAdapter.options.add(to, fromOption);
                updateResponse(rankOptionAdapter.options);
                rankOptionAdapter.notifyDataSetChanged();
            }
        });

        optionsView.addItemDecoration(dragSortRecycler);
        optionsView.addOnItemTouchListener(dragSortRecycler);
        optionsView.setOnScrollListener(dragSortRecycler.getScrollListener());

        hiddenView.requestFocus();

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
        }
        else {
            throw new IllegalArgumentException("Question is not a choice question: " + question);
        }
    }

    @Override
    public void refreshView() {
        ((TextView)rootView.findViewById(R.id.pollTitle)).setText(rankQuestion.getTitle());
        rankOptionAdapter.setOptions(rankQuestion.getOptions());
    }

    private class RankOptionAdapter extends RecyclerView.Adapter<RankOptionViewHolder> {

        private final Context context;

        private List<String> options;

        public RankOptionAdapter(Context context) {
            this.context = context;
            this.options = Collections.emptyList();
        }

        public void setOptions(List<String> options) {
            this.options = options;
            this.notifyDataSetChanged();
        }

        @Override
        public RankOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView optionCard = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rank_response_option, parent, false);
            RankOptionViewHolder viewHolder = new RankOptionViewHolder(context, optionCard);
            // you don't have to set the content here before you return the viewHolder
            // onBindViewHolder will get called next and set the content for us
            // so just make and return an empty view
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RankOptionViewHolder holder, int position) {
            holder.setContent(options.get(position), position);
        }

        @Override
        public int getItemCount() {
            return options.size();
        }
    }

    private class RankOptionViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        private CardView pollCard;

        public RankOptionViewHolder(Context context, CardView optionCard) {
            super(optionCard);
            this.context = context;
            this.pollCard = optionCard;
        }

        public void setContent(String option, int position) {
            ((TextView)pollCard.findViewById(R.id.optionIndex)).setText("" + (position + 1));
            ((TextView)pollCard.findViewById(R.id.optionName)).setText(option);
        }
    }

    private void updateResponse(final List<String> choices) {
        Log.d(TAG, "Updating response: " + choices);

        new AsyncTask<Object, Object, RankResponse>() {
            @Override
            protected RankResponse doInBackground(Object[] params) {
                try {
                    return RankResponse.putResponse(rankQuestion, choices);
                }
                catch(RESTException e) {
                    Log.e(TAG, "Failed to send response", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(RankResponse rankResponse) {
                super.onPostExecute(rankResponse);

                if(rankResponse == null) {
                    Toast.makeText(getActivity(), "Failed to send vote.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        latestResponse = rankResponse;
                        onPutResponseListener.onResponsePut(latestResponse);
                    }
                    catch (NullPointerException e) {
                        Log.e(TAG, "Failed to update response listener", e);
                    }
                }
            }
        }.execute();
    }

}

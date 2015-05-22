package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.ucsd.studentpoll.models.Group;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.view.ActionBarHider;
import edu.ucsd.studentpoll.view.RefreshRequestListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PollsFragment extends Fragment {

    private static final String TAG = "PollsFragment";
    private static final String SAVED_POLLS_KEY = "polls";
    private static final String SAVED_GROUP = TAG + ".group";

    private ViewGroup rootView;

    private RecyclerView pollsView;

    private PollsAdapter pollsAdapter;

    private Group group;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.polls_fragment, container, false);

        pollsView = (RecyclerView) rootView.findViewById(R.id.pollsView);
        pollsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pollsAdapter = new PollsAdapter(getActivity(), Collections.<Poll>emptyList());
        pollsView.setAdapter(pollsAdapter);

        pollsView.setOnScrollListener(new ActionBarHider(((ActionBarActivity) getActivity()).getSupportActionBar()));

        Activity parent = getActivity();
        if(parent instanceof RefreshRequestListener) {
            final RefreshRequestListener refreshRequestListener = (RefreshRequestListener) parent;
            final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pollsRefreshLayout);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshRequestListener.onRefreshRequested(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVED_GROUP, group);

        ArrayList<Poll> polls = new ArrayList<>(pollsAdapter.polls);
        outState.putParcelableArrayList(SAVED_POLLS_KEY, polls);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            group = savedInstanceState.getParcelable(SAVED_GROUP);

            List<Poll> polls = savedInstanceState.getParcelableArrayList(SAVED_POLLS_KEY);
            pollsAdapter.setPolls(polls);
        }
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void updatePolls(List<Poll> polls) {
        pollsAdapter.setPolls(polls);
    }

    private static class PollsAdapter extends RecyclerView.Adapter<PollsViewHolder> {

        private final Context context;

        private List<Poll> polls;

        public PollsAdapter(Context context, List<Poll> polls) {
            this.context = context;
            this.polls = polls;
        }

        public void setPolls(List<Poll> polls) {
            this.polls = polls;
            this.notifyDataSetChanged();
        }

        @Override
        public PollsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView pollCard = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.poll_history_card, parent, false);
            PollsViewHolder viewHolder = new PollsViewHolder(context, pollCard);
            // you don't have to set the content here before you return the viewHolder
            // onBindViewHolder will get called next and set the content for us
            // so just make and return an empty view
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(PollsViewHolder holder, int position) {
            holder.setContent(polls.get(position));
        }

        @Override
        public int getItemCount() {
            return polls.size();
        }
    }

    private static class PollsViewHolder extends RecyclerView.ViewHolder {
        private Context context;

        private CardView pollCard;

        public PollsViewHolder(Context context, CardView pollCard) {
            super(pollCard);
            this.context = context;
            this.pollCard = pollCard;
        }

        public void setContent(final Poll poll) {
            ((TextView) pollCard.findViewById(R.id.title)).setText(poll.getName());
            String questionNames = TextUtils.join("\n", getQuestionTitles(poll));
            String timeText = randInt(2, 21) + " minutes ago";
            String voteText = poll.getQuestions().size() + " questions";
            ((TextView) pollCard.findViewById(R.id.cardContent)).setText(questionNames);
            ((TextView) pollCard.findViewById(R.id.time)).setText(timeText);
            ((TextView) pollCard.findViewById(R.id.votes)).setText(voteText);

            pollCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PollActivity.class);
                    intent.putExtra("poll", poll);
                    context.startActivity(intent);
                }
            });
        }

        private static List<String> getQuestionTitles(Poll poll) {
            List<String> titles = new ArrayList<>(poll.getQuestions().size());
            for(Question question : poll.getQuestions()) {
                titles.add(question.getTitle());
            }
            return titles;
        }
    }

    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }

}

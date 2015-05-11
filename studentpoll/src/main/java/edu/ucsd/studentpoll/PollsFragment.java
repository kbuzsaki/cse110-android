package edu.ucsd.studentpoll;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.Group;
import edu.ucsd.studentpoll.models.Model;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.view.ActionBarHider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PollsFragment extends Fragment {

    private static final String TAG = "PollsFragment";
    private static final String SAVED_POLLS_KEY = "polls";

    private ViewGroup rootView;

    private RecyclerView pollsView;

    private PollsAdapter pollsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.polls_fragment, container, false);

        pollsView = (RecyclerView) rootView.findViewById(R.id.pollsView);
        pollsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pollsAdapter = new PollsAdapter(getActivity(), Collections.<Poll>emptyList());
        pollsView.setAdapter(pollsAdapter);

        pollsView.setOnScrollListener(new ActionBarHider(((ActionBarActivity) getActivity()).getSupportActionBar()));

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pollsRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPolls();
                refreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(pollsAdapter.polls.isEmpty()) {
            refreshPolls();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Poll> polls = new ArrayList<>(pollsAdapter.polls);
        outState.putParcelableArrayList(SAVED_POLLS_KEY, polls);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            List<Poll> polls = savedInstanceState.getParcelableArrayList(SAVED_POLLS_KEY);
            pollsAdapter.setPolls(polls);
        }
    }

    public void refreshPolls() {
        new AsyncTask<Object, Object, List<Poll>>() {

            @Override
            protected List<Poll> doInBackground(Object... params) {
                try {
                    User user = User.getDeviceUser();
                    user.inflate();

                    Log.d(TAG, "Loading polls for user: " + user);

                    List<Group> groups = user.getGroups();
                    Model.inflateAll(groups);

                    List<Poll> polls = new ArrayList<>();
                    for(Group group : groups) {
                        polls.addAll(group.getPolls());
                    }

                    Model.refreshAll(polls);

                    for(Poll poll : polls) {
                        Model.refreshAll(poll.getQuestions());
                    }

                    return polls;
                }
                catch (RESTException e) {
                    Log.e(TAG, "Failed to reload polls", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Poll> polls) {
                if(polls == null) {
                    Toast.makeText(getActivity(), "Failed to load polls.", Toast.LENGTH_SHORT).show();
                }
                else {
                    pollsAdapter.setPolls(polls);
                }
            }
        }.execute();
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
            String timeText = randInt(2, 21) + " minutes ago";
            String voteText = randInt(0, 7) + "/" + randInt(7, 10) + " votes";
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
    }

    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }

}

package edu.ucsd.studentpoll;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.ucsd.studentpoll.models.Group;
import edu.ucsd.studentpoll.models.ModelUtils;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.view.ActionBarHider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PollsFragment extends Fragment {

    private ViewGroup rootView;

    private RecyclerView pollsView;

    private PollsAdapter pollsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.polls_fragment, container, false);

        pollsView = (RecyclerView) rootView.findViewById(R.id.pollsView);
        pollsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pollsAdapter = new PollsAdapter(Collections.<Poll>emptyList());
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

        refreshPolls();

        return rootView;
    }

    public void refreshPolls() {
        new AsyncTask<Object, Object, List<Poll>>() {

            @Override
            protected List<Poll> doInBackground(Object... params) {
                User user = User.getDeviceUser();
                user.inflate();

                List<Group> groups = user.getGroups();
                ModelUtils.inflateAll(groups);

                List<Poll> polls = new ArrayList<>();
                for(Group group : groups) {
                    polls.addAll(group.getPolls());
                }

                ModelUtils.inflateAll(polls);

                return polls;
            }

            @Override
            protected void onPostExecute(List<Poll> polls) {
                pollsAdapter.setPolls(polls);
            }
        }.execute();
    }

    private static class PollsAdapter extends RecyclerView.Adapter<PollsViewHolder> {

        private List<Poll> polls;

        public PollsAdapter(List<Poll> polls) {
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
            PollsViewHolder viewHolder = new PollsViewHolder(pollCard);
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

        private CardView pollCard;

        public PollsViewHolder(CardView pollCard) {
            super(pollCard);
            this.pollCard = pollCard;
        }

        public void setContent(Poll poll) {
            ((TextView) pollCard.findViewById(R.id.title)).setText(poll.getName());
            String timeText = randInt(2, 21) + " minutes ago";
            String voteText = randInt(0, 7) + "/" + randInt(7, 10) + " votes";
            ((TextView) pollCard.findViewById(R.id.time)).setText(timeText);
            ((TextView) pollCard.findViewById(R.id.votes)).setText(voteText);
        }
    }

    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }

}

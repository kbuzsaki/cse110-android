package edu.ucsd.studentpoll;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.view.ActionBarHider;

import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private ViewGroup rootView;

    private RecyclerView groupsView;

    private GroupsAdapter groupsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.home, container, false);

        groupsView = (RecyclerView) rootView.findViewById(R.id.groupsView);
        groupsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupsAdapter = new GroupsAdapter(Collections.<Group>emptyList());
        groupsView.setAdapter(groupsAdapter);

        groupsView.setOnScrollListener(new ActionBarHider(((ActionBarActivity) getActivity()).getSupportActionBar()));

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.groupsRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGroups();
                refreshLayout.setRefreshing(false);
            }
        });

        refreshGroups();

        return rootView;
    }

    public void refreshGroups() {
        new AsyncTask<Object, Object, List<Group>>() {

            @Override
            protected List<Group> doInBackground(Object[] params) {
                User user = User.getDeviceUser();
                user.inflate();

                List<Group> groups = user.getGroups();
                ModelUtils.inflateAll(groups);

                return groups;
            }

            @Override
            protected void onPostExecute(List<Group> groups) {
                groupsAdapter.setGroups(groups);
            }
        }.execute();
    }

    private static class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder> {

        private List<Group> groups;

        public GroupsAdapter(List<Group> groups) {
            this.groups = groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
            this.notifyDataSetChanged();
        }

        @Override
        public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView groupCard = (CardView) LayoutInflater.from(parent.getContext())
                                                          .inflate(R.layout.poll_history_card, parent, false);
            GroupsViewHolder viewHolder = new GroupsViewHolder(groupCard);
            // you don't have to set the content here before you return the viewHolder
            // onBindViewHolder will get called next and set the content for us
            // so just make and return an empty view
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(GroupsViewHolder holder, int position) {
            holder.setContent(groups.get(position));
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }
    }

    private static class GroupsViewHolder extends RecyclerView.ViewHolder {

        private CardView groupCard;

        public GroupsViewHolder(CardView groupCard) {
            super(groupCard);
            this.groupCard = groupCard;
        }

        public void setContent(Group group) {
            ((TextView)groupCard.findViewById(R.id.title)).setText(group.getName());
            String timeText = randInt(2, 21) + " minutes ago";
            String voteText = randInt(0, 7) + "/" + randInt(7, 10) + " votes";
            ((TextView)groupCard.findViewById(R.id.time)).setText(timeText);
            ((TextView)groupCard.findViewById(R.id.votes)).setText(voteText);
        }
    }

    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }
}

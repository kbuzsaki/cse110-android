package edu.ucsd.studentpoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import edu.ucsd.studentpoll.view.ActionBarHider;
import edu.ucsd.studentpoll.view.RefreshRequestListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String SAVED_GROUPS_KEY = "groups";

    private ViewGroup rootView;

    private RecyclerView groupsView;

    private GroupsAdapter groupsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.home, container, false);

        groupsView = (RecyclerView) rootView.findViewById(R.id.groupsView);
        groupsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupsAdapter = new GroupsAdapter(getActivity(), Collections.<Group>emptyList());
        groupsView.setAdapter(groupsAdapter);

        groupsView.setOnScrollListener(new ActionBarHider(((ActionBarActivity) getActivity()).getSupportActionBar()));

        Activity parent = getActivity();
        if(parent instanceof RefreshRequestListener) {
            final RefreshRequestListener refreshRequestListener = (RefreshRequestListener) parent;
            final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.groupsRefreshLayout);
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

        ArrayList<Group> groups = new ArrayList<>(groupsAdapter.groups);
        outState.putParcelableArrayList(SAVED_GROUPS_KEY, groups);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            List<Group> groups = savedInstanceState.getParcelableArrayList(SAVED_GROUPS_KEY);
            groupsAdapter.setGroups(groups);
        }
    }

    public void updateGroups(List<Group> groups) {
        groupsAdapter.setGroups(groups);
    }

    private static class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder> {

        private final Context context;

        private List<Group> groups;

        public GroupsAdapter(Context context, List<Group> groups) {
            this.context = context;
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
            GroupsViewHolder viewHolder = new GroupsViewHolder(context, groupCard);
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

        private Context context;

        private CardView groupCard;

        public GroupsViewHolder(Context context, CardView groupCard) {
            super(groupCard);
            this.context = context;
            this.groupCard = groupCard;
        }

        public void setContent(final Group group) {
            ((TextView)groupCard.findViewById(R.id.title)).setText(group.getName());
            String pollNames = TextUtils.join("\n", getPollNames(group));
            String timeText = randInt(2, 21) + " minutes ago";
            String voteText = group.getMembers().size() + " members";
            ((TextView)groupCard.findViewById(R.id.cardContent)).setText(pollNames);
            ((TextView)groupCard.findViewById(R.id.time)).setText(timeText);
            ((TextView)groupCard.findViewById(R.id.votes)).setText(voteText);

            groupCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, GroupActivity.class);
                    intent.putExtra("group", group);
                    context.startActivity(intent);
                }
            });
        }

        private static List<String> getPollNames(Group group) {
            List<String> titles = new ArrayList<>(group.getPolls().size());
            for(Poll poll : group.getPolls()) {
                titles.add(poll.getName());
            }
            return titles;
        }
    }

    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }
}

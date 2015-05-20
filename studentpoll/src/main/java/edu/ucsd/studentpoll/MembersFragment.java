package edu.ucsd.studentpoll;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.view.ActionBarHider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MembersFragment extends Fragment {

    private static final String TAG = "MembersFragment";
    private static final String SAVED_MEMBERS_KEY = TAG + ".members";
    private static final String SAVED_GROUP = TAG + ".group";

    private ViewGroup rootView;

    private RecyclerView membersView;

    private MembersAdapter membersAdapter;

    private Group group;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.members_fragment, container, false);

        membersView = (RecyclerView) rootView.findViewById(R.id.pollsView);
        membersView.setLayoutManager(new LinearLayoutManager(getActivity()));

        membersAdapter = new MembersAdapter(getActivity(), Collections.<User>emptyList());
        membersView.setAdapter(membersAdapter);

        membersView.setOnScrollListener(new ActionBarHider(((ActionBarActivity) getActivity()).getSupportActionBar()));

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pollsRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMembers();
                refreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(membersAdapter.members.isEmpty()) {
            inflateMembers();
            refreshMembers();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVED_GROUP, group);

        ArrayList<User> members = new ArrayList<>(membersAdapter.members);
        outState.putParcelableArrayList(SAVED_MEMBERS_KEY, members);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            group = savedInstanceState.getParcelable(SAVED_GROUP);

            List<User> members = savedInstanceState.getParcelableArrayList(SAVED_MEMBERS_KEY);
            membersAdapter.setMembers(members);
        }
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    private void inflateMembers() {
        loadMembers(false);
    }

    private void refreshMembers() {
        loadMembers(true);
    }

    private void loadMembers(final boolean forceRefresh) {
        final Group refreshGroup = group;

        if(forceRefresh && group != null && group.getMembers() != null) {
            membersAdapter.setMembers(group.getMembers());
        }

        new AsyncTask<Object, Object, List<User>>() {

            @Override
            protected List<User> doInBackground(Object... params) {
                try {
                    group.refresh();
                    Model.refreshAll(group.getMembers());
                    return group.getMembers();
                }
                catch (RESTException e) {
                    Log.e(TAG, "Failed to reload members", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<User> members) {
                if(members == null) {
                    Toast.makeText(getActivity(), "Failed to load members.", Toast.LENGTH_SHORT).show();
                }
                else {
                    membersAdapter.setMembers(members);
                }
            }
        }.execute();
    }

    private static class MembersAdapter extends RecyclerView.Adapter<MembersViewHolder> {

        private final Context context;

        private List<User> members;

        public MembersAdapter(Context context, List<User> members) {
            this.context = context;
            this.members = members;
        }

        public void setMembers(List<User> members) {
            this.members = members;
            this.notifyDataSetChanged();
        }

        @Override
        public MembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView memberCard = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.poll_history_card, parent, false);
            MembersViewHolder viewHolder = new MembersViewHolder(context, memberCard);
            // you don't have to set the content here before you return the viewHolder
            // onBindViewHolder will get called next and set the content for us
            // so just make and return an empty view
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MembersViewHolder holder, int position) {
            holder.setContent(members.get(position));
        }

        @Override
        public int getItemCount() {
            return members.size();
        }
    }

    private static class MembersViewHolder extends RecyclerView.ViewHolder {
        private Context context;

        private CardView memberCard;

        public MembersViewHolder(Context context, CardView memberCard) {
            super(memberCard);
            this.context = context;
            this.memberCard = memberCard;
        }

        public void setContent(final User member) {
            ((TextView) memberCard.findViewById(R.id.title)).setText(member.getName());
            ((TextView) memberCard.findViewById(R.id.cardContent)).setText("");
            ((TextView) memberCard.findViewById(R.id.time)).setText("");
            ((TextView) memberCard.findViewById(R.id.votes)).setText("");

            memberCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Lol no", Toast.LENGTH_SHORT);
                }
            });
        }
    }

    private static int randInt(int min, int max) {
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }

}

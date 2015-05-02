package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import edu.ucsd.studentpoll.models.Group;
import edu.ucsd.studentpoll.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Arrays;
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

        groupsView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean hideToolBar = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (hideToolBar) {
                    ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
                } else {
                    ((ActionBarActivity)getActivity()).getSupportActionBar().show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (dy > 20) {
                    hideToolBar = true;
                }
                else if (dy < -5) {
                    hideToolBar = false;
                }
            }
        });

        return rootView;
    }

    private static class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder> {

        private final List<Group> groups;

        public GroupsAdapter(List<Group> groups) {
            this.groups = groups;
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

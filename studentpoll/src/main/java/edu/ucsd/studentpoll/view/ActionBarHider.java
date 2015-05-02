package edu.ucsd.studentpoll.view;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;

/**
 * Created by kbuzsaki on 5/2/15.
 */
public class ActionBarHider extends RecyclerView.OnScrollListener {

    private ActionBar actionBar;

    private boolean hideToolBar = false;

    public ActionBarHider(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (hideToolBar) {
            actionBar.hide();
        } else {
            actionBar.show();
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
}

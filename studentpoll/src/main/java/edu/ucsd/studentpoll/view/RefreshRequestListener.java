package edu.ucsd.studentpoll.view;

/**
 * Created by kbuzsaki on 5/22/15.
 */
public interface RefreshRequestListener {

    void onRefreshRequested(Runnable refreshComplete);

}

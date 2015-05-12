package edu.ucsd.studentpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import edu.ucsd.studentpoll.models.Question;

/**
 * Created by kdhuynh on 5/1/15.
 */
public abstract class ResultFragment extends Fragment {

    protected ViewGroup rootView;

    private View.OnClickListener responseListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.result_fragment, container, false);

        Button seeResponseButton = (Button) rootView.findViewById(R.id.editResponseButton);
        seeResponseButton.setOnClickListener(responseListener);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    protected ViewGroup getContentContainer() {
        ViewGroup resultContentFrame = (ViewGroup) rootView.findViewById(R.id.resultContentFrame);
        return resultContentFrame;
    }

    public abstract Question getQuestion();

    public abstract void setQuestion(Question question);

    public abstract void refreshView();

    public void setSeeResponseListener(View.OnClickListener responseListener) {
        this.responseListener = responseListener;

        if(rootView != null) {
            Button seeResponseButton = (Button) rootView.findViewById(R.id.editResponseButton);
            seeResponseButton.setOnClickListener(responseListener);
        }
    }

}

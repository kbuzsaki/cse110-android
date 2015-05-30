package edu.ucsd.studentpoll.view;

import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by kbuzsaki on 5/29/15.
 */
public class NewlineInterceptor implements EditText.OnEditorActionListener {

    private static final String TAG = "NewlineInterceptor";

    OnInterceptListener listener;

    public NewlineInterceptor() {
        this(null);
    }

    public NewlineInterceptor(OnInterceptListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            Log.v(TAG, "Intercepted a newline!");
            if(listener != null) {
                listener.newlineIntercepted();
            }
            return true;
        }
        return false;
    }

    public interface OnInterceptListener {
        void newlineIntercepted();
    }

    public static void addInterceptor(EditText editText) {
        addInterceptor(editText, null);
    }

    public static void addInterceptor(EditText editText, OnInterceptListener listener) {
        int maskedIME = editText.getImeOptions() & (~EditorInfo.IME_MASK_ACTION);

        editText.setSingleLine();
        editText.setImeOptions(maskedIME | EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new NewlineInterceptor(listener));
    }
}

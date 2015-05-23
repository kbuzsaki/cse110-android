package edu.ucsd.studentpoll.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import edu.ucsd.studentpoll.R;

/**
 * Specific view to provide 'oscilllator' kind of animation using two input views
 */
public final class OscillatorAnimatedView extends RelativeLayout {

        /* Internal constants, mostly for default values */
    /** default oscillator interval */
    private static final int DEFAULT_INTERVAL = 700;
    /** default oscillator extend */
    private  static final float DEFAULT_EXTEND = 2.0f;

    /** Image to be displayed at the center */
    private ImageView mCenterImage = null;
    /** Image to oscillate */
    private ImageView mOscillatorImage = null;
    /** Oscillator animation */
    private AnimatorSet mAnimatorSet = null;

    public OscillatorAnimatedView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initAndCompose(attrs);
    }

    public OscillatorAnimatedView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        initAndCompose(attrs);
    }

    /**
     * Internal init function to init all additional data
     * and compose child for this ViewGroup
     *
     * @param attrs {@link AttributeSet} with data from xml attributes
     */
    private void initAndCompose(final AttributeSet attrs) {

        if (null == attrs) {
            throw new IllegalArgumentException("Attributes should be provided to this view," +
                    " at least centerImage and oscillatorImage should be specified");
        }

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.OscillatorAnimatedView, 0, 0);
        final Drawable centerDrawable = a.getDrawable(R.styleable.OscillatorAnimatedView_centerImage);
        final Drawable oscillatorDrawable = a.getDrawable(R.styleable.OscillatorAnimatedView_oscillatorImage);

        if (null == centerDrawable || null == oscillatorDrawable) {
            throw new IllegalArgumentException("Attributes should be provided to this view," +
                    " at least centerImage and oscillatorImage should be specified");
        }

        final int oscillatorInterval = a.getInt(R.styleable.OscillatorAnimatedView_oscillatorInterval, DEFAULT_INTERVAL);
        final float maxOscillatorExtend = a.getFloat(R.styleable.OscillatorAnimatedView_oscillatorMaxExtend, DEFAULT_EXTEND);

        a.recycle();

        // Create child and add them into this view group
        mCenterImage = new ImageView(getContext());
        mCenterImage.setImageDrawable(centerDrawable);
        addInternalChild(mCenterImage);

        mOscillatorImage = new ImageView(getContext());
        mOscillatorImage.setImageDrawable(oscillatorDrawable);
        addInternalChild(mOscillatorImage);

        mAnimatorSet = new AnimatorSet();

        mAnimatorSet.setDuration(oscillatorInterval);

        final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mOscillatorImage, "ScaleX", 1.0f, maxOscillatorExtend);

        scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mOscillatorImage, "ScaleY", 1.0f, maxOscillatorExtend);

        scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleYAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        mAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator);
    }

    private void addInternalChild(final ImageView child) {
        final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.addRule(CENTER_IN_PARENT, 1);
        addView(child, params);
    }

    public void start() {
        mAnimatorSet.start();
    }

    public void stop() {
        mAnimatorSet.end();
    }
}

package com.android.bignerdranch.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private View mReflectionView;
    private View mTopCloudView;
    private View mLeftCloudView;
    private View mRightCloudView;
    private View mTopReflectionView;
    private View mLeftReflectionView;
    private View mRightReflectionView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private  int mSunsetSkyColorCurrent;
    private int mNightSkyColorCurrent;

    private boolean mSunset;

    private float mSunYCurrent;
    private float mReflectionYCurrent;
    private float mTopCloudXCurrent;
    private float mLeftCloudXCurrent;
    private float mRightCloudXCurrent;
    private float mTopReflectionXCurrent;
    private float mLeftReflectionXCurrent;
    private float mRightReflectionXCurrent;
    private float mSkyWidth;


    private AnimatorSet mSunsetAnimatorSet;
    private AnimatorSet mSunriseAnimatorSet;
    private AnimatorSet mSunRotateAnimatorSet;

    public static final int DURATION = 3000;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = v;
        mSunView = v.findViewById(R.id.sun);
        mReflectionView = v.findViewById(R.id.reflection);
        mSkyView = v.findViewById(R.id.sky);
        mTopCloudView = v.findViewById(R.id.topCloud);
        mLeftCloudView = v.findViewById(R.id.leftCloud);
        mRightCloudView = v.findViewById(R.id.rightCloud);
        mTopReflectionView = v.findViewById(R.id.topReflection);
        mLeftReflectionView = v.findViewById(R.id.leftReflection);
        mRightReflectionView = v.findViewById(R.id.rightReflection);


        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);
        mSunsetSkyColorCurrent = mBlueSkyColor;
        mNightSkyColorCurrent = mSunsetSkyColor;

        mSunset = true;

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSunset) {
                    startSunsetAnimation();
                    startCloudsAnimation();
                    if (mSunriseAnimatorSet != null) {
                        mSunriseAnimatorSet.end();
                        mSunriseAnimatorSet = null;
                    }
                } else {
                    startSunriseAnimation();
                    startReverseCloudsAnimation();
                    if (mSunsetAnimatorSet != null) {
                        mSunsetAnimatorSet.end();
                        mSunsetAnimatorSet = null;
                    }
                }
                mSunset = !mSunset;
                startSunRotationAnimation();
            }
        });

        ViewTreeObserver observer = v.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSunYCurrent = mSunView.getTop();
                mReflectionYCurrent = mReflectionView.getTop();
                mTopCloudXCurrent = mTopCloudView.getLeft();
                mLeftCloudXCurrent = mLeftCloudView.getLeft();
                mRightCloudXCurrent = mRightCloudView.getLeft();
                mTopReflectionXCurrent = mTopReflectionView.getLeft();
                mLeftReflectionXCurrent = mLeftReflectionView.getLeft();
                mRightReflectionXCurrent = mRightReflectionView.getLeft();
            }
        });

        return v;
    }

    public void startSunsetAnimation() {

        long duration = (long) (DURATION / (mSkyView.getHeight() - mSunView.getTop()) *
                (mSkyView.getHeight() - mSunYCurrent));

        ObjectAnimator heightSunAnimator = ObjectAnimator.ofFloat(mSunView, "y",
                mSunYCurrent, mSkyView.getHeight()).setDuration(duration);

        heightSunAnimator.setInterpolator(new AccelerateInterpolator());

        heightSunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunYCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator heightReflectionAnimator = ObjectAnimator.ofFloat(mReflectionView,
                "y", mReflectionYCurrent, -mReflectionView.getHeight())
                .setDuration(duration);

        heightReflectionAnimator.setInterpolator(new AccelerateInterpolator());

        heightReflectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mReflectionYCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator.ofObject(mSkyView,
                "backgroundColor", new ArgbEvaluator(), mSunsetSkyColorCurrent,
                mSunsetSkyColor).setDuration(duration);

        sunsetSkyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetSkyColorCurrent = (int) animation.getAnimatedValue();
            }
        });

        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofObject(mSkyView,
                "backgroundColor", new ArgbEvaluator(), mNightSkyColorCurrent,
                mNightSkyColor).setDuration(DURATION);

        nightSkyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNightSkyColorCurrent = (int) animation.getAnimatedValue();
            }
        });


        mSunsetAnimatorSet = new AnimatorSet();
        mSunsetAnimatorSet
                .play(heightSunAnimator)
                .with(heightReflectionAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        mSunsetAnimatorSet.start();
    }

    private void startSunriseAnimation() {

        long duration = (long) (DURATION / (mSkyView.getHeight() - mSunView.getTop()) *
                (mSunYCurrent- mSunView.getTop()));
        long nightDuration = (long)(DURATION*((double)(mSunsetSkyColor-mNightSkyColorCurrent)/
                (double)(mSunsetSkyColor-mNightSkyColor)));

        ObjectAnimator heightSunAnimator = ObjectAnimator.ofFloat(mSunView, "y",
                mSunYCurrent, mSunView.getTop()).setDuration(duration);

        heightSunAnimator.setInterpolator(new AccelerateInterpolator());

        heightSunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunYCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator heightReflectionAnimator = ObjectAnimator.ofFloat(mReflectionView,
                "y", mReflectionYCurrent, mReflectionView.getTop())
                .setDuration(duration);

        heightReflectionAnimator.setInterpolator(new AccelerateInterpolator());

        heightReflectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mReflectionYCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator sunriseSkyAnimator = ObjectAnimator.ofInt(mSkyView,
                "backgroundColor", mSunsetSkyColorCurrent,
                mBlueSkyColor).setDuration(duration);

        sunriseSkyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetSkyColorCurrent = (int) animation.getAnimatedValue();
            }
        });

        sunriseSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofInt(mSkyView,
                "backgroundColor",  mNightSkyColorCurrent
                , mSunsetSkyColor).setDuration(nightDuration);

        nightSkyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNightSkyColorCurrent = (int) animation.getAnimatedValue();
            }
        });

        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        mSunriseAnimatorSet = new AnimatorSet();
        mSunriseAnimatorSet
                .play(heightSunAnimator)
                .with(heightReflectionAnimator)
                .with(sunriseSkyAnimator)
                .after(nightSkyAnimator);
        mSunriseAnimatorSet.start();
    }

    private void startSunRotationAnimation() {
        ObjectAnimator sunRotateAnimator = ObjectAnimator.ofFloat(mSunView, "rotation",
                0, 23).setDuration(500);

        sunRotateAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        sunRotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        mSunRotateAnimatorSet = new AnimatorSet();
        mSunRotateAnimatorSet.play(sunRotateAnimator);
        mSunRotateAnimatorSet.start();
    }

    private void startCloudsAnimation() {

        ObjectAnimator moveTopCloudAnimator = ObjectAnimator.ofFloat(mTopCloudView, "x",
                mTopCloudXCurrent, mSkyView.getWidth()).setDuration(DURATION/3*2);

        moveTopCloudAnimator.setInterpolator(new AccelerateInterpolator());

        moveTopCloudAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTopCloudXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveTopReflectionAnimator = ObjectAnimator.ofFloat(mTopReflectionView,
                "x", mTopReflectionXCurrent, mSkyView.getWidth())
                .setDuration(DURATION/3*2);

        moveTopReflectionAnimator.setInterpolator(new AccelerateInterpolator());

        moveTopReflectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTopReflectionXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveLeftCloudAnimator = ObjectAnimator.ofFloat(mLeftCloudView,
                "x", mLeftCloudXCurrent, mSkyView.getWidth()).setDuration(DURATION);

        moveLeftCloudAnimator.setInterpolator(new AccelerateInterpolator());

        moveLeftCloudAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeftCloudXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveLeftReflectionAnimator = ObjectAnimator.ofFloat(mLeftReflectionView,
                "x", mLeftReflectionXCurrent, mSkyView.getWidth())
                .setDuration(DURATION);

        moveLeftReflectionAnimator.setInterpolator(new AccelerateInterpolator());

        moveLeftReflectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeftReflectionXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveRightCloudAnimator = ObjectAnimator.ofFloat(mRightCloudView,
                "x", mRightCloudXCurrent, mSkyView.getWidth()).setDuration(DURATION/3);

        moveRightCloudAnimator.setInterpolator(new AccelerateInterpolator());

        moveRightCloudAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRightCloudXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveRightReflectionAnimator = ObjectAnimator.ofFloat(mRightReflectionView,
                "x", mRightReflectionXCurrent, mSkyView.getWidth())
                .setDuration(DURATION/3);

        moveRightReflectionAnimator.setInterpolator(new AccelerateInterpolator());

        moveRightReflectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRightReflectionXCurrent = (float) animation.getAnimatedValue();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(moveTopCloudAnimator)
                .with(moveLeftCloudAnimator)
                .with(moveRightCloudAnimator)
                .with(moveTopReflectionAnimator)
                .with(moveLeftReflectionAnimator)
                .with(moveRightReflectionAnimator);
        animatorSet.start();
    }

    private void startReverseCloudsAnimation() {
        mSkyWidth = mSkyView.getRight()/2;
        ObjectAnimator moveTopCloudAnimator = ObjectAnimator.ofFloat(mTopCloudView, "x",
                mTopCloudXCurrent,
                mSkyView.getRight()/2-mTopCloudView.getWidth()/2).setDuration(DURATION/3*2);

        moveTopCloudAnimator.setInterpolator(new AccelerateInterpolator());

        moveTopCloudAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTopCloudXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveTopReflectionAnimator = ObjectAnimator.ofFloat(mTopReflectionView,
                "x", mTopReflectionXCurrent,
                mSkyView.getRight()/2-mTopReflectionView.getWidth()/2).setDuration(DURATION/3*2);

        moveTopReflectionAnimator.setInterpolator(new AccelerateInterpolator());

        moveTopReflectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTopReflectionXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveLeftCloudAnimator = ObjectAnimator.ofFloat(mLeftCloudView,
                "x", mLeftCloudXCurrent, mSkyView.getLeft()).setDuration(DURATION/2);

        moveLeftCloudAnimator.setInterpolator(new AccelerateInterpolator());

        moveLeftCloudAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeftCloudXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveLeftReflectionAnimator = ObjectAnimator.ofFloat(mLeftReflectionView,
                "x", mLeftReflectionXCurrent, mSkyView.getLeft())
                .setDuration(DURATION/2);

        moveLeftReflectionAnimator.setInterpolator(new AccelerateInterpolator());

        moveLeftReflectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeftReflectionXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveRightCloudAnimator = ObjectAnimator.ofFloat(mRightCloudView,
                "x", mRightCloudXCurrent,
                mSkyView.getWidth()-mRightCloudView.getWidth()).setDuration(DURATION);

        moveRightCloudAnimator.setInterpolator(new AccelerateInterpolator());

        moveRightCloudAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRightCloudXCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator moveRightReflectionAnimator = ObjectAnimator.ofFloat(mRightReflectionView,
                "x", mRightReflectionXCurrent,
                mSkyView.getWidth()-mRightReflectionView.getWidth()).setDuration(DURATION);

        moveRightReflectionAnimator.setInterpolator(new AccelerateInterpolator());

        moveRightReflectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRightReflectionXCurrent = (float) animation.getAnimatedValue();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(moveTopCloudAnimator)
                .with(moveLeftCloudAnimator)
                .with(moveRightCloudAnimator)
                .with(moveTopReflectionAnimator)
                .with(moveLeftReflectionAnimator)
                .with(moveRightReflectionAnimator);
        animatorSet.start();
    }
}

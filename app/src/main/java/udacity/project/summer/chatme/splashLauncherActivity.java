package udacity.project.summer.chatme;

import android.animation.Animator;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class splashLauncherActivity extends AppCompatActivity {
    private static final String TAG = "splashLauncherActivity";
    private ImageView mChatMeLogoImageView ;
    private FrameLayout mGradientFrameLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_launcher);
        mChatMeLogoImageView = (ImageView) findViewById(R.id.chat_me_logo);
        mGradientFrameLayout = (FrameLayout) findViewById(R.id.gradient_layer);
        List<View> viewList = new ArrayList<>();
        viewList.add(mChatMeLogoImageView);
        viewList.add(mGradientFrameLayout);
        setEnterAnimation(viewList);

    }
    private void setEnterAnimation(List<View> viewList){
            Log.d(TAG, "setEnterAnimation: ");

            Animation enterAnimation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.slide_fade_in);
            enterAnimation.setDuration(1000);
            enterAnimation.setInterpolator(AnimationUtils.loadInterpolator(
                    this,
                    android.R.interpolator.linear_out_slow_in

            ));
            enterAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        for (View view:viewList) {
            view.startAnimation(enterAnimation);
        }

    }
}

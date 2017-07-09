package meduim.project.summer.chatme;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;



public class SplashFragment extends Fragment{
    private static final String TAG = "SplashFragment";
    private ImageView mChatMeLogoImageView ;
    private View mView ;


    public SplashFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mView= inflater.inflate(R.layout.fragment_splash,container,false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChatMeLogoImageView = (ImageView) mView.findViewById(R.id.chat_me_logo);
        setEnterAnimation(mChatMeLogoImageView);
    }


    /**
     * a method to set up and start the app launcher screen animation
     * @param view hold the view to be animated
     */
    private void setEnterAnimation(View view){

            //loading the animation from the resource folder
            Animation enterAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.slide_fade_in);
            enterAnimation.setDuration(1000);
            enterAnimation.setInterpolator(AnimationUtils.loadInterpolator(
                    getContext() ,
                    android.R.interpolator.linear_out_slow_in
            ));
        /**
         * setting the context activity as an animation listener since
         * it implements AnimationListener interface
         */
            enterAnimation.setAnimationListener((Animation.AnimationListener) getContext());
            view.startAnimation(enterAnimation);


    }
}

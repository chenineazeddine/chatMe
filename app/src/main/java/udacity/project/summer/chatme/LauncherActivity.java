package udacity.project.summer.chatme;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import udacity.project.summer.chatme.firebaseUtils.FirebaseUtils;

/**
 * Created by azeddine on 29/06/17.
 */

public class LauncherActivity extends AppCompatActivity implements Animation.AnimationListener{
    private static final String TAG = "LauncherActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_luncher);
        getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container,new SplashFragment()).commit();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.d(TAG, "onAnimationEnd: ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseUtils.isAuth()){
                    Toast.makeText(LauncherActivity.this, "YES", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LauncherActivity.this, "NO", Toast.LENGTH_SHORT).show();
                }
                AuthFragment authFragment = new AuthFragment();
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
                    Slide slide = new Slide(Gravity.BOTTOM);
                    slide.setDuration(350);
                    slide.setInterpolator(AnimationUtils.loadInterpolator(
                            getBaseContext(),
                            android.R.interpolator.linear_out_slow_in
                    ));
                    authFragment.setEnterTransition(slide);
                    authFragment.setSharedElementEnterTransition(TransitionInflater.from(getBaseContext()).inflateTransition(R.transition.move));
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,authFragment)
                        .addSharedElement(findViewById(R.id.chat_me_logo),getResources().getString(R.string.app_name))

                        .commit();
            }
        }, 750);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void handleLogIn(String email,String Password){
        FirebaseUtils.mAuth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LauncherActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LauncherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}

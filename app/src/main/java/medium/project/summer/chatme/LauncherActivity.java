package medium.project.summer.chatme;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;


import medium.project.summer.chatme.firebaseUtils.FirebaseUtils;

/**
 * Created by azeddine on 29/06/17.
 */

public class LauncherActivity extends AppCompatActivity implements Animation.AnimationListener{
    private static final String TAG = "LauncherActivity";
    private View mProgressBarContainer;
    private View mEmailPasswordAuthContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container,new SplashFragment())
                .commit();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseUtils.isAuth()){
                    startChatActivity();
                }else {
                    AuthFragment authFragment = new AuthFragment();

                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
                        /**
                         activity or fragment entry transition and shared elements
                         is available only on API 21 and higher
                         **/

                        Slide slide = new Slide(Gravity.BOTTOM);
                        slide.setDuration(350);
                        slide.setInterpolator(AnimationUtils.loadInterpolator(
                                getBaseContext(),
                                android.R.interpolator.linear_out_slow_in
                        ));
                        authFragment.setEnterTransition(slide);

                        /**
                         * inflating the shared element transition from the resource folder
                         * **/
                        authFragment.setSharedElementEnterTransition(TransitionInflater.from(getBaseContext()).inflateTransition(R.transition.move));
                    }
                    /**
                     * starting the auth fragment calling the support fragment Manger,
                     *  with specifying the views that we want to be shared
                     *  **/
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container,authFragment)
                            .addSharedElement(findViewById(R.id.chat_me_logo),getResources().getString(R.string.app_name))
                            .commit();
                }

            }
        },
                750);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    /**
     * a method used to start the chat activity
     */
    private void startChatActivity(){
        Intent intent =new Intent(LauncherActivity.this,ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    /**
     * a public method to handel the user sign in event from the auth fragment
     *
     * @param email a string holds the user email address
     * @param password a string holds the user text password
     * @param view a view holds the auth widgets to hide
     */
    public void handleSignIn(String email, String password, final View view){
        showProgress(true,view);
        FirebaseUtils.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startChatActivity();
                        }else{
                            // displaying an error message toast
                            Toast.makeText(LauncherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false,view);
                        }
                    }
                }
        );
    }
    /**
     * a public method to handel the user sign up event from the auth fragment
     *
     * @param email a string holds the user email address
     * @param password a string holds the user text password
     * @param view a view holds the auth widgets to hide
     *
     *
     */
    public void handleSignUp(String email, String password, final View view){
        showProgress(true,view);
        FirebaseUtils.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent =new Intent(LauncherActivity.this,initProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }else{
                            showProgress(false,view);
                            Toast.makeText(LauncherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }


    /**
     * a public method to handel the facebook authentication
     *
     * @param token holds the user Facebook accessToken
     */
    public void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseUtils.auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential: success");
                            startChatActivity();
                        } else {
                            Toast.makeText(LauncherActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    /**
     * a method to handle the state of the auth widgets, it displays a progress bar while the
     * firebase authentication network process
     * @param state if true the progress bar will be shown
     * @param view  holds the auth widget to hide or dispaly
     */
    private void showProgress(boolean state,View view){
        mProgressBarContainer =view.findViewById(R.id.progress_bar_container);
        mEmailPasswordAuthContainer = view.findViewById(R.id.email_password_auth_container);
        if(state) {
            mProgressBarContainer.setVisibility(View.VISIBLE);
            mEmailPasswordAuthContainer.setVisibility(View.GONE) ;
        } else {
            mProgressBarContainer.setVisibility(View.GONE) ;
            mEmailPasswordAuthContainer.setVisibility(View.VISIBLE);
        }
    }


}

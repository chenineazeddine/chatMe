package udacity.project.summer.chatme;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

import udacity.project.summer.chatme.firebaseUtils.FirebaseUtils;

/**
 * Created by azeddine on 29/06/17.
 */

public class LauncherActivity extends AppCompatActivity implements Animation.AnimationListener{
    private static final String TAG = "LauncherActivity";
    public static final int REQUEST_CODE_GOOGLE_AUTH = 2017;
    private FrameLayout progressBarContainer;
    private GoogleSignInOptions gso;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_luncher);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        progressBarContainer = (FrameLayout) findViewById(R.id.progress_bar_container);
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

    public void handleSignIn(String email,String Password){
        showProgress(true);
        FirebaseUtils.auth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgress(false);
                        if (task.isSuccessful()){
                            // going to the profile
                        }else{
                            Toast.makeText(LauncherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    public void handleSignUp(String email,String password){
        showProgress(true);
        FirebaseUtils.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgress(false);
                        if (task.isSuccessful()){
                            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(LauncherActivity.this,null).toBundle();
                            startActivity(new Intent(LauncherActivity.this,initProfileActivity.class),bundle);
                        }else{
                            Toast.makeText(LauncherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void handleGmailAuth(){
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
       Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
       startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_AUTH);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE_AUTH) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseUtils.auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUtils.auth.getCurrentUser().sendEmailVerification();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success"+FirebaseUtils.auth.getCurrentUser().getPhotoUrl());

                            // going to the profile

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LauncherActivity.this,getResources().getString(R.string.auth_failed),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void showProgress(boolean state){
        if(state) progressBarContainer.setVisibility(View.VISIBLE); else progressBarContainer.setVisibility(View.GONE) ;
    }


}

package udacity.project.summer.chatme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class AuthFragment extends Fragment {
    private static final String TAG = "AuthFragment";
    private Button mSignInButton ;
    private Button mRegisterButton ;
    private TextInputLayout mEmailInputLayout ;
    private TextInputLayout mPasswordInputLayout  ;
    private EditText mEmailEditText ;
    private EditText mPasswordEditText;
    private LoginButton mFacebookLoginButton;
    private View mView ;
    private CallbackManager mCallbackManager ;
    public AuthFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_auth,container,false);
        return  mView ;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSignInButton = (Button) view.findViewById(R.id.sign_in_button);
        mRegisterButton = (Button) view.findViewById(R.id.register_button);
        mEmailInputLayout = (TextInputLayout) view.findViewById(R.id.email_edit_text_layout);
        mPasswordInputLayout = (TextInputLayout) view.findViewById(R.id.password_edit_text_layout);
        mEmailEditText = (EditText) view.findViewById(R.id.email_edit_text);
        mPasswordEditText = (EditText) view.findViewById(R.id.password_edit_text);
        mFacebookLoginButton = (LoginButton) view.findViewById(R.id.button_facebook_login);
        mFacebookLoginButton.setReadPermissions("email","public_profile");
        mFacebookLoginButton.setFragment(this);
        mCallbackManager = CallbackManager.Factory.create();



        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPasswordEditText.getText().toString();
                String email = mEmailEditText.getText().toString();
                boolean validEmail = isValidEmail(email);
                boolean validPassword = isValidPassword(password);


                if(validEmail && validPassword) {
                    if(getContext() instanceof LauncherActivity){
                        ((LauncherActivity)getContext()).handleSignIn(email,password);
                    }
                }else{
                    Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
                    mEmailInputLayout.setError(getContext().getResources().getString(R.string.email_error_mess));
                    mPasswordInputLayout.setError(getContext().getResources().getString(R.string.password_error_mess));
                }
                mEmailInputLayout.setErrorEnabled(validEmail);
                mPasswordInputLayout.setErrorEnabled(validPassword);

            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPasswordEditText.getText().toString();
                String email = mEmailEditText.getText().toString();
                boolean validEmail = isValidEmail(email);
                boolean validPassword = isValidPassword(password);


                if(validEmail && validPassword) {
                        ((LauncherActivity)getContext()).handleSignUp(email,password);
                }else{
                    mEmailInputLayout.setError(getContext().getResources().getString(R.string.email_error_mess));
                    mPasswordInputLayout.setError(getContext().getResources().getString(R.string.password_error_mess));
                }
                mEmailInputLayout.setErrorEnabled(validEmail);
                mPasswordInputLayout.setErrorEnabled(validPassword);
            }
        });

        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                ((LauncherActivity)getContext()).handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

    }
    private boolean isValidEmail(String email){
       return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password){
        return !password.isEmpty();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

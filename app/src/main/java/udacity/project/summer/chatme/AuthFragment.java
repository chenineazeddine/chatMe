package udacity.project.summer.chatme;

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
import android.widget.Toast;

public class AuthFragment extends Fragment {
    private static final String TAG = "AuthFragment";
    private Button mGetStartedButton ;
    private TextInputLayout mEmailInputLayout ;
    private TextInputLayout mPasswordInputLayout  ;
    private EditText mEmailEditText ;
    private EditText mPasswordEditText;
    private View mView ;
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
        mGetStartedButton = (Button) view.findViewById(R.id.get_started_button);
        mEmailInputLayout = (TextInputLayout) view.findViewById(R.id.email_edit_text_layout);
        mPasswordInputLayout = (TextInputLayout) view.findViewById(R.id.password_edit_text_layout);
        mEmailEditText = (EditText) view.findViewById(R.id.email_edit_text);
        mPasswordEditText = (EditText) view.findViewById(R.id.password_edit_text);



        mGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPasswordEditText.getText().toString();
                String email = mEmailEditText.getText().toString();
                boolean validEmail = isValidEmail(email);
                boolean validPassword = isValidPassword(password);


                if(validEmail && validPassword) {
                    if(getContext() instanceof LauncherActivity){
                        ((LauncherActivity)getContext()).handleLogIn(email,password);
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
    }
    private boolean isValidEmail(String email){
       return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password){
        return !password.isEmpty();
    }

}

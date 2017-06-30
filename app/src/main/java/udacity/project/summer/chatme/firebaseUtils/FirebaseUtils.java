package udacity.project.summer.chatme.firebaseUtils;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by azeddine on 30/06/17.
 */

public class FirebaseUtils {
    public static  FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static boolean isAuth(){
        return mAuth.getCurrentUser() != null ;
    }

}
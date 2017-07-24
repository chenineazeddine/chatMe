package medium.project.summer.chatme.firebaseUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by azeddine on 30/06/17.
 */

public class FirebaseUtils {
    public static final String PROFILE_PHOTO_ROOT_PATH= "users/profileImages/";
    public static final String PROFILE_PHOTO_FORMAT =".jpeg";

    public static final String DATABASE_USER_REF ="users/";

    public static  final FirebaseAuth auth = FirebaseAuth.getInstance();
    public static final FirebaseDatabase db = FirebaseDatabase.getInstance();

    /**
     * a method used to verify if the user is authenticated
     * @return true if the firebase user is authenticated
     */
    public static boolean isAuth(){
        return auth.getCurrentUser() != null ;
    }

    /**
     * a method to upload the profile image to the firebase storage
     * @param imageLocalPath the image path at the local file system
     * @return UploadTask of the uploaded image
     * @throws FileNotFoundException
     */
    public static UploadTask uploadProfileImage (String imageLocalPath) throws FileNotFoundException {

        String imageFirebasePath = PROFILE_PHOTO_ROOT_PATH + auth.getCurrentUser().getUid() + PROFILE_PHOTO_FORMAT;
        InputStream stream = new FileInputStream(new File(imageLocalPath));
        StorageReference fileStorageRef = FirebaseStorage.getInstance().getReference().child(imageFirebasePath);

        return  fileStorageRef.putStream(stream);

    }

    /**
     * a method used to get a the reference of the user in the firebase real time database
     * @return DatabaseReference of the authenticated user
     */
    public static DatabaseReference getCurrentUserDbRef(){
        return  db.getReference(DATABASE_USER_REF).child(auth.getCurrentUser().getUid());
    }
}

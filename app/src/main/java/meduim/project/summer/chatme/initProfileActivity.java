package meduim.project.summer.chatme;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import java.io.FileNotFoundException;


import meduim.project.summer.chatme.firebaseUtils.FirebaseUtils;

public class initProfileActivity extends AppCompatActivity implements ImportImageDialogFragment.ImportOnClickListener {

    private static final String TAG = "initProfileActivity";
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_FROM_GALLERY = 0;

    private FirebaseUser mFirebaseUser ;

    private TextInputLayout mUsernameLayout;
    private ImageView mProfileImage ;
    private EditText mUsernameEditText;
    private Button mGetStartedButton;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_profile);

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mUsernameEditText = (EditText) findViewById(R.id.username_edit_text);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.username_edit_text_layout);

        mGetStartedButton = (Button) findViewById(R.id.get_started_btn);
        mGetStartedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = mUsernameEditText.getText().toString();
                        if(username.isEmpty()){
                            mUsernameLayout.setErrorEnabled(true);
                            mUsernameLayout.setError(getResources().getString(R.string.username_error_mess));
                        }else{
                            // update the user's username in the firebase real time database
                            FirebaseUtils.getCurrentUserDbRef().child("username").setValue(username);
                            // start chatting
                            startActivity(new Intent(initProfileActivity.this,ChatActivity.class));
                        }
                    }
                }
        );
        // setting up the event listener for the ImageView long press event
        mProfileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // starting the dialog fragment
                ImportImageDialogFragment profilePictureDialog = new ImportImageDialogFragment();
                profilePictureDialog.show(getSupportFragmentManager(),ImportImageDialogFragment.TAG);
                return true;
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        // getting a reference to the firebase authenticated user
        mFirebaseUser = FirebaseUtils.auth.getCurrentUser();
        if(mFirebaseUser != null){
            // using Glide to display a rounded profile image
            Glide.with(this)
                    .load(R.drawable.avatar)
                    .apply(new RequestOptions().circleCrop())
                    .into(mProfileImage);
        }
    }

    /**
     * a method to respond to the click event at the fragment dialog
     * @param value the value resulting of the user click event on the dialog fragment items
     */
    @Override
    public void onClick(int value) {
        Intent intent;
        switch (value) {
            case ImportImageDialogFragment.TAKE_PHOTO:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
                break;
            case ImportImageDialogFragment.IMPORT_FROM_GALLERY:
                intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_FROM_GALLERY);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imageLocalPath;
        Cursor cursor;
        Log.d(TAG, "onActivityResult: ");
        if(resultCode == RESULT_OK){
            if(requestCode==REQUEST_IMAGE_CAPTURE ||  requestCode==REQUEST_IMAGE_FROM_GALLERY) {

                // a code block to get path of the taken or imported image
                Uri pickedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                cursor  = getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                imageLocalPath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                /**
                 *    using a try catch block since uploadProfileImage
                 *    because throws FileNotFoundException
                  */

                try {
                    FirebaseUtils.uploadProfileImage(imageLocalPath).addOnCompleteListener(
                            new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        // loading the profile photo from the firebase real time database
                                        FirebaseUtils.getCurrentUserDbRef().child("photoURL").addValueEventListener(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                      String profileImageUrl  = (String) dataSnapshot.getValue();
                                                        Glide.with(initProfileActivity.this)
                                                                .load(profileImageUrl)
                                                                .apply(new RequestOptions().circleCrop())
                                                                .into(mProfileImage);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                }
                                        );

                                    }else{
                                        Log.d(TAG, "onComplete: error while uploading");
                                        Toast.makeText(initProfileActivity.this, "error while uploading image", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                    );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }

    }


}

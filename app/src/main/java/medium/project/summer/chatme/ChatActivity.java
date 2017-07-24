package medium.project.summer.chatme;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;

import medium.project.summer.chatme.firebaseUtils.FirebaseUtils;

public class ChatActivity extends AppCompatActivity {
    private MessageInput mMessageInput ;
    private MessagesList mMessagesList ;
    private Toolbar mToolbar ;
    private TextView mUsernameTextView;
    private ImageView mUserImageView;
    private User mUser = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setBackgroundDrawableResource(R.mipmap.chat_background);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);

        mUsernameTextView = (TextView) findViewById(R.id.username_text);
        mUserImageView = (ImageView) findViewById(R.id.user_avatar);

         mUser.setName(getPreferences(MODE_PRIVATE).getString(initProfileActivity.USERNAME,null));

        mUser.setAvatar(getPreferences(MODE_PRIVATE).getString(initProfileActivity.PHOTO_URL,null));




        mMessageInput = (MessageInput) findViewById(R.id.message_input);
        mMessageInput.setInputListener(
                new MessageInput.InputListener() {
                    @Override
                    public boolean onSubmit(CharSequence input) {
                        Toast.makeText(ChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );

        mMessagesList = (MessagesList) findViewById(R.id.messages_list);
    }

    @Override
    protected void onStart() {
        FirebaseUtils.getCurrentUserDbRef().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = (String) dataSnapshot.child("username").getValue();
                        String photoURL =  (String) dataSnapshot.child("photoURL").getValue();
                        mUser.setName(username);
                        mUser.setAvatar(photoURL);
                        getPreferences(Context.MODE_PRIVATE)
                                .edit()
                                .putString(initProfileActivity.USERNAME,username)
                                .putString(initProfileActivity.PHOTO_URL,photoURL)
                                .apply();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       final int id  = item.getItemId();
        switch (id){
            case R.id.log_out_action:
                Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }
        return true;

    }

    public class User implements IUser{
        private String name;
        private final String ID = FirebaseUtils.auth.getCurrentUser().getUid();
        private String photoURL;

        public User(){

        }
        public User(String username, String photoURL) {
            this.name = username;
            this.photoURL = photoURL;
        }

        public void setName(String name) {

            this.name=  name;
            if(mUsernameTextView != null ) mUsernameTextView.setText(name);
        }

        public void setAvatar(String photoURL) {
            this.photoURL = photoURL;
            if(mUserImageView != null) {
                Glide.with(ChatActivity.this)
                        .load(photoURL)
                        .apply(new RequestOptions().circleCrop())
                        .into(mUserImageView);
                mUserImageView.setBackground(null);
            }
        }

        @Override
        public String getId() {
            return ID ;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getAvatar() {
            return photoURL ;
        }
    }
}

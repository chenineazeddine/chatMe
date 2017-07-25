package medium.project.summer.chatme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Date;

import medium.project.summer.chatme.firebaseUtils.FirebaseUtils;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private MessageInput mMessageInput;
    private MessagesList mMessagesList;
    private ImageLoader mImageLoader ;
    private MessagesListAdapter<Message> mMessagesAdapter;
    private Toolbar mToolbar;
    private TextView mUsernameTextView;
    private ImageView mUserImageView;
    private Message.User mUser = new Message.User(FirebaseUtils.auth.getCurrentUser().getUid());

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

        mUser.setName(getPreferences(MODE_PRIVATE).getString(initProfileActivity.USERNAME, null));
        if (mUsernameTextView != null) mUsernameTextView.setText(mUser.getName());

        mUser.setAvatar(getPreferences(MODE_PRIVATE).getString(initProfileActivity.PHOTO_URL, null));
        if (mUserImageView != null) {
            Glide.with(ChatActivity.this)
                    .load(mUser.getAvatar())
                    .apply(new RequestOptions().circleCrop())
                    .into(mUserImageView);
            mUserImageView.setBackground(null);
        }

        mMessageInput = (MessageInput) findViewById(R.id.message_input);
        mMessageInput.setInputListener(
                new MessageInput.InputListener() {
                    @Override
                    public boolean onSubmit(CharSequence input) {
                        DatabaseReference newMessage = FirebaseUtils.getMessagesDbRef().push();
                        Message message = new Message(newMessage.getKey(),input.toString(),mUser);
                        newMessage.setValue(message);
                        return true;
                    }
                }
        );
        mMessagesList = (MessagesList) findViewById(R.id.messages_list);
        mImageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(imageView);
            }
        };
        mMessagesAdapter = new MessagesListAdapter<>(mUser.getId(),mImageLoader);
       mMessagesAdapter.setLoadMoreListener(
              new MessagesListAdapter.OnLoadMoreListener(){
                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {

                        }
                }
       );
        mMessagesList.setAdapter(mMessagesAdapter);



    }

    @Override
    protected void onStart() {
        FirebaseUtils.getCurrentUserDbRef().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = (String) dataSnapshot.child("username").getValue();
                        String photoURL = (String) dataSnapshot.child("photoURL").getValue();

                        mUser.setName(username);
                        mUsernameTextView.setText(mUser.getName());

                        mUser.setAvatar(photoURL);
                        Glide.with(ChatActivity.this)
                                .load(mUser.getAvatar())
                                .apply(new RequestOptions().circleCrop())
                                .into(mUserImageView);
                        mUserImageView.setBackground(null);

                        getPreferences(Context.MODE_PRIVATE)
                                .edit()
                                .putString(initProfileActivity.USERNAME, username)
                                .putString(initProfileActivity.PHOTO_URL, photoURL)
                                .apply();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        FirebaseUtils.getMessagesDbRef().addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Message message = dataSnapshot.getValue(Message.class);
                        Log.d(TAG, "onChildAdded: "+message.getUser().getId()+" and "+ message.getUser().getName());
                        mMessagesAdapter.addToStart(message,true);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.log_out_action:
                FirebaseUtils.auth.signOut();
                startActivity(new Intent(ChatActivity.this,LauncherActivity.class));
                break;
            default:
                return false;
        }
        return true;

    }

    @IgnoreExtraProperties
    public static class Message implements IMessage {
        private String id;
        private String text ;
        private User user ;
        private long timeMillis ;


        public Message(){

        }
        public Message(String Id, String Text, User user, long timeMillis) {
            this.id = Id;
            this.text = Text;
            this.user = user;
            this.timeMillis = timeMillis;
        }
        public Message(String Id, String Text, User user) {
            this.id = Id;
            this.text = Text;
            this.user = user;
            this.timeMillis = System.currentTimeMillis();
        }

        public void setId(String Id) {
            this.id = Id;
        }
        
        public void setText(String Text) {
            this.text = Text;
        }

        public void setUser(User User) {
            this.user = User;
        }

        public void setTimeMillis(long timeMillis) {
            this.timeMillis = timeMillis;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getText() {
            return text ;
        }

        @Override
        public IUser getUser() {
            return user ;
        }

        public long getTimeMillis(){
            return timeMillis;
        }

        @Exclude
        @Override
        public Date getCreatedAt() {
            return new Date(timeMillis) ;
        }



        public static class User implements IUser {
            private String name;
            private String id ;
            private String avatar;

            public User() {

            }
            public  User(String id){
                this.id = id;
            }

            public User(String username, String avatar) {
                this.name = username;
                this.avatar = avatar;
                this.id = FirebaseUtils.auth.getCurrentUser().getUid();
            }
            public User(String username, String avatar,String id) {
                this.name = username;
                this.avatar = avatar;
                this.id = id;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;

            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getAvatar() {
                return avatar;
            }
        }
    }



}

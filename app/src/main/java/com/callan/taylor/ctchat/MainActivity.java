package com.callan.taylor.ctchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Callan Taylor
 *
 * 6/4/18
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText mRecipient;
    private EditText mMessageText;
    private TextView mUsernameDisplay;
    private String mUsername = null;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mMessagesChildEventListener;

    private ListView mMessageListView;
    private RecyclerView mMessageRV;
    private MessageAdapter mMessageAdapter;
    private MessagesRVAdapter mMessageRVAdapter;
    private List<Messages> mMessages;

    private static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecipient = (EditText) findViewById(R.id.send_to);
        mMessageText = (EditText) findViewById(R.id.messageEditText);
        mUsernameDisplay = (TextView) findViewById(R.id.current_user);
        //mMessageListView = (ListView) findViewById(R.id.message_list_view);
        mMessageRV = (RecyclerView) findViewById(R.id.rv_message_list_view);

        mMessageRV.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Initialize message ListView and its adapter
        mMessages = new ArrayList<>();
        // mMessageAdapter = new MessageAdapter(this, R.layout.message_list_item, messages);
        //mMessageListView.setAdapter(mMessageAdapter);

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        final List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    onSignOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sing in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        dettachDatabaseReadListener();
        removeMessagesFromView();
    }


    private void removeMessagesFromView() {
        for (int i = 0; i < mMessages.size(); i++) {
            mMessages.remove(i);
        }
        mMessageRVAdapter.notifyDataSetChanged();
        mMessageRVAdapter = new MessagesRVAdapter(this, mMessages);
        mMessageRV.setAdapter(mMessageRVAdapter);
    }


    public void onClickSendMessage(View view) {
        if (!(mMessageText.getText().toString().equals("") &&
                mRecipient.getText().toString().equals(""))) {
            Messages message = new Messages(mUsername, mRecipient.getText().toString(),
                    mMessageText.getText().toString(), false);
            mMessagesDatabaseReference.push().setValue(message);
            mMessageText.setText("");
        }
    }


    public void attachDatabaseReadListener() {
        if (mMessagesChildEventListener == null) {
            mMessagesChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages message = dataSnapshot.getValue(Messages.class);
                    try {
                        if (message.getTargetUser().equals(mUsername)) {
                            mMessages.add(message);
                            mMessageRVAdapter = new MessagesRVAdapter(MainActivity.this, mMessages);
                            mMessageRV.setAdapter(mMessageRVAdapter);
                        } else if (message.getMyName().equals(mUsername)) {
                            message.setSenderSelf(true);
                            mMessages.add(message);
                            mMessageRVAdapter = new MessagesRVAdapter(MainActivity.this, mMessages);
                            mMessageRV.setAdapter(mMessageRVAdapter);
                        }
                    } catch (NullPointerException e) {
                        Log.e("onChildAdded", "Null pointer Exception");
                    }
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };
            mMessagesDatabaseReference.addChildEventListener(mMessagesChildEventListener);
        }
    }


    public void dettachDatabaseReadListener() {
        if (mMessagesChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mMessagesChildEventListener);
            mMessagesChildEventListener = null;
        }
    }


    public void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
        mUsernameDisplay.setText(mUsername);
    }


    public void onSignOutCleanup() {
        mUsername = "";
        mRecipient.setText("");
        removeMessagesFromView();
        dettachDatabaseReadListener();
        mUsernameDisplay.setText("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

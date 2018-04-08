package com.callan.taylor.ctchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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

public class ContactsActivity extends AppCompatActivity {


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mContatcsDatabaseReference;
    private ChildEventListener mMessagesChildEventListener;
    private ChildEventListener mContatcsChildEventListener;
    private String mUsername;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGN_IN = 1;

    private RecyclerView mContactsRV;
    private ContactsRVAdapter mContactsRVAdapter;
    private List<String> mContatcs;


    /**
     *  Do all the authentication stuff and have its own read listener, update a recylcerview of
     *  contacts from a database reference named after the user. have the option to update the
     *  contacts manually. and have onclicklisteners on each contact which takes the user to an
     *  instance of main activity wherre the messages are only shown if recieved from that specific
     *  contact. Pehraps have the recyclerview have 'unread messages' boolean which is set to true
     *  when the database reference reads that a message has been added by that user, and resets to
     *  false in th onclick method.
     *
     *  Also add notifications. Be careful each step of the way not to add bugs, or introduce
     *  unnessasary complexity
     *
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();


        mContactsRV = (RecyclerView) findViewById(R.id.rv_contacts_list_view);
        mContactsRV.setLayoutManager(new LinearLayoutManager(this));

        mContatcs = new ArrayList<>();

        final List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

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

    public void onSignedInInitialize(String username) {
        mUsername = username;
        mContatcsDatabaseReference = mFirebaseDatabase.getReference().child(mUsername);
        attachMessagesDatabaseReadListener();
        attatchContactsDatabaseRaedListener();
    }

    public void onSignOutCleanup() {
        dettachMessagesDatabaseReadListener();
        dettachContactsDatabaseReadListener();
        mUsername = "";
        mContatcs = null;
    }

    public void attachMessagesDatabaseReadListener() {
        if (mMessagesChildEventListener == null) {
            mMessagesChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    try {
                        if (messages.getTargetUser().equals(mUsername)) {
                            mContatcs.add(messages.getMyName());
                            mContatcsDatabaseReference.push().setValue(messages.getMyName());
                        }
                    } catch (NullPointerException e) {
                        Log.e("on reading message", "Null pointer");
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



    public void attatchContactsDatabaseRaedListener() {
        if (mContatcsChildEventListener == null) {
            mContatcsChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mContactsRVAdapter = new ContactsRVAdapter(ContactsActivity.this, mContatcs);
                    mContactsRV.setAdapter(mContactsRVAdapter);
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
            mContatcsDatabaseReference.addChildEventListener(mContatcsChildEventListener);
        }
    }

    public void dettachContactsDatabaseReadListener() {
        if (mContatcsChildEventListener != null) {
            mContatcsDatabaseReference.removeEventListener(mContatcsChildEventListener);
            mContatcsChildEventListener = null;
        }
    }


    public void dettachMessagesDatabaseReadListener() {
        if (mMessagesChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mMessagesChildEventListener);
            mMessagesChildEventListener = null;
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

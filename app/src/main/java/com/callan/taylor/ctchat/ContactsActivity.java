package com.callan.taylor.ctchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Provider;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ContactsActivity extends AppCompatActivity {


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mMyContatcsDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mMessagesChildEventListener;
    private ChildEventListener mMyContatcsChildEventListener;
    private ChildEventListener mUsersChildEventListener;
    private String mUsername;
    private String mEmail;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGN_IN = 1;

    private RecyclerView mContactsRV;
    private ContactsRVAdapter mContactsRVAdapter;
    private List<String> mContatcs;
    private List<String> mUsers;
    private EditText mSearchContacts;
    private TextView mSignedInAs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mSearchContacts = (EditText) findViewById(R.id.search_contacts);
        mSignedInAs = (TextView) findViewById(R.id.signed_in_as);

        mContactsRV = (RecyclerView) findViewById(R.id.rv_contacts_list_view);
        mContactsRV.setLayoutManager(new LinearLayoutManager(this));

        mContatcs = new ArrayList<>();
        mUsers = new ArrayList<>();

        final List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialize(user.getDisplayName(), String.valueOf(user.getEmail()));
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


    public void onSignedInInitialize(String username, String email) {
        mUsername = username;
        mEmail = email;
        mSignedInAs.setText("Logged in as " + mUsername);
        mMyContatcsDatabaseReference = mFirebaseDatabase.getReference().child(mUsername);
        attatchMyContactsDatabaseRaedListener();
        attachMessagesDatabaseReadListener();
        attatchUserDatabaseReadListener();
    }


    public void onSignOutCleanup() {
        mSignedInAs.setText("");
        dettachMessagesDatabaseReadListener();
        dettachMyContactsDatabaseReadListener();
        dettachUsersDatabaseReadListener();
        mUsers.clear();
        mUsername = "";
        mContatcs.clear();
    }


    public void attachMessagesDatabaseReadListener() {
        if (mMessagesChildEventListener == null) {
            mMessagesChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    try {
                        if (messages.getTargetUser().equals(mUsername)) {
                            String receivedFrom = messages.getMyName();
                            boolean userAlreadyInDatabase = false;
                            for (String contacts: mContatcs) {
                                if (receivedFrom.equals(contacts)) {
                                    userAlreadyInDatabase = true;
                                }
                            }
                            if (!userAlreadyInDatabase) {
                                mContatcs.add(receivedFrom);
                                mMyContatcsDatabaseReference.push().setValue(receivedFrom);
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
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


    public void attatchMyContactsDatabaseRaedListener() {
        if (mMyContatcsChildEventListener == null) {
            mMyContatcsChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String user = dataSnapshot.getValue().toString();
                    boolean allreadyInContacts = false;
                    for (String contact: mContatcs) {
                        if (contact.equals(user)) {
                            allreadyInContacts = true;
                        }
                    }
                    if (!allreadyInContacts) {
                        mContatcs.add(user);
                    }
                    mContactsRVAdapter = new ContactsRVAdapter(ContactsActivity.this, mContatcs);
                    mContactsRV.setAdapter(mContactsRVAdapter);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };
            mMyContatcsDatabaseReference.addChildEventListener(mMyContatcsChildEventListener);
        }
    }

    public void attatchUserDatabaseReadListener() {
        if (mUsersChildEventListener == null) {
            mUsersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String displayName = dataSnapshot.child("displayName").getValue(String.class);
                    String userUid = dataSnapshot.getKey();
                    if (email.equals(mEmail)) {
                        if (displayName.equals("empty")) {
                            DatabaseReference displayNameReference = mUsersDatabaseReference
                                    .child(userUid);
                            displayNameReference.child("displayName").setValue(mUsername);
                        }
                    }
                    mUsers.add(displayName);
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
            mUsersDatabaseReference.addChildEventListener(mUsersChildEventListener);
        }
    }


    public void dettachMyContactsDatabaseReadListener() {
        if (mMyContatcsChildEventListener != null) {
            mMyContatcsDatabaseReference.removeEventListener(mMyContatcsChildEventListener);
            mMyContatcsChildEventListener = null;
        }
    }


    public void dettachMessagesDatabaseReadListener() {
        if (mMessagesChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mMessagesChildEventListener);
            mMessagesChildEventListener = null;
        }
    }


    public void dettachUsersDatabaseReadListener() {
        if (mUsersChildEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mUsersChildEventListener);
            mUsersChildEventListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        dettachMessagesDatabaseReadListener();
        dettachMyContactsDatabaseReadListener();
        dettachUsersDatabaseReadListener();
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




    public void onClickAddContact(View view) {
        boolean foundInMyContacts = false;
        String searchForContact = mSearchContacts.getText().toString();
        if (!searchForContact.equals("")) {
            if (mUsers.contains(searchForContact)) {
                for (String contact : mContatcs) {
                    if (searchForContact.equals(contact)) {
                        foundInMyContacts = true;
                        onStartMainActivity(searchForContact);
                    }
                }
                if (!foundInMyContacts) {
                    mMyContatcsDatabaseReference.push().setValue(searchForContact);
                }
            } else {
                Toast.makeText(this, "User " + searchForContact +
                " is not a registered user", Toast.LENGTH_SHORT).show();
            }
        }
        mSearchContacts.setText("");
    }


    public void onStartMainActivity(String contactUsername) {
        Class destinationClass = MainActivity.class;

        Intent startChildActivityIntent = new Intent(this, destinationClass);

        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, contactUsername);

        startActivity(startChildActivityIntent);
    }
}

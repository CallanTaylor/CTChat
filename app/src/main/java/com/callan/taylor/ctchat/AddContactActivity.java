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
import android.widget.SearchView;
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

public class AddContactActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAllUsersDatabaseReference;
    private ChildEventListener mOnUserAdded;
    private List<String> mAllUsers;
    private List<String> mAllEmails;
    private List<String> mSuggestedUsers;
    private SearchView mSearchUsers;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private RecyclerView mAddContactsRV;
    private AddContactRVAdapter mAddContactsRVAdapter;

    private int RC_SIGN_IN = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAllUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        mAddContactsRV = (RecyclerView) findViewById(R.id.suggested_users);
        mAddContactsRV.setLayoutManager(new LinearLayoutManager(this));

        mAllUsers = new ArrayList<>();
        mAllEmails = new ArrayList<>();
        mSuggestedUsers = new ArrayList<>();

        attachAllUsersChildListener();


    }


    public void attachAllUsersChildListener() {
        if (mOnUserAdded == null) {
            mOnUserAdded = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String username = dataSnapshot.child("displayName").getValue(String.class);
                    mAllUsers.add(username);
                    Log.e("add user", "called");
                    mAddContactsRVAdapter = new AddContactRVAdapter(AddContactActivity.this, mAllUsers);
                    mAddContactsRV.setAdapter(mAddContactsRVAdapter);
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
            mAllUsersDatabaseReference.addChildEventListener(mOnUserAdded);
        }
    }
}

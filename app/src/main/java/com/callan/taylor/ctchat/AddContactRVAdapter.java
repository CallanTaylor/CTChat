package com.callan.taylor.ctchat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddContactRVAdapter extends RecyclerView.Adapter<AddContactRVAdapter.AllUserViewHolder> {


    private List<String> mUsers;
    private List<String> mCurrentContacts;
    private Context mContext;
    private String mMyName;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mContactsDatabaseReference;


    public AddContactRVAdapter(Context context, List<String> users, List<String> currentContacts, String myName) {
        mMyName = myName;
        mCurrentContacts = currentContacts;
        mContext = context;
        mUsers = users;
    }


    @NonNull
    @Override
    public AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.search_list_item, parent, false);
        AllUserViewHolder viewHolder = new AllUserViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllUserViewHolder holder, final int position) {

        if (holder != null) {

            holder.username.setText(mUsers.get(position));

            if (!mCurrentContacts.contains(mUsers.get(position))) {
                holder.alreadyAddedText.setVisibility(View.INVISIBLE);
                holder.alreadyAddedIcon.setVisibility(View.INVISIBLE);
            }

            holder.mLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Class destinationClass = MainActivity.class;

                    Intent startChildActivityIntent = new Intent(mContext, destinationClass);

                    if (mCurrentContacts.contains(mUsers.get(position))) {
                        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, mUsers.get(position));
                        mContext.startActivity(startChildActivityIntent);
                    } else {
                        mFirebaseDatabase = FirebaseDatabase.getInstance();
                        mContactsDatabaseReference = mFirebaseDatabase.getReference().child(mMyName);
                        mContactsDatabaseReference.push().setValue(mUsers.get(position));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class AllUserViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ConstraintLayout mLayout;
        TextView alreadyAddedIcon;
        TextView alreadyAddedText;

        public AllUserViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_displayName);
            mLayout = (ConstraintLayout) itemView.findViewById(R.id.user);
            alreadyAddedIcon = (TextView) itemView.findViewById(R.id.already_added_icon);
            alreadyAddedText = (TextView) itemView.findViewById(R.id.already_added_text);
        }
    }
}

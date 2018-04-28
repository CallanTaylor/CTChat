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

import java.util.List;

public class AddContactRVAdapter extends RecyclerView.Adapter<AddContactRVAdapter.AllUserViewHolder> {


    private List<String> mUsers;
    private Context mContext;

    public AddContactRVAdapter(Context context, List<String> users) {
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

            holder.mLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Class destinationClass = ContactsActivity.class;

                    Intent returnToParentActivity = new Intent(mContext, destinationClass);

                    if (mUsers.get(position) != null) {
                        returnToParentActivity.putExtra(Intent.EXTRA_TEXT, mUsers.get(position));
                    }
                    mContext.startActivity(returnToParentActivity);
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

        public AllUserViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_displayName);
            mLayout = (ConstraintLayout) itemView.findViewById(R.id.user);
        }
    }
}

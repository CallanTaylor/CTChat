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

public class ContactsRVAdapter extends RecyclerView.Adapter<ContactsRVAdapter.ContactsViewHolder> {

    private Context mContext;
    private List<String> mContacts;

    public ContactsRVAdapter(Context context, List<String> objects) {
        mContext = context;
        mContacts = objects;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.contacts_list_item, parent, false);
        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, final int position) {

        holder.mContactsItem.setText(mContacts.get(position));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class destinationClass = MainActivity.class;

                Intent startChildActivityIntent = new Intent(mContext, destinationClass);

                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, mContacts.get(position));

                mContext.startActivity(startChildActivityIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder  {

        TextView mContactsItem;
        ConstraintLayout linearLayout;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            mContactsItem = (TextView) itemView.findViewById(R.id.contact_text_view);
            linearLayout = (ConstraintLayout) itemView.findViewById(R.id.contact_item_layout);
        }
    }
}

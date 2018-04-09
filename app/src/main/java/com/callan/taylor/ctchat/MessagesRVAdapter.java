package com.callan.taylor.ctchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MessagesRVAdapter extends RecyclerView.Adapter<MessagesRVAdapter.MessageViewHolder> {

    private Context mContext;
    private List<Messages> messages;

    private static final int SENT_BY_SELF = 1;
    private static final int SENT_BY_OTHER = 2;

    public MessagesRVAdapter(Context context, List<Messages> objects) {
        this.mContext = context;
        this.messages = objects;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        MessageViewHolder viewHolder = null;

        switch (viewType) {
            case SENT_BY_OTHER:
                View viewOther = inflater.inflate(R.layout.message_list_item, parent, false);
                viewHolder = new MessageViewHolder(viewOther);
            break;
            case SENT_BY_SELF:
                View viewSelf = inflater.inflate(R.layout.message_list_item_self_sent, parent, false);
                viewHolder = new MessageViewHolder(viewSelf);
            break;
        }
        if (viewHolder == null) {
            Log.e("ViewHolder", "Null");
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        holder.mMessageText.setText(messages.get(position).getMessageText());
        holder.mSenderName.setText(String.valueOf(messages.get(position).getMyName().charAt(0)));

        if (position > 0) {


            if (messages.get(position).getSenderSelf()) {
                if (messages.get(position - 1).getSenderSelf()) {
                    holder.mSenderName.setVisibility(View.INVISIBLE);
                }
            }

            if (!messages.get(position).getSenderSelf()) {
                if (!messages.get(position - 1).getSenderSelf()) {
                    holder.mSenderName.setVisibility(View.INVISIBLE);
                }
            }

        }

    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderSelf()) {
            return SENT_BY_SELF;
        } else {
            return SENT_BY_OTHER;
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView mMessageText;
        TextView mSenderName;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mMessageText = (TextView) itemView.findViewById(R.id.message_text_view);
            mSenderName = (TextView) itemView.findViewById(R.id.sender);
        }
    }
}

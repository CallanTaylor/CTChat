package com.callan.taylor.ctchat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.callan.taylor.ctchat.Messages;
import com.callan.taylor.ctchat.R;


/** Entire class not currently being used
 *  This is here incase RecycerView doesn't work because i know listview will
 *

 <ListView
 android:id="@+id/message_list_view"
 android:layout_width="match_parent"
 android:layout_height="match_parent"
 android:layout_below="@id/linearLayoutTop"
 android:layout_above="@+id/linearLayout"
 android:stackFromBottom="true"
 android:divider="@android:color/transparent"
 android:transcriptMode="alwaysScroll"
 tools:listitem="@layout/message_list_item"/>

 */

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Messages> {


    public MessageAdapter(Context context, int resource, List<Messages> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Messages message = getItem(position);


        if (convertView == null) {
            if (message.getSenderSelf()) {
                convertView = ((Activity) getContext()).getLayoutInflater().
                        inflate(R.layout.message_list_item_self_sent, parent, false);
            } else {
                convertView = ((Activity) getContext()).getLayoutInflater().
                        inflate(R.layout.message_list_item, parent, false);
            }
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.message_text_view);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.sender);


        messageTextView.setVisibility(View.VISIBLE);
        String nameTag = null;
        try {
            messageTextView.setText(message.getMessageText());
            nameTag = String.valueOf(message.getMyName().charAt(0));
        } catch (NullPointerException e) {
            Log.e("MessageAdapter", "NullPointerException");
        }

        authorTextView.setText(nameTag);

        return convertView;
    }

}

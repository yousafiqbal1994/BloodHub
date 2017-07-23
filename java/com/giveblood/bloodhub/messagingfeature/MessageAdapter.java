package com.giveblood.bloodhub.messagingfeature;
import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.giveblood.bloodhub.R;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class MessageAdapter extends BaseAdapter {

    public static final String DIRECTION_INCOMING = "0";
    public static final String DIRECTION_OUTGOING = "1";
    private List<Pair<String, String>> messages;
    private LayoutInflater layoutInflater;

    public MessageAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<>();
    }

    public void addMessage(String message, String details) {
        messages.add(new Pair<String, String>(message, details));
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        String details = messages.get(i).second;
        String[] parts = details.split(",,,");
        return Integer.parseInt(parts[0]);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        int direction = getItemViewType(i);
        if (convertView == null) {
            int res = 0;
            if (direction == Integer.parseInt(DIRECTION_INCOMING)) {
                res = R.layout.message_right;
            } else if (direction == Integer.parseInt(DIRECTION_OUTGOING)) {
                res = R.layout.message_left;
            }
            convertView = layoutInflater.inflate(res, viewGroup, false);
        }

        String message = messages.get(i).first;
        String details = messages.get(i).second;
        String[] parts = details.split(",,,");
        String name = parts[1];
        String time = parts[2];

        EmojiconTextView txtMessage = (EmojiconTextView) convertView.findViewById(R.id.txtMessage);
        TextView txtSenderName = (TextView) convertView.findViewById(R.id.txtSenderName);
        TextView txtTime = (TextView) convertView.findViewById(R.id.txtDate);
        txtSenderName.setText(name);
        txtTime.setText(time);
        txtMessage.setText(message);
        return convertView;
    }

}


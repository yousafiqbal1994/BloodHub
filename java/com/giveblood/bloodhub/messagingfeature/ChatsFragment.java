package com.giveblood.bloodhub.messagingfeature;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.giveblood.bloodhub.R;
import com.giveblood.bloodhub.callfeature.CallDetails;
import com.giveblood.bloodhub.callfeature.MsgBaseActivity;
import com.giveblood.bloodhub.callfeature.SinchService;
import com.giveblood.bloodhub.loginsignup.LoginActivity;
import com.giveblood.bloodhub.searchfeature.EmptyPeopleList;
import com.giveblood.bloodhub.searchfeature.PeopleList;
import com.giveblood.bloodhub.userprofile.CustomCenterCrop;
import com.google.gson.Gson;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Ratan on 7/29/2015.
 */
public class ChatsFragment extends MsgBaseActivity{

    public ArrayList<ChatDetails> ChatPersons= new ArrayList<ChatDetails>();
    public ArrayList<ChatDetails> tempChatPersons= new ArrayList<ChatDetails>();
    public ArrayAdapter<ChatDetails> adapter;
    SharedPreferences prefs;
    public  Context context;
    ListView list;
    View v ;
    private BroadcastReceiver receiver = null;
    int counter =0;
    private ProgressDialog mSpinner;
    public String RECEIVER_NAME,Sender_NAME,ReceiverID,SenderID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.chats_layout,container,false);
        if (container == null) {
            return null;
        }
        registerBroadcastReceiver();
        list = (ListView) v.findViewById(R.id.ChatsListView);
        updateBadgeCounter();
        refreshListView();
        registerClickCallback();
        registerForContextMenu(list); // long clicks
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBadgeCounter();
        refreshListView();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("message"));
    }
    private void updateBadgeCounter() {
        int sum = 0;
        SharedPreferences messagesCounterPreference = getActivity().getSharedPreferences("MessagesCounter", MODE_PRIVATE);
        Map<String, ?> allEntries = messagesCounterPreference.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            int value = messagesCounterPreference.getInt(entry.getKey(),-1);
            sum = sum+value;
        }


    }
    private void refreshListView(){
        ChatPersons.clear();
        addAllthePeople(getActivity());
        adjustPeopleInOrder();
        adapter = new MyListAdapter();
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void registerBroadcastReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                if(success){
                    refreshListView();
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("message"));
    }

    private void registerClickCallback() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                final ChatDetails currentPerson = ChatPersons.get(position);
                SharedPreferences userProfile = getActivity().getSharedPreferences("ProfileDetails", MODE_PRIVATE);
                String IDofLoggedINUser = userProfile.getString("id","");
                getSinchServiceInterface().startClient(IDofLoggedINUser);
                showSpinner();
                RECEIVER_NAME= currentPerson.getRname();
                Sender_NAME=currentPerson.getSname();
                ReceiverID=currentPerson.getReceiverID();
                SenderID=currentPerson.getSenderID();
                showMessagesActivity();
            }

        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                final ChatDetails currentPerson = ChatPersons.get(info.position);
                ShowDilog(currentPerson);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private void showSpinner() {
        mSpinner = new ProgressDialog(getActivity());
        mSpinner.setTitle("Connecting");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
    @Override
    public void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void addPerson(ChatDetails person,String name,Context c) {
        Context con = c;
        prefs = PreferenceManager.getDefaultSharedPreferences(con);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(person);
        name = "mp"+name;
        prefsEditor.putString(name, json);
        prefsEditor.apply();
    }

    public void addAllthePeople(Context con) {
        prefs = PreferenceManager.getDefaultSharedPreferences(con);
        Gson gson = new Gson();
        context = con;
        Map<String, ?> allEntries = prefs.getAll();
        if(allEntries.size()>0){
            tempChatPersons.clear();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String jsonn = prefs.getString(entry.getKey(),"");
                String keyofCurrentObject = entry.getKey();
                ChatDetails obj = gson.fromJson(jsonn, ChatDetails.class);
                if(obj!=null){
                    String initials = keyofCurrentObject.charAt(0)+""+keyofCurrentObject.charAt(1);
                    if(initials.equals("mp")){
                        SharedPreferences chatter = con.getSharedPreferences("MostRecentChatter",MODE_PRIVATE);
                        String chatterID = chatter.getString("chatterID","");
                        if(chatterID.equals(keyofCurrentObject)){
                            ChatDetails chatDetails  =new ChatDetails(obj.sname,obj.senderID,obj.receiverID,obj.rname);
                            ChatPersons.add(chatDetails); ///// always 0th index
                        }else{
                            counter++;
                            ChatDetails chatDetails  =new ChatDetails(obj.sname,obj.senderID,obj.receiverID,obj.rname);
                            tempChatPersons.add(chatDetails);
                        }
                    }
                }
            }
        }
    }
    public void adjustPeopleInOrder(){
        for(int i = 0;i<counter;i++){
            ChatPersons.add(tempChatPersons.get(i));
        }
        counter =0;
    }

    private void showMessagesActivity() {
        Intent intent = new Intent(getActivity(), MessagingActivity.class);
        intent.putExtra("RECEIVER_NAME", RECEIVER_NAME);
        intent.putExtra("Sender_NAME", Sender_NAME);
        intent.putExtra("ReceiverID", ReceiverID);
        intent.putExtra("SenderID",SenderID);
        startActivity(intent);
    }

    public class MyListAdapter extends ArrayAdapter<ChatDetails> {

        MyListAdapter() {
            super(context, R.layout.chat_item_view, ChatPersons);
        }
        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {

            View itemView = null;
            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                itemView = vi.inflate(R.layout.chat_item_view, null);
            } else {
                itemView = convertView;
            }

            ViewHolder viewHolder = new ViewHolder();
            final ChatDetails currentPerson = ChatPersons.get((position));
            ///////////////////////////////////////
            int unreadCounter=0;
            SharedPreferences messagesCounterPreference = getActivity().getSharedPreferences("MessagesCounter", MODE_PRIVATE);
            Map<String, ?> allEntries = messagesCounterPreference.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if(currentPerson.getReceiverID().equals(entry.getKey())){
                    unreadCounter =  messagesCounterPreference.getInt(entry.getKey(),-1);
                }
            }

            viewHolder.unreadMsgsCounter =(TextView) itemView.findViewById(R.id.counter);
            if(unreadCounter>0){
                if(unreadCounter>=99){
                    viewHolder.unreadMsgsCounter.setText(99+"");
                }else {
                    viewHolder.unreadMsgsCounter.setText(unreadCounter+"");
                }
            }else{
                viewHolder.unreadMsgsCounter.setBackground(null);
            }
            ///////////////////////////////////////
            viewHolder.lastMsg = (EmojiconTextView) itemView.findViewById(R.id.lastMessage);
            SharedPreferences lastMessagePreference = getActivity().getSharedPreferences("LastMessage", MODE_PRIVATE);
            Map<String, ?> allMsgEntries = lastMessagePreference.getAll();

            for (Map.Entry<String, ?> entry : allMsgEntries.entrySet()) {
                if(currentPerson.getReceiverID().equals(entry.getKey())){
                    viewHolder.lastMsg.setText(entry.getValue().toString().trim());
                }
            }
            ///////////////////////////////////////////////
            viewHolder.Name = (TextView) itemView.findViewById(R.id.Name);
            viewHolder.Name.setText(currentPerson.getRname());
//            viewHolder.imageView.setText(getFirstLettersOfName(currentPerson.getRname()));
            String myImage = getActivity().getSharedPreferences("ProfileDetails", MODE_PRIVATE).getString("id","");
            String imageToBeLoaded = "";
            if(myImage.equals(currentPerson.getReceiverID())){
                imageToBeLoaded =  currentPerson.getSenderID()+".jpg";
            }else{
                imageToBeLoaded =  currentPerson.getReceiverID()+".jpg";
                Log.e("naika","Chats imageToBeLoaded is "+imageToBeLoaded);
            }
            viewHolder.DonorImage = (CircleImageView) itemView.findViewById(R.id.chatImage);
            String url = "http://faceblood.website/bhub/images/";

            Glide.with(getActivity()).load(url+imageToBeLoaded).asBitmap().placeholder(R.drawable.dp).override(60, 60)
                    .transform(new CenterCrop(getActivity()), new CustomCenterCrop(getActivity())).into(viewHolder.DonorImage);
            return itemView;
        }

    }
    public class ViewHolder {
        TextView Name;
        CircleImageView DonorImage;
        TextView unreadMsgsCounter;
        EmojiconTextView lastMsg;
    }

    private void ShowDilog(final ChatDetails person) {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Remove Chat")
                .setMessage("Are u sure you want to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteConversation(person.getSenderID()+person.getReceiverID(),getActivity(),person);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void DeleteConversation(String fname,Context c,ChatDetails person) {
        String FilePath = c.getFilesDir()+"/mh"+fname;
        File file = new File(FilePath);
        file.delete();
        deletePerson(person);
    }

    public  void deletePerson(ChatDetails person){
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().remove("mp"+person.getSenderID()+person.getReceiverID()).apply();
        adapter.remove(person);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}

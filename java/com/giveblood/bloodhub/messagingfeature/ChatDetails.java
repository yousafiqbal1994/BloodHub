package com.giveblood.bloodhub.messagingfeature;

/**
 * Created by YouCaf Iqbal on 8/28/2016.
 */
public class ChatDetails {

    public String sname;
    public String senderID;
    public String receiverID;
    public String rname;


    public ChatDetails(String sname,String senderID,String receiverID,String rname) {
        this.sname = sname;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.rname = rname;

    }

    public String getSenderID() {
        return senderID;
    }
    public String getReceiverID() {
        return receiverID;
    }
    public String getSname() {return sname;}
    public String getRname() {return rname;}



}

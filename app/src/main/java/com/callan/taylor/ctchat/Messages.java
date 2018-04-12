package com.callan.taylor.ctchat;

import android.support.annotation.NonNull;



public class Messages {

    private String myName;
    private String targetUser;
    private String messageText;
    private String timeSent;
    private boolean isSenderSelf;

    public Messages() {
    }

    public Messages(@NonNull String myName,@NonNull String targetUser,@NonNull String messageText,
                    @NonNull String timeSent, boolean isSenderSelf) {
        this.isSenderSelf = isSenderSelf;
        this.myName = myName;
        this.timeSent = timeSent;
        this.targetUser = targetUser;
        this.messageText = messageText;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getMyName() {
        return myName;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setSenderSelf(boolean isSenderSelf) {
        this.isSenderSelf = isSenderSelf;
    }

    public boolean getSenderSelf() {
        return isSenderSelf;
    }
}

package com.ethan.morephone.twilio.model;

import com.ethan.morephone.data.entity.message.MessageItem;
import com.ethan.morephone.utils.DateUtils;
import com.ethan.morephone.utils.TextUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by truongnguyen on 8/24/17.
 */
public class ConversationModel implements Comparable<ConversationModel> {

    private String mPhoneNumber;
    private String mDateCreated;
    private List<MessageItem> mMessageItems;

    public ConversationModel(String phoneNumber, String dateCreated, List<MessageItem> messageItems) {
        this.mPhoneNumber = phoneNumber;
        this.mDateCreated = dateCreated;
        this.mMessageItems = messageItems;

    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public List<MessageItem> getMessageItems() {
        return mMessageItems;
    }

    public void setMessageItems(List<MessageItem> messageItems) {
        this.mMessageItems = messageItems;
    }

    public String getDateCreated() {
        return mDateCreated;
    }

    public void setDateCreated(String mDateCreated) {
        this.mDateCreated = mDateCreated;
    }

    @Override
    public int compareTo(ConversationModel conversationModel) {
        if (!TextUtils.isEmpty(mDateCreated) && !TextUtils.isEmpty(conversationModel.getDateCreated())) {

            Date current = DateUtils.getDate(mDateCreated);
            Date now = DateUtils.getDate(conversationModel.getDateCreated());
            if (current.after(now)) {
                return -1;
            } else {
                return 1;
            }
        }

        return 0;

    }
}

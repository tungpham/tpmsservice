package com.ethan.morephone.twilio.model;

import com.ethan.morephone.data.entity.message.MessageItem;
import com.ethan.morephone.utils.DateUtils;
import com.ethan.morephone.utils.TextUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by truongnguyen on 8/24/17.
 */
public class ConversationModel implements Comparable<ConversationModel> {

    @JsonProperty("group_id")
    public String mGroupId;

    @JsonProperty("phone_number")
    public String mPhoneNumber;

    @JsonProperty("date_created")
    public String mDateCreated;

    @JsonProperty("message_items")
    public List<MessageItem> mMessageItems;

    public ConversationModel(String groupId, String phoneNumber, String dateCreated, List<MessageItem> messageItems) {
        this.mGroupId = groupId;
        this.mPhoneNumber = phoneNumber;
        this.mDateCreated = dateCreated;
        this.mMessageItems = messageItems;

    }

    @Override
    public int compareTo(ConversationModel conversationModel) {
        if (!TextUtils.isEmpty(mDateCreated) && !TextUtils.isEmpty(conversationModel.mDateCreated)) {

            Date current = DateUtils.getDate(mDateCreated);
            Date now = DateUtils.getDate(conversationModel.mDateCreated);
            if (current.after(now)) {
                return -1;
            } else {
                return 1;
            }
        }

        return 0;

    }
}

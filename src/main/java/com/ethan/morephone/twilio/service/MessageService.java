package com.ethan.morephone.twilio.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.usage.domain.UsageDTO;
import com.ethan.morephone.api.usage.service.UsageService;
import com.ethan.morephone.api.user.domain.UserDTO;
import com.ethan.morephone.api.user.service.UserService;
import com.ethan.morephone.data.entity.message.MessageItem;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.twilio.email.EmailServiceImpl;
import com.ethan.morephone.twilio.fcm.FCM;
import com.ethan.morephone.twilio.model.ConversationModel;
import com.ethan.morephone.utils.TextUtils;
import com.ethan.morephone.utils.Utils;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.exception.TwilioException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.api.v2010.account.MessageReader;
import com.twilio.twiml.Body;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by truongnguyen on 7/20/17.
 */
@RestController
@RequestMapping(value = "/api/v1/message")
public class MessageService {

    private final UserService mUserService;
    private final UsageService mUsageService;
    private final PhoneNumberService mPhoneNumberService;
    private final EmailServiceImpl mEmailService;

    @Autowired
    MessageService(UserService userService, PhoneNumberService phoneNumberService, UsageService usageService, EmailServiceImpl emailService) {
        this.mUserService = userService;
        this.mPhoneNumberService = phoneNumberService;
        this.mUsageService = usageService;
        this.mEmailService = emailService;
    }

    @RequestMapping(value = "/receive-message", method = RequestMethod.POST, produces = {"application/xml"})
    public String receiveMessage(@RequestParam Map<String, String> allRequestParams) {
        Utils.logMessage("Receive MultiValueMap: " + allRequestParams.toString());

        String response = "";

        String to = allRequestParams.get("To");
        String from = allRequestParams.get("From");
        String body = allRequestParams.get("Body");

        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(to);

        if (phoneNumberDTO != null) {

            String accountSid = allRequestParams.get("AccountSid");
            UserDTO user = mUserService.findByAccountSid(accountSid);
            if (user != null) {
                String token = user.getToken();
                Utils.logMessage("TOKEN: " + token);
                List<String> identities = new ArrayList<>();
                identities.add(user.getEmail());
                mUsageService.updateMessageIncoming(user.getId());

//                sendNotification("High", allRequestParams.get("To"), allRequestParams.get("From"), allRequestParams.get("Body"), identities);
//                sendNotification(token, allRequestParams.get("From") + "-" + allRequestParams.get("To"), allRequestParams.get("Body"));
                FCM.sendNotification(token, Constants.FCM_SERVER_KEY, from + "-" + to, body);
            }

            if (phoneNumberDTO.isForward()) {

                UsageDTO usageDTO = mUsageService.findByUserId(user.getId());
                if (usageDTO != null && usageDTO.getBalance() > Constants.PRICE_MESSAGE_OUTGOING) {

                    //Forward sms to other phone number
                    if (!TextUtils.isEmpty(phoneNumberDTO.getForwardPhoneNumber())) {

                        com.twilio.twiml.Message message = new com.twilio.twiml.Message.Builder()
                                .to(phoneNumberDTO.getForwardPhoneNumber())
                                .body(new Body(body))
                                .build();

                        MessagingResponse twiml = new MessagingResponse.Builder()
                                .message(message)
                                .build();

                        try {
                            mUsageService.updateMessageOutgoing(user.getId());
                            response = twiml.toXml();
                        } catch (TwiMLException e) {
                            e.printStackTrace();
                        }
                    }

                    //Forward sms to email
                    if (!TextUtils.isEmpty(phoneNumberDTO.getForwardEmail())) {
                        mEmailService.sendSimpleMessage(phoneNumberDTO.getForwardEmail(), from, body);
                    }

                } else {
                    FCM.sendNotification(user.getToken(), Constants.FCM_SERVER_KEY, HTTPStatus.MONEY.getReasonPhrase(), "");
                }
            }
        }
        return response;

    }

    @PostMapping(value = "/send-message")
    com.ethan.morephone.http.Response<Object> sendMessage(@RequestParam(value = "account_sid") String accountSid,
                                                          @RequestParam(value = "auth_token") String authToken,
                                                          @RequestParam(value = "userId") String userId,
                                                          @RequestParam(value = "from") String from,
                                                          @RequestParam(value = "to") String to,
                                                          @RequestParam(value = "body") String body) {

        if (TextUtils.isEmpty(userId)
                || TextUtils.isEmpty(accountSid)
                || TextUtils.isEmpty(authToken)) {
            return new com.ethan.morephone.http.Response<>(HTTPStatus.BAD_REQUEST.getReasonPhrase(), HTTPStatus.BAD_REQUEST);
        }

        UsageDTO usageDTO = mUsageService.findByUserId(userId);

        if (usageDTO == null) {
            return new com.ethan.morephone.http.Response<>(HTTPStatus.BAD_REQUEST.getReasonPhrase(), HTTPStatus.BAD_REQUEST);
        }

//        usageDTO.getBalance()
        if (usageDTO.getBalance() > Constants.PRICE_MESSAGE_OUTGOING) {

            TwilioRestClient client = new TwilioRestClient.Builder(accountSid, authToken).build();
            try {
                Message message = new MessageCreator(
                        new PhoneNumber(to),
                        new PhoneNumber(from),
                        body).create(client);

                MessageItem messageItem = new MessageItem(message.getSid(),
                        message.getDateCreated() == null ? "" : message.getDateCreated().toString(),
                        message.getDateUpdated() == null ? "" : message.getDateUpdated().toString(),
                        message.getDateSent() == null ? "" : message.getDateSent().toString(),
                        message.getAccountSid(),
                        message.getTo(),
                        message.getFrom() == null ? "" : message.getFrom().toString(),
                        message.getMessagingServiceSid(),
                        message.getBody(),
                        message.getStatus() == null ? "" : message.getStatus().name(),
                        message.getNumSegments(),
                        message.getNumMedia(),
                        message.getDirection() == null ? "" : message.getDirection().toString(),
                        message.getApiVersion(),
                        message.getPrice() == null ? "" : message.getPrice().toString(),
                        message.getPriceUnit() == null ? "" : message.getPriceUnit().toString(),
                        String.valueOf(message.getErrorCode()),
                        message.getErrorMessage(),
                        message.getUri(),
                        null
                );

                mUsageService.updateMessageOutgoing(userId);

                return new com.ethan.morephone.http.Response<>(messageItem, HTTPStatus.CREATED);
            } catch (TwilioException e) {
                Utils.logMessage("An exception occurred trying to send a message to {}, exception: {} " + to + " ||| " + e.getMessage());
            }
        } else {
            UserDTO userDTO = mUserService.findById(usageDTO.getUserId());
            if (userDTO != null) {
                FCM.sendNotification(userDTO.getToken(), Constants.FCM_SERVER_KEY, HTTPStatus.MONEY.getReasonPhrase(), "");
                Utils.logMessage("SEND NOTIFCATION SMS");
            }
            return new com.ethan.morephone.http.Response<>(HTTPStatus.MONEY.getReasonPhrase(), HTTPStatus.MONEY);
        }
        return new com.ethan.morephone.http.Response<>(HTTPStatus.NOT_ACCEPTABLE.getReasonPhrase(), HTTPStatus.NOT_ACCEPTABLE);
    }


    @GetMapping(value = "/retrieve")
    com.ethan.morephone.http.Response<Object> retrieveMessage(@RequestParam(value = "account_sid") String accountSid,
                                                              @RequestParam(value = "auth_token") String authToken,
                                                              @RequestParam(value = "phone_number") String phoneNumber) {
        Twilio.init(accountSid, authToken);

        HashMap<String, List<MessageItem>> mArrayMap = new HashMap<>();

        ResourceSet<com.twilio.rest.api.v2010.account.Message> messagesIncoming = new MessageReader(accountSid).setTo(new PhoneNumber(phoneNumber)).read();

        if (messagesIncoming != null) {
            mArrayMap.putAll(executeData(messagesIncoming, true));
        }

        ResourceSet<com.twilio.rest.api.v2010.account.Message> messagesOutgoing = new MessageReader(accountSid).setFrom(new PhoneNumber(phoneNumber)).read();

        if (messagesOutgoing != null) {
            mArrayMap.putAll(executeData(messagesOutgoing, false));
        }

        List<ConversationModel> mConversationModels = new ArrayList<>();
        for (Map.Entry entry : mArrayMap.entrySet()) {
            List<MessageItem> items = mArrayMap.get(entry.getKey());
            if (items != null && !items.isEmpty()) {
                Collections.sort(items);
                String dateCreated = items.get(items.size() - 1).dateCreated;
                Utils.logMessage("DATE CREATED: " + dateCreated);
                mConversationModels.add(new ConversationModel(entry.getKey().toString(), dateCreated, items));
            }
        }

        return new com.ethan.morephone.http.Response<>(mConversationModels, HTTPStatus.OK);
    }

    private static HashMap<String, List<MessageItem>> executeData(ResourceSet<Message> messageItems, boolean isComing) {
        HashMap<String, List<MessageItem>> mArrayMap = new HashMap<>();
        if (isComing) {
            for (com.twilio.rest.api.v2010.account.Message messageItem : messageItems) {
                if (messageItem.getStatus() != null && messageItem.getStatus() == com.twilio.rest.api.v2010.account.Message.Status.RECEIVED) {
                    if (mArrayMap.containsKey(messageItem.getFrom().toString())) {
                        mArrayMap.get(messageItem.getFrom().toString()).add(convertMessage(messageItem));
                    } else {
                        List<MessageItem> items = new ArrayList<>();
                        items.add(convertMessage(messageItem));
                        mArrayMap.put(messageItem.getFrom().toString(), items);
                    }
                }

            }
        } else {
            for (com.twilio.rest.api.v2010.account.Message messageItem : messageItems) {
                if (messageItem.getStatus() != null && messageItem.getStatus() == com.twilio.rest.api.v2010.account.Message.Status.DELIVERED) {
                    if (mArrayMap.containsKey(messageItem.getTo())) {
                        mArrayMap.get(messageItem.getTo()).add(convertMessage(messageItem));
                    } else {
                        List<MessageItem> items = new ArrayList<>();
                        items.add(convertMessage(messageItem));
                        mArrayMap.put(messageItem.getTo(), items);
                    }
                }
            }
        }
        return mArrayMap;
    }

    private static MessageItem convertMessage(com.twilio.rest.api.v2010.account.Message message) {
        return new MessageItem(message.getSid(),
                message.getDateCreated() == null ? "" : message.getDateCreated().toString(),
                message.getDateUpdated() == null ? "" : message.getDateUpdated().toString(),
                message.getDateSent() == null ? "" : message.getDateSent().toString(),
                message.getAccountSid(),
                message.getTo(),
                message.getFrom() == null ? "" : message.getFrom().toString(),
                message.getMessagingServiceSid(),
                message.getBody(),
                message.getStatus() == null ? "" : message.getStatus().name(),
                message.getNumSegments(),
                message.getNumMedia(),
                message.getDirection() == null ? "" : message.getDirection().toString(),
                message.getApiVersion(),
                message.getPrice() == null ? "" : message.getPrice().toString(),
                message.getPriceUnit() == null ? "" : message.getPriceUnit().toString(),
                String.valueOf(message.getErrorCode()),
                message.getErrorMessage(),
                message.getUri(),
                null
        );
    }
}

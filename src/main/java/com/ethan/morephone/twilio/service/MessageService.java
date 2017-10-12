package com.ethan.morephone.twilio.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.group.domain.GroupDTO;
import com.ethan.morephone.api.group.service.GroupService;
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
import com.ethan.morephone.twilio.model.ResourceMessage;
import com.ethan.morephone.utils.TextUtils;
import com.ethan.morephone.utils.Utils;
import com.google.common.collect.Range;
import com.twilio.Twilio;
import com.twilio.base.Page;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.api.v2010.account.MessageReader;
import com.twilio.twiml.Body;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;
import com.twilio.type.PhoneNumber;
import org.joda.time.DateTime;
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
    private final GroupService mGroupService;
    private final PhoneNumberService mPhoneNumberService;
    private final EmailServiceImpl mEmailService;

    @Autowired
    MessageService(UserService userService, PhoneNumberService phoneNumberService, UsageService usageService, GroupService groupService, EmailServiceImpl emailService) {
        this.mUserService = userService;
        this.mPhoneNumberService = phoneNumberService;
        this.mUsageService = usageService;
        this.mGroupService = groupService;
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

//            String accountSid = allRequestParams.get("AccountSid");

            UserDTO user = mUserService.findById(phoneNumberDTO.getUserId());
            if (user != null) {
                String token = user.getToken();
                Utils.logMessage("TOKEN RECEIVE SMS: " + token);
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

        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(from);
        if (phoneNumberDTO != null && phoneNumberDTO.getPool()) {
            accountSid = Constants.ACCOUNT_SID;
            authToken = Constants.AUTH_TOKEN;
        }

        Twilio.init(accountSid, authToken);

        UsageDTO usageDTO = mUsageService.findByUserId(userId);

        if (usageDTO == null) {
            return new com.ethan.morephone.http.Response<>(HTTPStatus.BAD_REQUEST.getReasonPhrase(), HTTPStatus.BAD_REQUEST);
        }

//        usageDTO.getBalance()
        if (usageDTO.getBalance() > Constants.PRICE_MESSAGE_OUTGOING) {

            try {
                Message message = new MessageCreator(
                        new PhoneNumber(to),
                        new PhoneNumber(from),
                        body).create();

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
                                                              @RequestParam(value = "phone_number") String phoneNumber,
                                                              @RequestParam(value = "phone_number_id") String phoneNumberId,
                                                              @RequestParam(value = "page_incoming") String pageIncoming,
                                                              @RequestParam(value = "page_outgoing") String pageOutgoing) {
        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(phoneNumber);
        if (phoneNumberDTO != null) {
            if (phoneNumberDTO.getPool()) {
                accountSid = Constants.ACCOUNT_SID;
                authToken = Constants.AUTH_TOKEN;
            }
            Twilio.init(accountSid, authToken);

            HashMap<String, List<MessageItem>> mArrayMap = new HashMap<>();

            MessageReader messageReaderIncoming = new MessageReader(accountSid)
                    .setTo(new PhoneNumber(phoneNumber))
                    .setDateSent(Range.greaterThan(new DateTime(phoneNumberDTO.getCreatedAt())));
            messageReaderIncoming.limit(Constants.LIMIT);

            Page<Message> messagePageIncoming = null;
            if (!com.ethan.morephone.utils.TextUtils.isEmpty(pageIncoming)) {
                if (pageIncoming.equals(Constants.FIRST_PAGE)) {
                    messagePageIncoming = messageReaderIncoming.firstPage();
                } else {
                    messagePageIncoming = messageReaderIncoming.getPage(pageIncoming);
                }

                List<Message> messagesIncoming = messagePageIncoming.getRecords();

                if (messagesIncoming != null) {
                    mArrayMap.putAll(executeData(messagesIncoming, true));
                }
            }

            List<GroupDTO> groupDTOS = mGroupService.findByPhoneNumberId(phoneNumberId);

            MessageReader messageReaderOutgoing = new MessageReader(accountSid)
                    .setFrom(new PhoneNumber(phoneNumber))
                    .setDateSent(Range.greaterThan(new DateTime(phoneNumberDTO.getCreatedAt())));
            messageReaderOutgoing.limit(Constants.LIMIT);

            Page<Message> messagePageOutgoing = null;
            if (!com.ethan.morephone.utils.TextUtils.isEmpty(pageOutgoing)) {
                if (pageOutgoing.equals(Constants.FIRST_PAGE)) {
                    messagePageOutgoing = messageReaderOutgoing.firstPage();
                } else {
                    messagePageOutgoing = messageReaderOutgoing.getPage(pageOutgoing);
                }

                List<Message> messagesOutgoing = messagePageOutgoing.getRecords();

                if (messagesOutgoing != null) {
                    mArrayMap.putAll(executeData(messagesOutgoing, false));
                }
            }


            List<ConversationModel> mConversationModels = new ArrayList<>();

            if(mArrayMap != null && !mArrayMap.isEmpty()) {
                for (Map.Entry entry : mArrayMap.entrySet()) {
                    List<MessageItem> items = mArrayMap.get(entry.getKey());
                    if (items != null && !items.isEmpty()) {
                        Collections.sort(items);
                        String dateCreated = items.get(items.size() - 1).dateCreated;
                        mConversationModels.add(new ConversationModel("", entry.getKey().toString(), dateCreated, items));
                    }
                }
            }

            String incomingFirstPageUrl = "";
            String incomingNextPageUrl = "";
            String incomingPreviousPageUrl = "";
            String incomingUrl = "";
            String outgoingFirstPageUrl = "";
            String outgoingNextPageUrl = "";
            String outgoingPreviousPageUrl = "";
            String outgoingUrl = "";
            int pageSize = 0;

            if (messagePageIncoming != null) {
                incomingFirstPageUrl = messagePageIncoming.getFirstPageUrl("api", null).contains("null") ? "" : messagePageIncoming.getFirstPageUrl("api", null);
                incomingNextPageUrl = messagePageIncoming.getNextPageUrl("api", null).contains("null") ? "" : messagePageIncoming.getNextPageUrl("api", null);
                incomingPreviousPageUrl = messagePageIncoming.getPreviousPageUrl("api", null).contains("null") ? "" : messagePageIncoming.getPreviousPageUrl("api", null);
                incomingUrl = messagePageIncoming.getUrl("api", null).contains("null") ? "" : messagePageIncoming.getUrl("api", null);
                pageSize = messagePageIncoming.getPageSize();
            }

            if (messagePageOutgoing != null) {
                outgoingFirstPageUrl = messagePageOutgoing.getFirstPageUrl("api", null).contains("null") ? "" : messagePageOutgoing.getFirstPageUrl("api", null);
                outgoingNextPageUrl = messagePageOutgoing.getNextPageUrl("api", null).contains("null") ? "" : messagePageOutgoing.getNextPageUrl("api", null);
                outgoingPreviousPageUrl = messagePageOutgoing.getPreviousPageUrl("api", null).contains("null") ? "" : messagePageOutgoing.getPreviousPageUrl("api", null);
                outgoingUrl = messagePageOutgoing.getUrl("api", null).contains("null") ? "" : messagePageOutgoing.getUrl("api", null);
                pageSize = messagePageOutgoing.getPageSize();
            }

            if(!mConversationModels.isEmpty()) {

                ResourceMessage resourceMessage = new ResourceMessage(mConversationModels,
                        incomingFirstPageUrl,
                        incomingNextPageUrl,
                        incomingPreviousPageUrl,
                        incomingUrl,
                        outgoingFirstPageUrl,
                        outgoingNextPageUrl,
                        outgoingPreviousPageUrl,
                        outgoingUrl,
                        pageSize);


                return new com.ethan.morephone.http.Response<>(resourceMessage, HTTPStatus.OK);
            }
        }
        return new com.ethan.morephone.http.Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
    }

    private static HashMap<String, List<MessageItem>> executeData(List<Message> messageItems,
                                                                  boolean isComing) {
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
                message.getDateCreated() == null ? "" : message.getDateCreated().toString(Constants.FORMAT_DATE),
                message.getDateUpdated() == null ? "" : message.getDateUpdated().toString(Constants.FORMAT_DATE),
                message.getDateSent() == null ? "" : message.getDateSent().toString(Constants.FORMAT_DATE),
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

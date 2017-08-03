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
import com.ethan.morephone.twilio.fcm.FCM;
import com.ethan.morephone.twilio.model.NotificationRequest;
import com.ethan.morephone.twilio.model.Response;
import com.ethan.morephone.utils.TextUtils;
import com.ethan.morephone.utils.Utils;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.notify.v1.service.Notification;
import com.twilio.rest.notify.v1.service.NotificationCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by truongnguyen on 7/20/17.
 */
@RestController
@RequestMapping(value = "/api/v1/message")
public class MessageService {

    private final UserService mUserService;
    private final UsageService mUsageService;
    private final PhoneNumberService mPhoneNumberService;

    @Autowired
    MessageService(UserService userService, PhoneNumberService phoneNumberService, UsageService usageService) {
        this.mUserService = userService;
        this.mPhoneNumberService = phoneNumberService;
        this.mUsageService = usageService;
    }

    @RequestMapping(value = "/receive-message", method = RequestMethod.POST)
    public void receiveMessage(@RequestParam Map<String, String> allRequestParams) {
        Utils.logMessage("Receive MultiValueMap: " + allRequestParams.toString());
        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(allRequestParams.get("To"));
        if (phoneNumberDTO != null) {
            String userId = phoneNumberDTO.getUserId();

            Utils.logMessage("RECEIVE SMS USER ID: " + userId);
            UserDTO user = mUserService.findById(userId);
            if (user != null) {
                String token = user.getToken();
                Utils.logMessage("TOKEN: " + token);
                List<String> identities = new ArrayList<>();
                identities.add(user.getEmail());
//                sendNotification("High", allRequestParams.get("To"), allRequestParams.get("From"), allRequestParams.get("Body"), identities);
                sendNotification(token, allRequestParams.get("From") + "-" + allRequestParams.get("To"), allRequestParams.get("Body"));
            }
        }

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
            Utils.logMessage("MONEY: " + usageDTO.getBalance());
            return new com.ethan.morephone.http.Response<>(HTTPStatus.MONEY.getReasonPhrase(), HTTPStatus.MONEY);
        }
        return new com.ethan.morephone.http.Response<>(HTTPStatus.NOT_ACCEPTABLE.getReasonPhrase(), HTTPStatus.NOT_ACCEPTABLE);
    }

    @PostMapping(value = "/send-notification")
    public Response notification(@RequestBody NotificationRequest notificationRequest) {
        return sendNotification(notificationRequest.getPriority(), "PHone Number", notificationRequest.getTitle(), notificationRequest.getBody(), notificationRequest.getIdentity());
    }

    private static void sendNotification(String tokenId, String title, String body) {
        //Just I am passed dummy information

//Method to send Push Notification
        FCM.send_FCM_Notification(tokenId, Constants.FCM_SERVER_KEY, title, body);
    }

    private Response sendNotification(String priorityRequest, String phoneNumber, String title, String body, List<String> identity) {
        Twilio.init(Constants.TWILIO_API_KEY, Constants.TWILIO_API_SECRET, Constants.TWILIO_ACCOUNT_SID);

        try {
            // Convert Priority from Object to enum value
            Notification.Priority priority = Notification.Priority.forValue(priorityRequest);
            Utils.logMessage("priority: " + priority.name());
            NotificationCreator notificationCreator = new NotificationCreator(Constants.TWILIO_NOTIFICATION_SERVICE_SID);
            notificationCreator.setTitle(title + "-" + phoneNumber);
            notificationCreator.setBody(body);
            notificationCreator.setPriority(priority);
            notificationCreator.setIdentity(identity);
            Notification notification = notificationCreator.create();

            Utils.logMessage("Notification successfully created");
            Utils.logMessage(notification.toString());

            // Send a JSON response indicating success
            Response sendNotificationResponse = new Response("Notification Created", "");
            return sendNotificationResponse;

        } catch (Exception ex) {

            // Send a JSON response indicating an error
            Response sendNotificationResponse = new Response("Failed to create notification: " + ex.getMessage(), ex.getMessage());
            return sendNotificationResponse;
        }
    }

}

package com.ethan.morephone.twilio.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.user.domain.UserDTO;
import com.ethan.morephone.api.user.service.UserService;
import com.ethan.morephone.twilio.fcm.FCM;
import com.ethan.morephone.twilio.model.NotificationRequest;
import com.ethan.morephone.twilio.model.Response;
import com.ethan.morephone.utils.Utils;
import com.google.common.base.CaseFormat;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.http.TwilioRestClient;
import com.twilio.jwt.accesstoken.*;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.notify.v1.service.Notification;
import com.twilio.rest.notify.v1.service.NotificationCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by truongnguyen on 7/20/17.
 */
@RestController
@RequestMapping(value = "/message")
public class MessageService {

    private final UserService mUserService;
    private final PhoneNumberService mPhoneNumberService;

    @Autowired
    MessageService(UserService userService, PhoneNumberService phoneNumberService) {
        this.mUserService = userService;
        this.mPhoneNumberService = phoneNumberService;
    }


    @RequestMapping(value = "/receive-message", method = RequestMethod.POST, produces = {"application/xml"})
    public void receiveMessage(@RequestParam Map<String, String> allRequestParams) {
        Utils.logMessage("Receive MultiValueMap: " + allRequestParams.toString());
        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(allRequestParams.get("To"));
        if(phoneNumberDTO != null){
            String userId = phoneNumberDTO.getUserId();

            Utils.logMessage("USER ID: " + userId);
            UserDTO user = mUserService.findById(userId);
            if(user != null){
                String token = user.getToken();
                Utils.logMessage("TOKEN: " + token);
                List<String> identities = new ArrayList<>();
                identities.add(user.getEmail());
                sendNotification("High", allRequestParams.get("From"), allRequestParams.get("Body"), identities);
//                sendNotification(token, allRequestParams.get("From") +" FCM ", allRequestParams.get("Body"));
            }
        }
//        List<String> identities = new ArrayList<>();
//        identities.add(allRequestParams.get("To"));
//        sendNotification("High", allRequestParams.get("From"), allRequestParams.get("Body"), identities);

    }

    @PostMapping(value = "/send-message")
    public String sendMessage(@RequestParam(value = "from") String from,
                              @RequestParam(value = "to") String to,
                              @RequestParam(value = "body") String body) {
        TwilioRestClient client = new TwilioRestClient.Builder(Constants.TWILIO_ACCOUNT_SID, Constants.TWILIO_AUTH_TOKEN).build();
        try {
            Message message = new MessageCreator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    body).create(client);

            return message.getSid();
        } catch (TwilioException e) {
            Utils.logMessage("An exception occurred trying to send a message to {}, exception: {} " + to + " ||| " + e.getMessage());
        }
        return "";
    }

    @PostMapping(value = "/send-notification")
    public Response notification(@RequestBody NotificationRequest notificationRequest) {
        return sendNotification(notificationRequest.getPriority(), notificationRequest.getTitle(), notificationRequest.getBody(), notificationRequest.getIdentity());
    }

    private static void sendNotification(String tokenId, String title, String body) {
        //Just I am passed dummy information

//Method to send Push Notification
        FCM.send_FCM_Notification(tokenId, Constants.FCM_SERVER_KEY, title, body);
    }

    private Response sendNotification(String priorityRequest, String title, String body, List<String> identity){
        Twilio.init(Constants.TWILIO_API_KEY, Constants.TWILIO_API_SECRET, Constants.TWILIO_ACCOUNT_SID);

        try {
            // Convert Priority from Object to enum value
            Notification.Priority priority = Notification.Priority.forValue(priorityRequest);
            Utils.logMessage("priority: " + priority.name());
            NotificationCreator notificationCreator = new NotificationCreator(Constants.TWILIO_NOTIFICATION_SERVICE_SID);
            notificationCreator.setTitle(title);
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



    private String generateToken(String identity) {

        // Create access token builder
        AccessToken.Builder builder = new AccessToken.Builder(
                Constants.TWILIO_ACCOUNT_SID,
                Constants.TWILIO_API_KEY,
                Constants.TWILIO_API_SECRET
        ).identity(identity);

        List<Grant> grants = new ArrayList<>();

        // Add Sync grant if configured
        SyncGrant grant = new SyncGrant();
        grant.setServiceSid(Constants.TWILIO_SYNC_SERVICE_SID);
        grants.add(grant);

        // Add Chat grant if configured
        IpMessagingGrant grantChat = new IpMessagingGrant();
        grantChat.setServiceSid(Constants.TWILIO_CHAT_SERVICE_SID);
        grants.add(grantChat);

        // Add Video grant
        VideoGrant grantVideo = new VideoGrant();
        grants.add(grantVideo);

        builder.grants(grants);

        AccessToken token = builder.build();

        return token.toJwt();
    }

    // Convert keys to camelCase to conform with the twilio-java api definition contract
    private static Map<String, Object> camelCaseKeys(Map<String, Object> map) {
        Map<String, Object> newMap = new HashMap<>();
        map.forEach((k, v) -> {
            String newKey = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, k);
            newMap.put(newKey, v);
        });
        return newMap;
    }

}

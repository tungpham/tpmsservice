package com.ethan.morephone.service;

import com.ethan.morephone.model.BindingRequest;
import com.ethan.morephone.model.NotificationRequest;
import com.ethan.morephone.model.Response;
import com.ethan.morephone.utils.Utils;
import com.google.common.base.CaseFormat;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.http.TwilioRestClient;
import com.twilio.jwt.accesstoken.*;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.notify.v1.service.Binding;
import com.twilio.rest.notify.v1.service.BindingCreator;
import com.twilio.rest.notify.v1.service.Notification;
import com.twilio.rest.notify.v1.service.NotificationCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by truongnguyen on 7/20/17.
 */
@RestController
@RequestMapping(value = "/notification")
public class MessageService {

    private String TWILIO_ACCOUNT_SID = "ACebd7d3a78e2fdda9e51239bad6b09f97";
    private String TWILIO_AUTH_TOKEN = "8d2af0937ed2a581dbb19f70dd1dd43b";
    private String TWILIO_API_SECRET = "Um8JRwIztNvOFED19jRxubSAZXgTmOmH";
    private String TWILIO_API_KEY = "SK028e5bbb3d0b19cb333dfe99ba10c35f";
    private String TWILIO_NOTIFICATION_SERVICE_SID = "IS22d53044e23416340337ea9d85c35f90";
    private String TWILIO_CHAT_SERVICE_SID = "";
    private String TWILIO_SYNC_SERVICE_SID = "";


    @PostMapping(value = "/register")
    public Response register(@RequestBody BindingRequest bindingRequest) {
        Twilio.init(TWILIO_API_KEY, TWILIO_API_SECRET, TWILIO_ACCOUNT_SID);
        try {
            // Convert BindingType from Object to enum value
            Binding.BindingType bindingType = Binding.BindingType.forValue(bindingRequest.getBinding());
            // Add the notification service sid
            String serviceSid = TWILIO_NOTIFICATION_SERVICE_SID;

            // Create the binding
            BindingCreator bindingCreator = new BindingCreator(serviceSid, bindingRequest.getEndpoint(), bindingRequest.getIdentity(), bindingType, bindingRequest.getAddress());
            Binding binding = bindingCreator.create();

            // Send a JSON response indicating success
            Response bindingResponse = new Response("Binding Created", "");
            return bindingResponse;

        } catch (Exception ex) {
            // Send a JSON response indicating an error
            Response bindingResponse = new Response("Failed to create binding: " + ex.getMessage(), ex.getMessage());
            return bindingResponse;
        }
    }

    @RequestMapping(value = "/receive-message", method = RequestMethod.POST, produces = {"application/xml"})
    public void receiveMessage(@RequestParam Map<String, String> allRequestParams) {
        Utils.logMessage("Receive MultiValueMap: " + allRequestParams.toString());
    }

    @PostMapping(value = "/send-message")
    public String sendMessage(@RequestParam(value = "from") String from,
                              @RequestParam(value = "to") String to,
                              @RequestParam(value = "body") String body) {
        TwilioRestClient client = new TwilioRestClient.Builder(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN).build();
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
        // Authenticate with Twilio
        Twilio.init(TWILIO_API_KEY, TWILIO_API_SECRET, TWILIO_ACCOUNT_SID);

        try {
            // Convert Priority from Object to enum value
            Notification.Priority priority = Notification.Priority.forValue(notificationRequest.getPriority());
            Utils.logMessage("priority: " + priority.name());
            NotificationCreator notificationCreator = new NotificationCreator(TWILIO_NOTIFICATION_SERVICE_SID);
            notificationCreator.setTitle(notificationRequest.getTitle());
            notificationCreator.setBody(notificationRequest.getBody());
            notificationCreator.setPriority(priority);
            notificationCreator.setIdentity(notificationRequest.getIdentity());
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
                TWILIO_ACCOUNT_SID,
                TWILIO_API_KEY,
                TWILIO_API_SECRET
        ).identity(identity);

        List<Grant> grants = new ArrayList<>();

        // Add Sync grant if configured
        SyncGrant grant = new SyncGrant();
        grant.setServiceSid(TWILIO_SYNC_SERVICE_SID);
        grants.add(grant);

        // Add Chat grant if configured
        IpMessagingGrant grantChat = new IpMessagingGrant();
        grantChat.setServiceSid(TWILIO_CHAT_SERVICE_SID);
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

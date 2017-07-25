package com.ethan.morephone.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.data.entity.application.Applications;
import com.ethan.morephone.data.entity.phonenumbers.IncomingPhoneNumber;
import com.ethan.morephone.data.network.ApiManager;
import com.ethan.morephone.model.BindingRequest;
import com.ethan.morephone.model.Response;
import com.twilio.Twilio;
import com.twilio.rest.notify.v1.service.Binding;
import com.twilio.rest.notify.v1.service.BindingCreator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by truongnguyen on 7/25/17.
 */
@RestController
@RequestMapping(value = "/phone")
public class PhoneService {

    @PostMapping(value = "/register")
    public Response register(@RequestBody BindingRequest bindingRequest) {

        if(!registerApplication(bindingRequest.getIncomingPhoneNumberSid())){
            Response bindingResponse = new Response("Failed to register application: ", "400");
            return bindingResponse;
        }

        Twilio.init(Constants.TWILIO_API_KEY, Constants.TWILIO_API_SECRET, Constants.TWILIO_ACCOUNT_SID);
        try {
            // Convert BindingType from Object to enum value
            Binding.BindingType bindingType = Binding.BindingType.forValue(bindingRequest.getBinding());
            // Add the notification service sid

            // Create the binding
            BindingCreator bindingCreator = new BindingCreator(Constants.TWILIO_NOTIFICATION_SERVICE_SID, bindingRequest.getEndpoint(), bindingRequest.getIdentity(), bindingType, bindingRequest.getAddress());
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

    //  With new phone number must register application for voice and message
    private boolean registerApplication(String incomingPhoneNumberSid) {
//        String applicationSid = getApplicationSid();
        IncomingPhoneNumber incomingPhoneNumber = ApiManager.registerApplicationSms(incomingPhoneNumberSid, Constants.TWILIO_APPLICATION_SID);
        return incomingPhoneNumber == null ? false : true;
    }

    //    Get Sid from application want to register
    private String getApplicationSid() {
        Applications applications = ApiManager.getApplications();
        if (applications != null && applications.applications != null && !applications.applications.isEmpty()) {
            return applications.applications.get(0).sid;
        } else {
            return "";
        }
    }

}

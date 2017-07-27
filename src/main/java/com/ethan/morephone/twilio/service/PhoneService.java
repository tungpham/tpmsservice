package com.ethan.morephone.twilio.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.data.entity.application.Applications;
import com.ethan.morephone.data.network.ApiManager;
import com.ethan.morephone.twilio.model.BindingRequest;
import com.ethan.morephone.twilio.model.Response;
import com.ethan.morephone.utils.Utils;
import com.twilio.Twilio;
import com.twilio.rest.notify.v1.service.Binding;
import com.twilio.rest.notify.v1.service.BindingCreator;
import org.springframework.web.bind.annotation.*;

/**
 * Created by truongnguyen on 7/25/17.
 */
@RestController
@RequestMapping(value = "/phone")
public class PhoneService {

    @PostMapping(value = "/register-application")
    public Response registerApplications(@RequestParam(value = "incoming_phone_number_sid") String incomingPhoneNumberSid){
//        IncomingPhoneNumber incomingPhoneNumber = ApiManager.registerApplicationSms(incomingPhoneNumberSid, Constants.TWILIO_APPLICATION_SID);
//        if(incomingPhoneNumber == null){
//            Response bindingResponse = new Response("Failed to register application: ", "400");
//            return bindingResponse;
//        }else{
//            Response bindingResponse = new Response("REGISTER SUCCESS ", incomingPhoneNumberSid);
//            return bindingResponse;
//        }
        return null;
    }

    @PostMapping(value = "/binding")
    public Response binding(@RequestBody BindingRequest bindingRequest) {
        Twilio.init(Constants.TWILIO_API_KEY, Constants.TWILIO_API_SECRET, Constants.TWILIO_ACCOUNT_SID);
        try {
            // Convert BindingType from Object to enum value
            Binding.BindingType bindingType = Binding.BindingType.forValue(bindingRequest.getBinding());
            // Add the notification service sid

            Utils.logMessage("SERVICE: " + Constants.TWILIO_NOTIFICATION_SERVICE_SID);
            Utils.logMessage("ENDPOINT: " + bindingRequest.getEndpoint());
            Utils.logMessage("bindingType: " + bindingType);
            Utils.logMessage("identity: " + bindingRequest.getIdentity());
            Utils.logMessage("ADDRESS: " + bindingRequest.getAddress());
            // Create the binding
            BindingCreator bindingCreator = new BindingCreator(Constants.TWILIO_NOTIFICATION_SERVICE_SID, bindingRequest.getEndpoint(), bindingRequest.getIdentity(), bindingType, bindingRequest.getAddress());
            Binding binding = bindingCreator.create();

            // Send a JSON response indicating success
            Response bindingResponse = new Response("Binding Created", "" + bindingRequest.getIdentity());
            return bindingResponse;

        } catch (Exception ex) {
            // Send a JSON response indicating an error
            Response bindingResponse = new Response("Failed to create binding: " + ex.getMessage(), ex.getMessage());
            return bindingResponse;
        }
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

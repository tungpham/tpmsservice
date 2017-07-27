package com.ethan.morephone.twilio.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.user.domain.UserDTO;
import com.ethan.morephone.api.user.service.UserService;
import com.ethan.morephone.utils.Utils;
import com.twilio.sdk.CapabilityToken;
import com.twilio.sdk.client.TwilioCapability;
import com.twilio.sdk.verbs.*;
import com.twilio.sdk.verbs.Number;
import com.twilio.twiml.Say;
import com.twilio.twiml.VoiceResponse;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by truongnguyen on 6/30/17.
 */
@RestController
@RequestMapping(value = "/call")
public class CallService {

    private final UserService mUserService;
    private final PhoneNumberService mPhoneNumberService;

    @Autowired
    CallService(UserService userService, PhoneNumberService phoneNumberService) {
        this.mUserService = userService;
        this.mPhoneNumberService = phoneNumberService;
    }

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public String createToken(@RequestParam(value = "client") String client) {

//        Ethan
//        String TWILIO_ACCOUNT_SID = "ACebd7d3a78e2fdda9e51239bad6b09f97";
//        String TWILIO_AUTH_TOKEN = "8d2af0937ed2a581dbb19f70dd1dd43b";
//        String TWILIO_APPLICATION_SID = "AP5d46bf675557ec0f73b1d08afcfcdc75";

//        More Phone

//        String TWILIO_ACCOUNT_SID = "ACdd510b09cfb9af9f1c2dd9d45e9ce1e5";
//        String TWILIO_AUTH_TOKEN = "18b65f8d69b4982f6a34a59704df83f4";
//        String TWILIO_APPLICATION_SID = "AC4e52b443ac512d71421b5fb901732d17";

        // Create an access token using our Twilio credentials
        // Generate a random username for the connecting client

        TwilioCapability capability = new TwilioCapability(Constants.TWILIO_ACCOUNT_SID, Constants.TWILIO_AUTH_TOKEN);
        capability.allowClientOutgoing(Constants.TWILIO_APPLICATION_SID);
        capability.allowClientIncoming(client);

        String token = "";
        try {
            token = capability.generateToken();
        } catch (CapabilityToken.DomainException e) {
            e.printStackTrace();
        }

        return token;
    }

    @RequestMapping(value = "/call", method = RequestMethod.POST, produces = {"application/xml"})
    public String call(@RequestParam Map<String, String> allRequestParams) {

        System.out.println("MultiValueMap: " + allRequestParams.toString());

        String from = allRequestParams.get("From");
        String to = allRequestParams.get("To");

//        String CALLER_ID = "+17606215500";
//        String CLIENT_ID = "17606215500";

        TwiMLResponse twiml = new TwiMLResponse();

        VoiceResponse voiceResponse = new VoiceResponse.Builder()
                .say(new Say.Builder("Invalid Value f").build())
                .build();

        if (TextUtils.isEmpty(from) && TextUtils.isEmpty(to)) {
            try {
                return voiceResponse.toXml();
            } catch (com.twilio.twiml.TwiMLException e) {
                e.printStackTrace();
            }
        }

        Dial dial = new Dial();


        if (from.startsWith("client:")) {
            System.out.println("Client - PSTN");
            // client -> PSTN
            dial.setCallerId(from.substring(7, from.length()));
            dial.setRecord(true);
            try {
                dial.append(new Number(to));
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("PSTN - client");
            // PSTN -> client
            dial.setCallerId(from);
            dial.setRecord(true);

            PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(allRequestParams.get("To"));
            if (phoneNumberDTO != null) {
                String userId = phoneNumberDTO.getUserId();

                Utils.logMessage("USER ID: " + userId);
                UserDTO user = mUserService.findById(userId);

                if (user != null) {
                    try {
                        Utils.logMessage("EMAIL: " + user.getEmail());
                        dial.append(new Client(user.getEmail()));
                    } catch (TwiMLException e) {
                        e.printStackTrace();
                        Utils.logMessage("ERROR APPEND: " + e.getMessage());
                    }
                }

            } else {
                Utils.logMessage("CALL NORMAL");
                try {
                    dial.append(new Client(to));
                } catch (TwiMLException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            twiml.append(dial);
        } catch (TwiMLException e) {
            e.printStackTrace();
            Utils.logMessage("ERROR CALL: " + e.getMessage());
        }
        return twiml.toXML();
    }

//    @RequestMapping(value = "/call", method = RequestMethod.POST, produces={"application/xml"})
//    public String call(@RequestParam(value = "From") String from, @RequestParam(value = "To") String to) {
//
//        String CALLER_ID = "+17606215500";
//        String CLIENT_ID = "17606215500";
//
//        System.out.println("FROM: " + from + " || TO: " + to);
//
//        TwiMLResponse twiml = new TwiMLResponse();
//
//        VoiceResponse voiceResponse = new VoiceResponse.Builder()
//                .say(new Say.Builder("Invalid Value f").build())
//                .build();
//
//        if (TextUtils.isEmpty(from) && TextUtils.isEmpty(to)) {
//            try {
//                return voiceResponse.toXml();
//            } catch (com.twilio.twiml.TwiMLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        Dial dial = new Dial();
//
//        if (from.startsWith("client:")) {
//            System.out.println("Client - PSTN");
//            // client -> PSTN
//            dial.setCallerId(from.substring(7, from.length()));
//            try {
//                dial.append(new Number(to));
//            } catch (TwiMLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("PSTN - client");
//            // PSTN -> client
//            dial.setCallerId(from);
//            try {
//                dial.append(new Client(to));
//            } catch (TwiMLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            twiml.append(dial);
//        } catch (TwiMLException e) {
//            e.printStackTrace();
//        }
//        return twiml.toXML();
//    }


}

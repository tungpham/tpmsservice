package com.ethan.morephone.twilio.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.user.service.UserService;
import com.ethan.morephone.utils.Utils;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VoiceGrant;
import com.twilio.sdk.CapabilityToken;
import com.twilio.sdk.client.TwilioCapability;
import com.twilio.twiml.*;
import com.twilio.twiml.Number;
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

    @RequestMapping(value = "/voice/token", method = RequestMethod.GET)
    public String token(@RequestParam(value = "identity") String identity) {
        VoiceGrant voiceGrant = new VoiceGrant();
        voiceGrant.setPushCredentialSid(Constants.TWILIO_PUSH_CREDENTIALS);
        voiceGrant.setOutgoingApplicationSid(Constants.TWILIO_APPLICATION_SID);

        AccessToken accessToken = new AccessToken.Builder(Constants.TWILIO_ACCOUNT_SID, Constants.TWILIO_API_KEY, Constants.TWILIO_API_SECRET)
                .identity(identity)
                .grant(voiceGrant)
                .build();
        return accessToken.toJwt();
    }

    String IDENTITY = "voice_test";
    String CALLER_ID = "quick_start";

    @RequestMapping(value = "/python/token", method = RequestMethod.GET)
    public String tokenPython() {
        VoiceGrant voiceGrant = new VoiceGrant();
        voiceGrant.setPushCredentialSid(Constants.TWILIO_PUSH_CREDENTIALS);
        voiceGrant.setOutgoingApplicationSid(Constants.TWILIO_APPLICATION_SID);

        AccessToken accessToken = new AccessToken.Builder(Constants.TWILIO_ACCOUNT_SID, Constants.TWILIO_API_KEY, Constants.TWILIO_API_SECRET)
                .identity(IDENTITY)
                .grant(voiceGrant)
                .build();
        return accessToken.toJwt();
    }

    @RequestMapping(value = "/place", method = RequestMethod.GET, produces = {"application/xml"})
    public String placeCall() {
        VoiceResponse twiml;

        Dial dial = new Dial.Builder()
                .callerId(CALLER_ID)
                .client(new Client.Builder(IDENTITY).build())
                .build();
        twiml = new VoiceResponse.Builder().dial(dial).build();

        try {
            return twiml.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
            Utils.logMessage("ERROR CALL: " + e.getMessage());
        }
        return "";
    }

//    @RequestMapping(value = "/call", method = RequestMethod.POST, produces = {"application/xml"})
//    public String call(@RequestParam Map<String, String> allRequestParams) {
//
//        System.out.println("MultiValueMap: " + allRequestParams.toString());
//
//        String from = allRequestParams.get("From");
//        String to = allRequestParams.get("To");
//
////        String CALLER_ID = "+17606215500";
////        String CLIENT_ID = "17606215500";
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
//
//        if (from.startsWith("client:")) {
//            System.out.println("Client - PSTN");
//            // client -> PSTN
//            dial.setCallerId(from.substring(7, from.length()));
//            dial.setRecord(true);
//            try {
//                dial.append(new Number(to));
//            } catch (TwiMLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("PSTN - client");
//            // PSTN -> client
//            dial.setCallerId(from);
//            dial.setRecord(true);
//
//            PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(allRequestParams.get("To"));
//            if (phoneNumberDTO != null) {
//                String userId = phoneNumberDTO.getUserId();
//
//                Utils.logMessage("USER ID: " + userId);
//                UserDTO user = mUserService.findById(userId);
//
//                if (user != null) {
//                    try {
//                        Utils.logMessage("EMAIL: " + user.getEmail());
//                        dial.append(new Client(user.getEmail()));
//                    } catch (TwiMLException e) {
//                        e.printStackTrace();
//                        Utils.logMessage("ERROR APPEND: " + e.getMessage());
//                    }
//                }
//
//            } else {
//                Utils.logMessage("CALL NORMAL");
//                try {
//                    dial.append(new Client(to));
//                } catch (TwiMLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        try {
//            twiml.append(dial);
//        } catch (TwiMLException e) {
//            e.printStackTrace();
//            Utils.logMessage("ERROR CALL: " + e.getMessage());
//        }
//        return twiml.toXML();
//    }

    @RequestMapping(value = "/call", method = RequestMethod.POST, produces = {"application/xml"})
    public String callPhone(@RequestParam Map<String, String> allRequestParams) {

        System.out.println("MultiValueMap: " + allRequestParams.toString());

        String from = allRequestParams.get("From");
        String to = allRequestParams.get("To");

//        String CALLER_ID = "+17606215500";
//        String CLIENT_ID = "17606215500";

        VoiceResponse twiml;

        if (TextUtils.isEmpty(from) && TextUtils.isEmpty(to)) {
            try {
                twiml = new VoiceResponse.Builder()
                        .say(new Say.Builder("Invalid Value f").build())
                        .build();
                return twiml.toXml();
            } catch (com.twilio.twiml.TwiMLException e) {
                e.printStackTrace();
            }
        }

        Dial dial = null;


        if (from.startsWith("client:")) {
            System.out.println("Client - PSTN");
            // client -> PSTN
            dial = new Dial.Builder()
                    .callerId(from.substring(7, from.length()))
                    .number(new Number.Builder(to).build())
                    .build();

        } else {
            System.out.println("PSTN - client");
            // PSTN -> client

//            PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(to);
//            if (phoneNumberDTO != null) {
//                String userId = phoneNumberDTO.getUserId();
//
//                Utils.logMessage("USER ID: " + userId);
//                UserDTO user = mUserService.findById(userId);
//
//                if (user != null) {
////                    try {
////                        Utils.logMessage("EMAIL: " + user.getEmail());
////                        dial.append(new Client(user.getEmail()));
////                    } catch (TwiMLException e) {
////                        e.printStackTrace();
////                        Utils.logMessage("ERROR APPEND: " + e.getMessage());
////                    }
//
//                    dial = new Dial.Builder()
//                            .callerId(from)
//                            .client(new Client.Builder(user.getEmail()).build())
//                            .build();
//
//                    com.twilio.type.Client client;
//                }
////
//            } else {
//                dial = new Dial.Builder()
//                        .callerId(from)
//                        .client(new Client.Builder(to).build())
//                        .build();
//            }

            dial = new Dial.Builder()
                    .callerId(from)
                    .client(new Client.Builder(to).build())
                    .build();
        }

        twiml = new VoiceResponse.Builder().dial(dial).build();

        try {
            return twiml.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
            Utils.logMessage("ERROR CALL: " + e.getMessage());
        }
        return "";
    }

}

package com.ethan.morephone.twilio.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.user.service.UserService;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import com.ethan.morephone.twilio.call.CallStatus;
import com.ethan.morephone.utils.Utils;
import com.twilio.sdk.CapabilityToken;
import com.twilio.sdk.client.TwilioCapability;
import com.twilio.twiml.*;
import com.twilio.twiml.Number;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by truongnguyen on 6/30/17.
 */
@RestController
@RequestMapping(value = "/api/v1/call")
public class CallService {

    private final UserService mUserService;
    private final PhoneNumberService mPhoneNumberService;

    @Autowired
    CallService(UserService userService, PhoneNumberService phoneNumberService) {
        this.mUserService = userService;
        this.mPhoneNumberService = phoneNumberService;
    }

    @PostMapping(value = "/token")
    public Response<Object> createToken(@RequestParam(value = "client") String client,
                                        @RequestParam(value = "account_sid") String accountSid,
                                        @RequestParam(value = "auth_token") String authToken,
                                        @RequestParam(value = "application_sid") String applicationSid) {
        Utils.logMessage("CLIENT: " + client);
        Utils.logMessage("account_sid: " + accountSid);
        Utils.logMessage("auth_token: " + authToken);
        Utils.logMessage("application_sid: " + applicationSid);
        TwilioCapability capability = new TwilioCapability(accountSid, authToken);
        capability.allowClientOutgoing(applicationSid);
        capability.allowClientIncoming(client);

        try {
            String token = capability.generateToken();
            return new Response<>(token, HTTPStatus.CREATED);
        } catch (CapabilityToken.DomainException e) {
            e.printStackTrace();
        }

        return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
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

    @RequestMapping(value = "/events", method = RequestMethod.POST, produces = {"application/xml"})
    public String callEvent(@RequestParam Map<String, String> allRequestParams) {
        Utils.logMessage("MultiValueMap: " + allRequestParams.toString());

        CallStatus callStatus = CallStatus.getCallStatus(allRequestParams.get("CallStatus"));
        String callDuration = allRequestParams.get("CallDuration");
        String parentCallSid = allRequestParams.get("ParentCallSid");
        String direction = allRequestParams.get("Direction");
        String from = allRequestParams.get("From");
        String to = allRequestParams.get("To");

        long duration = 0;
        if (!TextUtils.isEmpty(callDuration)) {
            duration = Long.parseLong(callDuration);
        }

        if (callStatus != null) {
            Utils.logMessage("CALL STATUS: " + callStatus.callStatus());
        }

        return "";

    }

    @RequestMapping(value = "/dial", method = RequestMethod.POST, produces = {"application/xml"})
    public String callPhone(@RequestParam Map<String, String> allRequestParams) {

        Utils.logMessage("MultiValueMap: " + allRequestParams.toString());

        String from = allRequestParams.get("From");
        String to = allRequestParams.get("To");

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
                    .number(new Number.Builder(to)
                            .statusCallback(Constants.EVENT_URL)
                            .statusCallbackMethod(Method.POST)
                            .statusCallbackEvents(Arrays.asList(Event.INITIATED, Event.RINGING, Event.ANSWERED, Event.COMPLETED))
                            .build())
                    .build();

        } else {
            System.out.println("PSTN - client");
            // PSTN -> client

            dial = new Dial.Builder()
                    .callerId(from)
                    .client(new Client.Builder(to)
                            .statusCallback(Constants.EVENT_URL)
                            .statusCallbackMethod(Method.POST)
                            .statusCallbackEvents(Arrays.asList(Event.INITIATED, Event.RINGING, Event.ANSWERED, Event.COMPLETED))
                            .build())
                    .record(Dial.Record.RECORD_FROM_RINGING)
                    .build();
        }

        Say pleaseLeaveMessage = new Say.Builder("Record your monkey howl after the tone.").build();
        // Record the caller's voice.
        Record record = new Record.Builder()
                .maxLength(30)
                .action("/handle-recording") // You may need to change this to point to the location of your servlet
                .build();

        twiml = new VoiceResponse.Builder().dial(dial).record(record).build();

        try {
            Utils.logMessage("RESULT: " + twiml.toXml());
            return twiml.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
            Utils.logMessage("ERROR CALL: " + e.getMessage());
        }
        return "";
    }

    @RequestMapping(value = "/handle-recording", method = RequestMethod.POST, produces = {"application/xml"})
    public String handleRecording(@RequestParam Map<String, String> allRequestParams) {
        Utils.logMessage("MultiValueMap RECORD: " + allRequestParams.toString());
        String recordingUrl = allRequestParams.get("RecordingUrl");

        VoiceResponse twiml = new VoiceResponse.Builder()
                .say(new Say.Builder("Thanks for howling... take a listen to what you howled.").build())
                .play(new Play.Builder(recordingUrl).build())
                .say(new Say.Builder("Goodbye").build())
                .build();

        try {
            return twiml.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
        }
        return "";
    }
}

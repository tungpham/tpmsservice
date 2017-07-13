package com.ethan.morephone.service;

import com.twilio.sdk.CapabilityToken;
import com.twilio.sdk.client.TwilioCapability;
import com.twilio.sdk.verbs.*;
import com.twilio.sdk.verbs.Number;
import com.twilio.twiml.Say;
import com.twilio.twiml.VoiceResponse;
import org.apache.http.util.TextUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by truongnguyen on 6/30/17.
 */
@RestController
@RequestMapping(value = "/twilio")
public class PhoneService {

    @RequestMapping(value = "/create/token", method = RequestMethod.GET)
    public String createToken(@RequestParam(value = "client") String client) {

//        String TWILIO_ACCOUNT_SID = "ACebd7d3a78e2fdda9e51239bad6b09f97";
        String TWILIO_ACCOUNT_SID = "ACdd510b09cfb9af9f1c2dd9d45e9ce1e5";
//        String TWILIO_AUTH_TOKEN = "8d2af0937ed2a581dbb19f70dd1dd43b";
        String TWILIO_AUTH_TOKEN = "18b65f8d69b4982f6a34a59704df83f4";
        String TWILIO_APP_SID = "AP5d46bf675557ec0f73b1d08afcfcdc75";

        // Create an access token using our Twilio credentials
        // Generate a random username for the connecting client

        TwilioCapability capability = new TwilioCapability(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
        capability.allowClientOutgoing(TWILIO_APP_SID);
        capability.allowClientIncoming(client);

        String token = "";
        try {
            token = capability.generateToken();
        } catch (CapabilityToken.DomainException e) {
            e.printStackTrace();
        }

        return token;
    }

    @RequestMapping(value = "/call", method = RequestMethod.GET, produces = {"application/xml"})
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
//            dial.setRecord(true);
            try {
                dial.append(new Number(to));
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("PSTN - client");
            // PSTN -> client
            dial.setCallerId(from);
//            dial.setRecord(true);
            try {
                dial.append(new Client(to));
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
        }

        try {
            twiml.append(dial);
        } catch (TwiMLException e) {
            e.printStackTrace();
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
//
//
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

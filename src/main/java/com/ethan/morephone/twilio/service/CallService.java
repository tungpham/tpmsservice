package com.ethan.morephone.twilio.service;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.usage.domain.UsageDTO;
import com.ethan.morephone.api.usage.service.UsageService;
import com.ethan.morephone.api.user.domain.UserDTO;
import com.ethan.morephone.api.user.service.UserService;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import com.ethan.morephone.twilio.call.CallStatus;
import com.ethan.morephone.twilio.fcm.FCM;
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

    private final UsageService mUsageService;
    private final UserService mUserService;
    private final PhoneNumberService mPhoneNumberService;

    @Autowired
    CallService(UsageService usageService, UserService userService, PhoneNumberService phoneNumberService) {
        this.mUsageService = usageService;
        this.mUserService = userService;
        this.mPhoneNumberService = phoneNumberService;
    }

    @PostMapping(value = "/token")
    public Response<Object> createToken(@RequestParam(value = "client") String client,
                                        @RequestParam(value = "account_sid") String accountSid,
                                        @RequestParam(value = "auth_token") String authToken,
                                        @RequestParam(value = "application_sid") String applicationSid) {

        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(client);
        if(phoneNumberDTO != null){
            Utils.logMessage("CLIENT: " + client);
            Utils.logMessage("account_sid: " + accountSid);
            Utils.logMessage("auth_token: " + authToken);
            Utils.logMessage("application_sid: " + applicationSid);

            TwilioCapability capability;
            if(phoneNumberDTO.getPool()){
                capability = new TwilioCapability(Constants.ACCOUNT_SID, Constants.AUTH_TOKEN);
                capability.allowClientOutgoing(Constants.APPLICATION_ID);
                capability.allowClientIncoming(client);
                Utils.logMessage("ALOW NOW");
            }else{
                capability = new TwilioCapability(accountSid, authToken);
                capability.allowClientOutgoing(applicationSid);
                capability.allowClientIncoming(client);
            }

            try {
                String token = capability.generateToken();
                return new Response<>(token, HTTPStatus.CREATED);
            } catch (CapabilityToken.DomainException e) {
                e.printStackTrace();
            }
        }
        return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
    }

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
            String accountSid = allRequestParams.get("AccountSid");
            UsageDTO usage = mUsageService.findByAccountSid(accountSid);
            if (callStatus == CallStatus.COMPLETED && !TextUtils.isEmpty(direction) && !direction.equals("inbound")) {
                double money = duration * Constants.PRICE_CALL_OUTGOING / 60;

                mUsageService.updateCallOutgoing(usage.getUserId(), usage.getBalance() - money);
            } else {
                mUsageService.updateCallIncoming(usage.getUserId(), usage.getBalance());
                Utils.logMessage("DIRECTION: " + direction);
            }
            Utils.logMessage("CALL STATUS: " + callStatus.callStatus() + " ||| DIRECTION: " + direction);
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

            Utils.logMessage("Client - PSTN");

            String accountSid = allRequestParams.get("AccountSid");

            UsageDTO usage = mUsageService.findByAccountSid(accountSid);
            if (usage != null) {

                if (usage.getBalance() < Constants.PRICE_CALL_MIN) {
                    UserDTO userDTO = mUserService.findById(usage.getUserId());
                    if (userDTO != null) {
                        FCM.sendNotification(userDTO.getToken(), Constants.FCM_SERVER_KEY, HTTPStatus.MONEY.getReasonPhrase(), "");
                    }
                    try {
                        twiml = new VoiceResponse.Builder()
                                .say(new Say.Builder("Your more phone is out of money. Please add money to your account.").build())
                                .build();
                        return twiml.toXml();
                    } catch (com.twilio.twiml.TwiMLException e) {
                        e.printStackTrace();
                    }
                } else {
                    int limit = (int) (usage.getBalance() / Constants.PRICE_CALL_OUTGOING * 60);
                    Utils.logMessage(" TOTAL MONEY: " + usage.getBalance() + "      ||    " + limit);
                    dial = new Dial.Builder()
                            .callerId(from.substring(7, from.length()))
                            .number(new Number.Builder(to)
                                    .statusCallback(Constants.EVENT_URL)
                                    .statusCallbackMethod(Method.POST)
                                    .statusCallbackEvents(Arrays.asList(Event.INITIATED, Event.RINGING, Event.ANSWERED, Event.COMPLETED))
                                    .build())
                            .timeLimit(limit)
                            .build();
                }
            }

        } else {

            Utils.logMessage("PSTN - client");
            dial = new Dial.Builder()
                    .callerId(from)
                    .client(new Client.Builder(to)
                            .statusCallback(Constants.EVENT_URL)
                            .statusCallbackMethod(Method.POST)
                            .statusCallbackEvents(Arrays.asList(Event.INITIATED, Event.RINGING, Event.ANSWERED, Event.COMPLETED))
                            .build())
                    .timeout(10)
                    .action("/api/v1/call/leave-message")
                    .build();
        }

        twiml = new VoiceResponse.Builder().dial(dial).build();

        try {
            return twiml.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/leave-message", method = RequestMethod.POST, produces = {"application/xml"})
    public String record(@RequestParam Map<String, String> allRequestParams) {

        Utils.logMessage("MultiValueMap RECORD: " + allRequestParams.toString());
        Say pleaseLeaveMessage = new Say.Builder("Please leave a message at the beep. \n" +
                                                        "Press the star key when finished. ").build();
        // Record the caller's voice.
        Record record = new Record.Builder()
                .finishOnKey("*")
                .recordingStatusCallbackMethod(Method.POST)
                .recordingStatusCallback("/api/v1/call/record-event")
                .action("/api/v1/call/handle-recording") // You may need to change this to point to the location of your servlet
                .build();

        VoiceResponse twiml = new VoiceResponse.Builder().say(pleaseLeaveMessage).record(record).build();

        try {
            return twiml.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/handle-recording", method = RequestMethod.POST, produces = {"application/xml"})
    public String handleRecording(@RequestParam Map<String, String> allRequestParams) {

        Utils.logMessage("MultiValueMap HANDLE RECORD: " + allRequestParams.toString());
        String recordingUrl = allRequestParams.get("RecordingUrl");
        Utils.logMessage("RECORDING URL: " + recordingUrl);

        VoiceResponse twiml = new VoiceResponse.Builder()
                .say(new Say.Builder("Thank you for your message. Goodbye.").build())
                .build();

        try {
            return twiml.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/record-event", method = RequestMethod.POST, produces = {"application/xml"})
    public String recordEvent(@RequestParam Map<String, String> allRequestParams) {
        Utils.logMessage("MultiValueMap RECORD EVENT: " + allRequestParams.toString());

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

            if (callStatus == CallStatus.COMPLETED && !TextUtils.isEmpty(direction) && !direction.equals("inbound")) {
                double money = duration * Constants.PRICE_CALL_OUTGOING / 60;
                Utils.logMessage("BI TRU: " + money);

                String accountSid = allRequestParams.get("AccountSid");

                UsageDTO usage = mUsageService.findByAccountSid(accountSid);
                mUsageService.updateCallOutgoing(usage.getUserId(), usage.getBalance() - money);
            } else {
                Utils.logMessage("DIRECTION: " + direction);
            }
            Utils.logMessage("CALL STATUS: " + callStatus.callStatus());
        }

        return "";

    }

}

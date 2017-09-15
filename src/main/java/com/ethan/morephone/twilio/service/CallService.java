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
import com.ethan.morephone.twilio.model.CallDTO;
import com.ethan.morephone.twilio.model.ResourceCall;
import com.ethan.morephone.utils.Utils;
import com.google.common.collect.Range;
import com.twilio.Twilio;
import com.twilio.base.Page;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.CallFetcher;
import com.twilio.rest.api.v2010.account.CallReader;
import com.twilio.rest.api.v2010.account.Recording;
import com.twilio.sdk.CapabilityToken;
import com.twilio.sdk.client.TwilioCapability;
import com.twilio.twiml.*;
import com.twilio.twiml.Number;
import com.twilio.type.PhoneNumber;
import org.apache.http.util.TextUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        if (phoneNumberDTO != null) {
            Utils.logMessage("CLIENT: " + client);
            Utils.logMessage("account_sid: " + accountSid);
            Utils.logMessage("auth_token: " + authToken);
            Utils.logMessage("application_sid: " + applicationSid);

            TwilioCapability capability;
            if (phoneNumberDTO.getPool()) {
                capability = new TwilioCapability(Constants.ACCOUNT_SID, Constants.AUTH_TOKEN);
                capability.allowClientOutgoing(Constants.APPLICATION_ID);
                capability.allowClientIncoming(client);
                Utils.logMessage("ALOW NOW");
            } else {
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

        if (callStatus != null && callStatus == CallStatus.COMPLETED) {

            PhoneNumberDTO phoneNumberFrom = mPhoneNumberService.findByPhoneNumber(from);

            if (phoneNumberFrom != null) {
                UsageDTO usageFrom = mUsageService.findByUserId(phoneNumberFrom.getUserId());
                if (usageFrom != null) {
                    double moneyFrom = duration * Constants.PRICE_CALL_OUTGOING / 60;
                    mUsageService.updateCallOutgoing(usageFrom.getUserId(), usageFrom.getBalance() - moneyFrom);
                }
            }

            PhoneNumberDTO phoneNumberTo = mPhoneNumberService.findByPhoneNumber(to);
            if (phoneNumberTo != null) {
                UsageDTO usageTo = mUsageService.findByUserId(phoneNumberTo.getUserId());
                if (usageTo != null) {
                    double moneyTo = duration * Constants.PRICE_CALL_INCOMING / 60;
                    mUsageService.updateCallOutgoing(usageTo.getUserId(), usageTo.getBalance() - moneyTo);
                }
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

//            String accountSid = allRequestParams.get("AccountSid");
            PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(from.substring(7, from.length()));
            if (phoneNumberDTO != null) {

                UsageDTO usage = mUsageService.findByUserId(phoneNumberDTO.getUserId());

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
                    .timeout(60)
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

    @GetMapping(value = "/records")
    com.ethan.morephone.http.Response<Object> retrieveRecords(@RequestParam(value = "account_sid") String accountSid,
                                                              @RequestParam(value = "auth_token") String authToken,
                                                              @RequestParam(value = "phone_number") String phoneNumber) {
        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(phoneNumber);
        if (phoneNumberDTO != null) {
            if (phoneNumberDTO.getPool()) {
                accountSid = Constants.ACCOUNT_SID;
                authToken = Constants.AUTH_TOKEN;
            }

            Twilio.init(accountSid, authToken);
            List<com.ethan.morephone.twilio.model.Record> records = new ArrayList<>();

            ResourceSet<Recording> recordings = new com.twilio.rest.api.v2010.account.RecordingReader(accountSid)
                    .setDateCreated(Range.greaterThan(new DateTime(phoneNumberDTO.getCreatedAt())))
                    .read();
            if (recordings != null) {
                for (com.twilio.rest.api.v2010.account.Recording recording : recordings) {
                    Call call = new CallFetcher(accountSid, recording.getCallSid()).fetch();

                    if (call != null && call.getTo().equals(phoneNumber)) {
                        records.add(new com.ethan.morephone.twilio.model.Record(
                                recording.getSid(),
                                recording.getAccountSid(),
                                recording.getCallSid(),
                                call.getFrom(),
                                recording.getDuration(),
                                recording.getDateCreated().toString(Constants.FORMAT_DATE),
                                recording.getApiVersion(),
                                recording.getDateUpdated().toString(Constants.FORMAT_DATE),
                                recording.getStatus().toString(),
                                recording.getSource().name(),
                                recording.getChannels(),
                                recording.getPrice().toString(),
                                recording.getPriceUnit(),
                                recording.getUri()
                        ));
                    }
                }
            }

            if (!records.isEmpty()) {
                return new com.ethan.morephone.http.Response<>(records, HTTPStatus.OK);
            }

        }
        return new com.ethan.morephone.http.Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);

    }


    @GetMapping(value = "/logs")
    com.ethan.morephone.http.Response<Object> retrieveCallLogs(@RequestParam(value = "account_sid") String accountSid,
                                                               @RequestParam(value = "auth_token") String authToken,
                                                               @RequestParam(value = "phone_number") String phoneNumber,
                                                               @RequestParam(value = "page_incoming") String pageIncoming,
                                                               @RequestParam(value = "page_outgoing") String pageOutgoing) {

        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findByPhoneNumber(phoneNumber);
        if (phoneNumberDTO != null) {
            if (phoneNumberDTO.getPool()) {
                accountSid = Constants.ACCOUNT_SID;
                authToken = Constants.AUTH_TOKEN;
            }

            Twilio.init(accountSid, authToken);

            List<CallDTO> calls = new ArrayList<>();

            CallReader callReaderIncoming = new CallReader(accountSid)
                    .setTo(new PhoneNumber(phoneNumber));

            callReaderIncoming.limit(Constants.LIMIT);

            Page<Call> callPageIncoming;
            if (com.ethan.morephone.utils.TextUtils.isEmpty(pageIncoming)) {
                callPageIncoming = callReaderIncoming.firstPage();
            } else {
                callPageIncoming = callReaderIncoming.getPage(pageIncoming);
            }

            List<Call> callsIncoming = callPageIncoming.getRecords();

            if (callsIncoming != null) {
                for (Call call : callsIncoming) {
                    calls.add(convertToDTO(call));
                }
            }

            CallReader callReaderOutgoing = new CallReader(accountSid)
                    .setFrom(new PhoneNumber(phoneNumber));

            callReaderOutgoing.limit(Constants.LIMIT);

            Page<Call> callPageOutgoing;
            if (com.ethan.morephone.utils.TextUtils.isEmpty(pageOutgoing)) {
                callPageOutgoing = callReaderOutgoing.firstPage();
            } else {
                callPageOutgoing = callReaderOutgoing.getPage(pageOutgoing);
            }

            List<Call> callsOutgoing = callPageOutgoing.getRecords();

            if (callsOutgoing != null) {
                for (Call call : callsOutgoing) {
                    calls.add(convertToDTO(call));
                }
            }

            Collections.sort(calls);

            ResourceCall resourceCall = new ResourceCall(calls,
                    callPageIncoming.getFirstPageUrl("api", null).contains("null") ? "" : callPageIncoming.getFirstPageUrl("api", null),
                    callPageIncoming.getNextPageUrl("api", null).contains("null") ? "" : callPageIncoming.getNextPageUrl("api", null),
                    callPageIncoming.getPreviousPageUrl("api", null).contains("null") ? "" : callPageIncoming.getPreviousPageUrl("api", null),
                    callPageIncoming.getUrl("api", null).contains("null") ? "" : callPageIncoming.getUrl("api", null),
                    callPageOutgoing.getFirstPageUrl("api", null).contains("null") ? "" : callPageIncoming.getFirstPageUrl("api", null),
                    callPageOutgoing.getNextPageUrl("api", null).contains("null") ? "" : callPageIncoming.getNextPageUrl("api", null),
                    callPageOutgoing.getPreviousPageUrl("api", null).contains("null") ? "" : callPageIncoming.getPreviousPageUrl("api", null),
                    callPageOutgoing.getUrl("api", null).contains("null") ? "" : callPageIncoming.getUrl("api", null),
                    callPageIncoming.getPageSize());

            if (!calls.isEmpty()) {
                Collections.sort(calls);
                return new com.ethan.morephone.http.Response<>(resourceCall, HTTPStatus.OK);
            }
        }

        return new com.ethan.morephone.http.Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
    }


    private CallDTO convertToDTO(Call call) {
        return new CallDTO(call.getSid(),
                call.getDateCreated().toString(Constants.FORMAT_DATE),
                call.getDateUpdated().toString(Constants.FORMAT_DATE),
                call.getParentCallSid(),
                call.getAccountSid(),
                call.getTo(),
                call.getFrom(),
                call.getToFormatted(),
                call.getFromFormatted(),
                call.getPhoneNumberSid(),
                call.getStatus().name(),
                call.getStartTime().toString(Constants.FORMAT_DATE),
                call.getEndTime().toString(Constants.FORMAT_DATE),
                call.getDuration(),
                call.getPrice() == null ? "" : call.getPrice().toString(),
                call.getPriceUnit().getCurrencyCode(),
                call.getDirection(),
                call.getAnsweredBy(),
                call.getApiVersion(),
                call.getAnnotation(),
                call.getForwardedFrom(),
                call.getGroupSid(),
                call.getCallerName(),
                call.getUri(),
                null

        );

    }
}

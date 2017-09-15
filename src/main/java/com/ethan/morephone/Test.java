package com.ethan.morephone;

import com.ethan.morephone.data.entity.message.MessageItem;
import com.ethan.morephone.twilio.fcm.FCM;
import com.ethan.morephone.twilio.model.CallDTO;
import com.ethan.morephone.twilio.model.ConversationModel;
import com.ethan.morephone.twilio.model.ResourceCall;
import com.ethan.morephone.utils.TextUtils;
import com.ethan.morephone.utils.Utils;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.Twilio;
import com.twilio.base.Page;
import com.twilio.base.ResourceSet;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.*;
import com.twilio.rest.api.v2010.account.call.Recording;
import com.twilio.rest.api.v2010.account.call.RecordingReader;
import com.twilio.twiml.*;
import com.twilio.type.PhoneNumber;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by truongnguyen on 7/25/17.
 */
public class Test {

//    private static final PhoneNumberService service;

    public static void main(String[] args) {
//        modifyApplication();
//        sendNotification("", "WHY");
//        getApplication();
//        token();
//        binding();
//        createApplication();
//        deletePhoneNumber();
//        application();
//        service = new PhoneNumberServiceIml();\
//        messageForward();
//        getRecord();
//        testTask();

//        getRecordData("AC1bb60516853a77bcf93ea89e4a7e3b45", "bb82a5d15eca8e8ae4171173ce150014", "+14152365339");
        retrieveCallLogs("AC588786f8b8b8a4ad83c5d576646ae764", "5767b6743ca34d734e1c94d694e72d03", "+15097616265", "", "");
//        countDown();
//        testTime();
//        getAccessToken();
    }


    private static void sendNotification(String title, String body) {
        //Just I am passed dummy information
        String tokenId = "ehO-dAIUbKY:APA91bGYpNmSqfUMidnnx4SxBcFNoa-80r_1-URTZDaudnUvv_kXLDkQl9MYgDCNkgFnukeKdug2rcOj_LmT_-gqDT6S3ztg73HOC9F-aQW07AwfzbNdj6Ddn2g_wUqg9TXsqg_yl_Aa";

        String server_key = "AAAANaqlCmY:APA91bGdQKmQNlZhqLTq31yXx36auQvc9I2xA0RB-VIgGhnN4haVdXllvWgFiRkzwJ8B_qVZ8eaJbqCTr-pqlKxbq0O4hWAcUpVga655rByPKOVSB0YnoA5t08DpiNG6uj-iAArs2bCv";

//Method to send Push Notification
        FCM.sendNotification(tokenId, server_key, title, body);
    }


    private static void application() {
        Twilio.init("AC1bb60516853a77bcf93ea89e4a7e3b45", "bb82a5d15eca8e8ae4171173ce150014");

        String applicationName = "Email";
        com.twilio.rest.api.v2010.account.Application application = null;

        ResourceSet<com.twilio.rest.api.v2010.account.Application> applications = new ApplicationReader("AC1bb60516853a77bcf93ea89e4a7e3b45").read();
        if (applications != null && applications.iterator().hasNext()) {
            application = applications.iterator().next();

            application = new ApplicationUpdater(application.getSid())
                    .setFriendlyName(applicationName)
                    .setVoiceUrl(Constants.VOICE_URL)
                    .setVoiceMethod(HttpMethod.POST)
                    .setSmsUrl(Constants.MESSAGE_URL)
                    .setSmsMethod(HttpMethod.POST)
                    .update();

        } else {

            application = new ApplicationCreator(
                    applicationName)
                    .setVoiceUrl(Constants.VOICE_URL)
                    .setVoiceMethod(HttpMethod.POST)
                    .setSmsUrl(Constants.MESSAGE_URL)
                    .setSmsMethod(HttpMethod.POST)
                    .create();

        }
    }


    private static void messageForward() {
        com.twilio.twiml.Message message = new com.twilio.twiml.Message.Builder()
                .to("+16193322905")
                .body(new Body("Hello, Mobile Monkey"))
                .build();

        MessagingResponse twiml = new MessagingResponse.Builder()
                .message(message)
                .build();

        try {
            Utils.logMessage(twiml.toXml());
        } catch (TwiMLException e) {
            e.printStackTrace();
        }
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
                    .number(new com.twilio.twiml.Number.Builder(to)
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
                    .timeout(30)
//                    .action("/record")
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

    @RequestMapping(value = "/record", method = RequestMethod.POST, produces = {"application/xml"})
    public String record(@RequestParam Map<String, String> allRequestParams) {
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

    public static void getRecord() {
        String accountSid = "AC1bb60516853a77bcf93ea89e4a7e3b45";
        Twilio.init(accountSid, "bb82a5d15eca8e8ae4171173ce150014");
//        ResourceSet<Recording> recordings = new RecordingReader("AC1bb60516853a77bcf93ea89e4a7e3b45").read();
//        if (recordings != null && recordings.iterator() != null) {
//            for (Recording recording : recordings) {
//                Utils.logMessage("RECORD: ");
//            }
//        }
        long start = System.currentTimeMillis();
//        ResourceSet<Call> calls = new CallReader(accountSid)
//                .setTo(new PhoneNumber("+14152365339"))
//                .limit(10)
//                .read();
//        if (calls != null && calls.iterator() != null) {
//            for (Call call : calls) {
//                ResourceSet<Recording> recordings = new RecordingReader(accountSid, call.getSid()).read();
//                if (recordings != null && recordings.iterator() != null) {
//                    for (Recording recording : recordings) {;
//                        Utils.logMessage("RECORDING: " + recording.getUri());
//                    }
//                }
//            }
//        }

        ResourceSet<Recording> recordings = new RecordingReader(accountSid).read();
        if (recordings != null && recordings.iterator() != null) {
            for (Recording recording : recordings) {
                Utils.logMessage("RECORDING: " + recording.getUri());
            }
        }

        long duration = System.currentTimeMillis() - start;
        Utils.logMessage("TOTAL: " + duration / 1000);
    }

    private static ScheduledExecutorService mExecutorService;
    private static ScheduledFuture<?> mScheduleFuture;

    private static void testTask() {
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        stopDonutProgressUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.schedule(
                    new Runnable() {
                        @Override
                        public void run() {
                            Utils.logMessage("START " + new Date());
                        }
                    },
                    10,
                    TimeUnit.SECONDS);
        }
    }

    private static void stopDonutProgressUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }


    private static void loadConversationMessage(String accountSid, String authToken, String phoneNumber) {
        Twilio.init(accountSid, authToken);

        HashMap<String, List<MessageItem>> mArrayMap = new HashMap<>();

        ResourceSet<com.twilio.rest.api.v2010.account.Message> messagesIncoming = new MessageReader(accountSid).setTo(new PhoneNumber(phoneNumber)).read();

        if (messagesIncoming != null) {
            mArrayMap.putAll(executeData(messagesIncoming, true));
        }

        ResourceSet<com.twilio.rest.api.v2010.account.Message> messagesOutgoing = new MessageReader(accountSid).setFrom(new PhoneNumber(phoneNumber)).read();

        if (messagesOutgoing != null) {
            mArrayMap.putAll(executeData(messagesOutgoing, false));
        }

        List<ConversationModel> mConversationModels = new ArrayList<>();
        for (Map.Entry entry : mArrayMap.entrySet()) {
            List<MessageItem> items = mArrayMap.get(entry.getKey());
            if (items != null && !items.isEmpty()) {
                Collections.sort(items);
                String dateCreated = items.get(items.size() - 1).dateCreated;
                Utils.logMessage("DATE CREATED: " + dateCreated);
                mConversationModels.add(new ConversationModel(entry.getKey().toString(), dateCreated, items));
            }
        }

        for (ConversationModel conversationModel : mConversationModels) {
            Utils.logMessage(conversationModel.mPhoneNumber + " DATE " + conversationModel.mDateCreated);
            for (MessageItem message : conversationModel.mMessageItems) {
                Utils.logMessage(message.body);
            }

            Utils.logMessage("**********************************");
        }
    }

    private static HashMap<String, List<com.twilio.rest.api.v2010.account.Message>> mArrayMap = new HashMap<>();
    private static List<ConversationModel> mConversationModels = new ArrayList<>();

    private static HashMap<String, List<MessageItem>> executeData(ResourceSet<com.twilio.rest.api.v2010.account.Message> messageItems, boolean isComing) {
        HashMap<String, List<MessageItem>> mArrayMap = new HashMap<>();
        if (isComing) {
            for (com.twilio.rest.api.v2010.account.Message messageItem : messageItems) {
                if (messageItem.getStatus() != null && messageItem.getStatus() == com.twilio.rest.api.v2010.account.Message.Status.RECEIVED) {
                    if (mArrayMap.containsKey(messageItem.getFrom().toString())) {
                        mArrayMap.get(messageItem.getFrom().toString()).add(convertMessage(messageItem));
                    } else {
                        List<MessageItem> items = new ArrayList<>();
                        items.add(convertMessage(messageItem));
                        mArrayMap.put(messageItem.getFrom().toString(), items);
                    }
                }

            }
        } else {
            for (com.twilio.rest.api.v2010.account.Message messageItem : messageItems) {
                if (messageItem.getStatus() != null && messageItem.getStatus() == com.twilio.rest.api.v2010.account.Message.Status.DELIVERED) {
                    if (mArrayMap.containsKey(messageItem.getTo())) {
                        mArrayMap.get(messageItem.getTo()).add(convertMessage(messageItem));
                    } else {
                        List<MessageItem> items = new ArrayList<>();
                        items.add(convertMessage(messageItem));
                        mArrayMap.put(messageItem.getTo(), items);
                    }
                }
            }
        }
        return mArrayMap;
    }

    private static MessageItem convertMessage(com.twilio.rest.api.v2010.account.Message message) {
        String formatDate = "E, d MMM yyyy HH:mm:ss Z";
        return new MessageItem(message.getSid(),
                message.getDateCreated() == null ? "" : message.getDateCreated().toString(formatDate),
                message.getDateUpdated() == null ? "" : message.getDateUpdated().toString(formatDate),
                message.getDateSent() == null ? "" : message.getDateSent().toString(formatDate),
                message.getAccountSid(),
                message.getTo(),
                message.getFrom() == null ? "" : message.getFrom().toString(),
                message.getMessagingServiceSid(),
                message.getBody(),
                message.getStatus() == null ? "" : message.getStatus().name(),
                message.getNumSegments(),
                message.getNumMedia(),
                message.getDirection() == null ? "" : message.getDirection().toString(),
                message.getApiVersion(),
                message.getPrice() == null ? "" : message.getPrice().toString(),
                message.getPriceUnit() == null ? "" : message.getPriceUnit().toString(),
                String.valueOf(message.getErrorCode()),
                message.getErrorMessage(),
                message.getUri(),
                null
        );
    }

    public static Date getDateMessage(String date) {
//        2017-08-04T07:57:46.000Z
        SimpleDateFormat in = new SimpleDateFormat("yyyy-MMM-dEHH:mm:ssZ, d MMM yyyy HH:mm:ss Z");

        try {
            Date time = in.parse(date);
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void getRecordData(String accountSid, String authToken, String phoneNumber) {
        List<com.ethan.morephone.twilio.model.Record> records = new ArrayList<>();
        Twilio.init(accountSid, authToken);
        ResourceSet<com.twilio.rest.api.v2010.account.Recording> recordings = new com.twilio.rest.api.v2010.account.RecordingReader(accountSid).read();
        if (recordings != null) {
            for (com.twilio.rest.api.v2010.account.Recording recording : recordings) {
                Utils.logMessage("RECORD: " + recording.getDuration());
                Call call = new CallFetcher(accountSid, recording.getCallSid()).fetch();

                if (call != null && call.getTo().equals(phoneNumber)) {
                    Utils.logMessage("COME");
                    records.add(new com.ethan.morephone.twilio.model.Record(
                            recording.getSid(),
                            recording.getAccountSid(),
                            recording.getCallSid(),
                            phoneNumber,
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

        for (com.ethan.morephone.twilio.model.Record record : records) {
            Utils.logMessage("KQ: " + record.duration);
        }
    }

    private static final int LIMIT = 5;

    private static void retrieveCallLogs(String accountSid, String authToken, String phoneNumber, String pageIncoming, String pageOutgoing) {
        Twilio.init(accountSid, authToken);

        List<CallDTO> calls = new ArrayList<>();

        CallReader callReaderIncoming = new CallReader(accountSid)
                .setTo(new PhoneNumber(phoneNumber));

        callReaderIncoming.limit(LIMIT);

        Page<Call> callPageIncoming;
        if (TextUtils.isEmpty(pageIncoming)) {
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

        callReaderIncoming.limit(LIMIT);

        Page<Call> callPageOutgoing;
        if (TextUtils.isEmpty(pageOutgoing)) {
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
                callPageIncoming.getFirstPageUrl("api", null),
                callPageIncoming.getNextPageUrl("api", null),
                callPageIncoming.getPreviousPageUrl("api", null),
                callPageIncoming.getUrl("api", null),
                callPageOutgoing.getFirstPageUrl("api", null),
                callPageOutgoing.getNextPageUrl("api", null),
                callPageOutgoing.getPreviousPageUrl("api", null),
                callPageOutgoing.getUrl("api", null),
                callPageIncoming.getPageSize());

        for (CallDTO call : calls) {
            Utils.logMessage("CALL: " + call.from + " DATE: " + call.dateCreated);
        }
    }


    private static CallDTO convertToDTO(Call call) {
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

    public static int SECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;

    public static void countDown() {
        //milliseconds
        long different = SECONDS_IN_A_DAY;

        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    //in one account sid will lost message
    private static void deleteAllDataPhoneNumber(String accountSid, String authToken, String phoneNumber) {
        Twilio.init(accountSid, authToken);
        //Call
    }

    private static void testTime() {
        SimpleDateFormat out = new SimpleDateFormat("MMM d, HH:mm a");
        Date time = new Date(1503055060956L);
        Utils.logMessage(out.format(time));
    }

    private static void getAccessToken() {
        try {
            com.mashape.unirest.http.HttpResponse<String> response = Unirest.post("https://coderdaudat.auth0.com/oauth/token")
                    .header("content-type", "application/json")
                    .body("{\"grant_type\":\"authorization_code\",\"client_id\": \"g-Ta-6ukhBXUsQHjvezs5u5G_Gi6DoW5\",\"client_secret\": \"zzX9ZAHTFb0BhXyb51OhpSmowTL-M2yAuzBiBiTzWwvf91fo5GZIlIWqo0ULHhIp\",\"code\": \"YOUR_AUTHORIZATION_CODE\",\"redirect_uri\": \"https://YOUR_APP/callback\"}")
                    .asString();

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

}

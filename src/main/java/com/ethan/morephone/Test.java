package com.ethan.morephone;

import com.ethan.morephone.twilio.fcm.FCM;
import com.ethan.morephone.utils.Utils;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.ApplicationCreator;
import com.twilio.rest.api.v2010.account.ApplicationReader;
import com.twilio.rest.api.v2010.account.ApplicationUpdater;
import com.twilio.rest.api.v2010.account.call.Recording;
import com.twilio.rest.api.v2010.account.call.RecordingReader;
import com.twilio.twiml.*;
import org.apache.http.util.TextUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
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
        testTask();
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
        Utils.logMessage("TOTAL: " + duration/1000);
    }

    private static ScheduledExecutorService mExecutorService;
    private static ScheduledFuture<?> mScheduleFuture;

    private static void testTask(){
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        stopDonutProgressUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.schedule(
                    new Runnable() {
                        @Override
                        public void run() {
                            Utils.logMessage("START "+ new Date());
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
}

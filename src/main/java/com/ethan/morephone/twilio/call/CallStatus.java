package com.ethan.morephone.twilio.call;

/**
 * Created by truongnguyen on 8/3/17.
 */
public enum CallStatus {

    QUEUED("queued"),
    INITIATED("initiated"),
    RINGING("ringing"),
    IN_PROGRESS("in-progress"),
    COMPLETED("completed"),
    BUSY("busy"),
    NO_ANSWER("no-answer"),
    CANCELED("canceled"),
    FAILED("failed");

    private String status;

    CallStatus(String status) {
        this.status = status;
    }

    public String callStatus() {
        return status;
    }

    public static CallStatus getCallStatus(String status) {
        for (CallStatus callStatus : CallStatus.values()) {
            if (callStatus.status.equalsIgnoreCase(status)) {
                return callStatus;
            }
        }
        return null;
    }

}

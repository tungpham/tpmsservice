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

    public static CallStatus getCallStatus(String name) {
        return getEnumFromString(CallStatus.class, name);
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if( c != null && string != null ) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch(IllegalArgumentException ex) {
            }
        }
        return null;
    }

}

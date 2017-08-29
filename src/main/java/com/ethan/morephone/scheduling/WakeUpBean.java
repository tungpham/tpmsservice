package com.ethan.morephone.scheduling;

import org.springframework.stereotype.Component;

/**
 * Created by truongnguyen on 8/29/17.
 */
@Component("wakeUpBean")
public class WakeUpBean {

    public void printMessage() {
        System.out.println("More Phone never die");
    }

}

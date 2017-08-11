package com.ethan.morephone.twilio;

import com.ethan.morephone.utils.Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by truongnguyen on 8/8/17.
 */
@RestController
@RequestMapping(value = "/api/v1/scheduler")
public class SchedulerService {

    @PostMapping(value = "/job")
    public String scheduler() {
        Utils.logMessage("WAKE UP NOW");
        return "OKAY";
    }
}

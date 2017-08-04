package com.ethan.morephone.test;

import com.ethan.morephone.utils.TextUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by truongnguyen on 8/4/17.
 */
@RestController
@RequestMapping(value = "/api/v1/test")
public class TestService {
    @PostMapping(value = "/call")
    public String callPhone(@RequestParam(value = "client") String client) {
        if(TextUtils.isEmpty(client)){
            return "Do not empty";
        }else {
            return client;
        }
    }
}

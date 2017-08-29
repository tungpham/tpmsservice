package com.ethan.morephone.quartz;

import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.utils.Utils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

/**
 * Created by truongnguyen on 8/29/17.
 */
public class ScheduledJob extends QuartzJobBean {

    private PhoneNumberService phoneNumberService;

    @Autowired
    public void setPhoneNumberService(PhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    @Override
    protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

        List<PhoneNumberDTO> phoneNumbersUnavailable = phoneNumberService.findPoolPhoneNumberUnavailable();
        if (phoneNumbersUnavailable != null) {
            for (PhoneNumberDTO dto : phoneNumbersUnavailable) {
                if(dto.getExpire() < System.currentTimeMillis()){
                    dto.setExpire(0);
                    dto.setUserId("");

                    phoneNumberService.update(dto);
                    Utils.logMessage("Check Expire Phone Number");
                }
            }
        }
    }


}

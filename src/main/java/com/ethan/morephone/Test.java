package com.ethan.morephone;

import com.ethan.morephone.data.entity.application.Application;
import com.ethan.morephone.data.entity.application.Applications;
import com.ethan.morephone.data.network.ApiManager;
import com.ethan.morephone.utils.Utils;

/**
 * Created by truongnguyen on 7/25/17.
 */
public class Test {

    public static void main(String[] args){
        Applications applications = ApiManager.getApplications();
        if(applications != null && applications.applications != null) {
            for (Application application : applications.applications){
                Utils.logMessage(application.friendly_name);
                Utils.logMessage(application.sid);
            }
        }
    }
}

package com.ethan.morephone.schedule;

import com.ethan.morephone.test.ApiMorePhone;
import com.ethan.morephone.utils.Utils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by truongnguyen on 8/8/17.
 */
public class SchedulerMain {

    public static void main(String[] args) throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail jobDetail = newJob(HelloJob.class).build();

        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(repeatSecondlyForever(60))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public static class HelloJob implements Job {

//        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            ApiMorePhone.scheduler(new Callback<String>() {
                public void onResponse(Call<String> call, Response<String> response) {
                    Utils.logMessage("SUCCESS: " + response.body());
                }

                public void onFailure(Call<String> call, Throwable throwable) {
                    Utils.logMessage("FAIL");
                }
            });
        }
    }
}

package com.ethan.morephone.schedule;

/**
 * Created by truongnguyen on 8/8/17.
 */
public class SchedulerMain {

//    public static void main(String[] args) throws Exception {
//        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//
//        scheduler.start();
//
//        JobDetail jobDetail = newJob(HelloJob.class).build();
//
//        Trigger trigger = newTrigger()
//                .startNow()
//                .withSchedule(repeatSecondlyForever(60))
//                .build();
//
//        scheduler.scheduleJob(jobDetail, trigger);
//
//        Utils.logMessage("START SCHEDULER");
//    }
//
//    public static class HelloJob implements Job {
//
////        @Override
//        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//            ApiMorePhone.scheduler(new Callback<String>() {
//                public void onResponse(Call<String> call, Response<String> response) {
//                    Utils.logMessage("SUCCESS: ");
//                }
//
//                public void onFailure(Call<String> call, Throwable throwable) {
//                    Utils.logMessage("FAIL");
//                }
//            });
//        }
//    }
}

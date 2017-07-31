package com.ethan.morephone.data.network;

import com.ethan.morephone.Constants;
import com.ethan.morephone.data.entity.application.Application;
import com.ethan.morephone.data.entity.application.Applications;
import com.ethan.morephone.data.entity.phonenumbers.IncomingPhoneNumber;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by truongnguyen on 7/25/17.
 */
public class ApiManager {
    //    private static final String BASE_URL = "https://raw.githubusercontent.com/tungpham/tpmsservices/";
    private static final String BASE_URL = "https://api.twilio.com/2010-04-01/";

    private static final int PAGE_SIZE = 50;

    private static ApiPath mApiPath;

    private static volatile Retrofit mRetrofit;

    private static final String TAG = ApiManager.class.getSimpleName();

    //Singleton for Retrofit
    private static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            synchronized (ApiManager.class) {
                if (mRetrofit == null) {

                    //Create cache
//                    File file = new File(context.getCacheDir(), "response");

                    //Set log
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    boolean isLog = true;
                    logging.setLevel(isLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);


                    //Add log and set time out
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .authenticator(new Authenticator() {
                                @Override
                                public Request authenticate(Route route, Response response) throws IOException {
                                    String credential = Credentials.basic(Constants.TWILIO_ACCOUNT_SID, Constants.TWILIO_AUTH_TOKEN);
                                    return response.request().newBuilder()
                                            .header("Authorization", credential)
                                            .build();
                                }
                            })
                            .readTimeout(60, TimeUnit.SECONDS)
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .addInterceptor(logging)
                            .build();
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient).build();
                }
            }
        }
        return mRetrofit;
    }


    //Singleton for ApiPath
    private static ApiPath getApiPath() {
        if (mApiPath == null) {
            synchronized (ApiManager.class) {
                if (mApiPath == null) {
                    mApiPath = getRetrofit().create(ApiPath.class);
                }
            }
        }
        return mApiPath;
    }


    /*----------------------------------------- APPLICATIONS -----------------------------------------*/

    public static Applications getApplications() {
        Call<Applications> call = getApiPath().getApplications(Constants.TWILIO_ACCOUNT_SID);
        try {
            Applications applications = call.execute().body();
            return applications;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Application modifyApplication(String applicationSid,
                                                String voiceUrl,
                                                String voiceMethod,
                                                String smsUrl,
                                                String smsMethod) {
        Call<Application> call = getApiPath().modifyApplication(Constants.TWILIO_ACCOUNT_SID, applicationSid, voiceUrl, voiceMethod, smsUrl, smsMethod);
        try {
            Application application = call.execute().body();
            return application;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Application createApplication(String accountSid,
                                                String friendlyName,
                                                String voiceUrl,
                                                String voiceMethod,
                                                String smsUrl,
                                                String smsMethod) {
        Call<Application> call = getApiPath().createApplication(accountSid, friendlyName, voiceUrl, voiceMethod, smsUrl, smsMethod);
        try {
            Application application = call.execute().body();
            return application;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*-----------------------------------------INCOMING PHONE NUMBERS-----------------------------------------*/

    public static IncomingPhoneNumber modifyIncomingPhoneNumber(String incomingPhoneNumberSid,
                                                             String smsApplicationSid,
                                                             String smsMethod,
                                                             String voiceApplicationSid,
                                                             String voiceMethod) {
        Call<IncomingPhoneNumber> call = getApiPath().modifyIncomingPhoneNumber(Constants.TWILIO_ACCOUNT_SID, incomingPhoneNumberSid, smsApplicationSid, smsMethod, voiceApplicationSid, voiceMethod);
        try {
            IncomingPhoneNumber incomingPhoneNumber = call.execute().body();
            return incomingPhoneNumber;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

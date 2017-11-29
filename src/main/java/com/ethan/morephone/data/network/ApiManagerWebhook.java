package com.ethan.morephone.data.network;

import com.ethan.morephone.data.entity.message.MessageItem;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by truongnguyen on 7/25/17.
 */
public class ApiManagerWebhook {
    //    private static final String BASE_URL = "https://raw.githubusercontent.com/tungpham/tpmsservices/";
    private static final String BASE_URL = "https://tpmsreact.uptind.com/";

    private static final int PAGE_SIZE = 50;

    private static ApiPathWebhook mApiPath;

    private static volatile Retrofit mRetrofit;

    private static final String TAG = ApiManagerWebhook.class.getSimpleName();

    //Singleton for Retrofit
    private static Retrofit getRetrofit() {
        if (mRetrofit == null) {

            //Create cache
//                    File file = new File(context.getCacheDir(), "response");

            //Set log
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            boolean isLog = true;
            logging.setLevel(isLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);


            //Add log and set time out
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .authenticator(new Authenticator() {
//                        @Override
//                        public Request authenticate(Route route, Response response) throws IOException {
//                            String credential = Credentials.basic(accountSid, authToken);
//                            return response.request().newBuilder()
//                                    .header("Authorization", credential)
//                                    .build();
//                        }
//                    })
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient).build();


        }
        return mRetrofit;
    }


    //Singleton for ApiPath
    private static ApiPathWebhook getApiPath() {
        if (mApiPath == null) {
//            synchronized (ApiManager.class) {
//                if (mApiPath == null) {
            mApiPath = getRetrofit().create(ApiPathWebhook.class);
//                }
//            }
        }
        return mApiPath;
    }


    /*----------------------------------------- APPLICATIONS -----------------------------------------*/

    public static void pushMessage(MessageItem message) {
        getApiPath().pushMessage(message);
    }


}

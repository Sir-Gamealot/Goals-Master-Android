package com.goalsmaster.goalsmaster.rest;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
//import com.goalsmaster.goalsmaster.activities.LoginActivity;
//import com.goalsmaster.goalsmaster.events.LogOffRequest;
//import com.goalsmaster.goalsmaster.utils.SecurityUtils;
import com.goalsmaster.goalsmaster.utils.ServerSettings;

import java.lang.reflect.Type;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by tudor on 5/6/2017.
 */

public class RestApi {
    private static Retrofit HANDLE;
    private static TaskApi TASK_API;

    private static synchronized Retrofit getRetrofit(final Context context) {
        if(null == HANDLE) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    /*.addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            long callerId = 0;
                            long timestamp = System.currentTaskMillis();
                            String hash = "";
                            if (LoginActivity.isLoggedIn(context)) {
                                callerId = LoginActivity.getLoggedInUserId();
                                String token = LoginActivity.getLoggedInUserToken();
                                String stamp = String.format(Locale.US, "SALT%dSALT", timestamp);
                                hash = SecurityUtils.SHA256(stamp.getBytes(), token);
                                Log.d(TAG, "timestamp = " + String.valueOf(timestamp));
                                Log.d(TAG, "stamp = " + stamp);
                                Log.d(TAG, "hash = " + hash);
                                Log.d(TAG, "token = " + token);
                            }
                            Request request = chain.request().newBuilder()
                                    .addHeader("apikey", "THE KEY")
                                    .addHeader("timestamp", String.valueOf(timestamp))
                                    .addHeader("hash", hash)
                                    .addHeader("callerId", String.valueOf(callerId))
                                    .build();
                            Response r = chain.proceed(request);
                            if(r.code() == 401) {
                                EventBus.getDefault().post(new LogOffRequest());
                            }
                            return r;
                        }
                    })*/
                    .build();

            GsonBuilder gson = new GsonBuilder();

            gson.setDateFormat("yyyy-MM-dd HH:mm:ss")
                    //.registerTypeAdapter(Date.class, new DateSerializer())
                    //.registerTypeAdapter(Date.class, new DateDeserializer())
                    .serializeNulls()
                    .setLenient();

            HANDLE = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson.create()))
                    .client(client)
                    .baseUrl(ServerSettings.BASE_PATH)
                    .build();

            TASK_API = getRetrofit(context).create(TaskApi.class);
        }

        return HANDLE;
    }

    public static synchronized TaskApi getTaskApi(Context context) {
        Log.d(TAG, "$$$ Create task api");
        getRetrofit(context);
        return TASK_API;
    }

    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd",
            "EEE MMM dd HH:mm:ss z yyyy",
            "HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss aaa",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'",
            "MMM d',' yyyy H:mm:ss a"
    };

    private static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement jsonElement, Type typeOF, JsonDeserializationContext context) throws JsonParseException {
            return new Date();
            /*for (String format : DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(format, Locale.US).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                }
            }
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                    + "\". Supported formats: \n" + Arrays.toString(DATE_FORMATS));*/
        }
    }

    private static class DateSerializer implements JsonSerializer<Date>  {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            /*Log.d(TAG, "$$$ serialize");
            String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(src);
            return new JsonPrimitive(s);*/
            return new JsonPrimitive("1999-01-01 01:01:01");
        }
    }

}

package com.example.dany198x.myapplication;


import com.example.dany198x.myapplication.constant.Constant;

import org.json.JSONObject;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyService {
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType text = MediaType.parse("text/plain;charset=utf-8");
    private static MediaType jsonType = MediaType.parse("application/json;charset=utf-8");
    public interface Callback{
        void callback(String jsonString);
    }
    private static String baseUrl = Constant.baseUrl + "/goldendoctor";

    public static void sendGetRequest(String url, JSONObject object, final Callback callback) {
        try {
//            RequestBody body = RequestBody.create(text, object.toString());
            final Request request = new Request.Builder().get().url(baseUrl + url).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    System.out.println(e);
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    callback.callback(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPostRequest(String url, JSONObject object, final Callback callback) {
        try {
            RequestBody body = RequestBody.create(text, object.toString());
            final Request request = new Request.Builder().post(body).url(baseUrl + url).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    System.out.println(e);
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    callback.callback(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPostRequest1(String url, JSONObject object, final Callback callback) {
        try {
            RequestBody body = RequestBody.create(jsonType, object.toString());
            final Request request = new Request.Builder().post(body).url(url).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    System.out.println(e);
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    callback.callback(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

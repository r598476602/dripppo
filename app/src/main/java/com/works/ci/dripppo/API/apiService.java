package com.works.ci.dripppo.API;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jason on 2016/10/28.
 */

public class apiService {
    private OkHttpClient client;
    public apiService(){
        client = new OkHttpClient();
    }
    public String get(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

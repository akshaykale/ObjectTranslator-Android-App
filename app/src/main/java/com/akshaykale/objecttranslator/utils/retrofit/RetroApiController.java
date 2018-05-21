package com.akshaykale.objecttranslator.utils.retrofit;

import com.akshaykale.objecttranslator.models.MEinsteinResponse;
import com.akshaykale.objecttranslator.utils.LocalDataStorageManager;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroApiController implements Callback<MEinsteinResponse>{

    private IRetroObjectResponseListener listener;

    public final static String salesforcetoken = LocalDataStorageManager.getInstance().key();

    public RetroApiController(IRetroObjectResponseListener listener) {
        this.listener = listener;
    }

    private final String BASE_URL = "https://api.einstein.ai/v2/vision/";
    private final String TAG = this.getClass().getSimpleName();

    public void start(String url) {
        if (listener == null)
            return;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer "+LocalDataStorageManager.getInstance().key()).build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        RetroEinsteinApiInterface api = retrofit.create(RetroEinsteinApiInterface.class);

        Map<String,String> map = new HashMap<>();
        map.put("sampleLocation", url);
        map.put("modelId", "GeneralImageClassifier");

        Call<MEinsteinResponse> call = api.loadApi(RequestBody.create(MediaType.parse("text/plain"), url),
                RequestBody.create(MediaType.parse("text/plain"), "GeneralImageClassifier"));
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call<MEinsteinResponse> call, Response<MEinsteinResponse> response) {
        if(response.isSuccessful()) {
            MEinsteinResponse einsteinResponse = response.body();
            listener.onRetroApiSuccess(einsteinResponse);
        } else {
            System.out.println(response.errorBody());
            listener.onRetroApiFailed(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<MEinsteinResponse> call, Throwable t) {
        System.out.println("");
        listener.onRetroApiFailed(null);
    }
}

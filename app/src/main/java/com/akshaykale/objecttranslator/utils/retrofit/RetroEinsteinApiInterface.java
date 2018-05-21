package com.akshaykale.objecttranslator.utils.retrofit;

import com.akshaykale.objecttranslator.models.MEinsteinResponse;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetroEinsteinApiInterface {

    @POST("predict")
    @Multipart
    //@Headers({"Authorization: Bearer "+RetroApiController.salesforcetoken})
    //Call<MEinsteinResponse> loadApi( @FieldMap Map<String,String> params);
    Call<MEinsteinResponse> loadApi( @Part ("sampleLocation") RequestBody sampleLocation,
                                     @Part ("modelId") RequestBody modelId);


}

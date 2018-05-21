package com.akshaykale.objecttranslator.utils.retrofit;

public interface IRetroObjectResponseListener<M> {
    void onRetroApiSuccess(M response);
    void onRetroApiFailed(M error);
}

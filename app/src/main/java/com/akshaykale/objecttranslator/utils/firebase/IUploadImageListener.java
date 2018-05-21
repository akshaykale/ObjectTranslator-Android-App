package com.akshaykale.objecttranslator.utils.firebase;

public interface IUploadImageListener {

    void onImageUploadSuccess(String path);
    void onImageUploadFailed();
}

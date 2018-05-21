package com.akshaykale.objecttranslator.utils.translate;

import com.akshaykale.objecttranslator.models.MWord;

public interface TranslateCallback {
    void onSuccess(MWord translatedText);
    void onFailure();
}

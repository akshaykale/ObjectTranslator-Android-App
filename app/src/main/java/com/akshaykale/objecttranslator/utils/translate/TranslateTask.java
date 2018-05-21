package com.akshaykale.objecttranslator.utils.translate;

import android.os.AsyncTask;

import com.akshaykale.objecttranslator.models.MWord;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class TranslateTask extends AsyncTask<String, String, String> {

    String textToTranslate;
    String targetLanguage;
    TranslateCallback callback;

    public TranslateTask(String textToTranslate, String targetLanguage, TranslateCallback callback) {
        this.textToTranslate = textToTranslate;
        this.targetLanguage = targetLanguage;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {

        translate(textToTranslate, targetLanguage, callback);

        return null;
    }

    private void translate(String textToTranslate, String targetLanguage, TranslateCallback callback) {
        try {
            TranslateOptions options = TranslateOptions.newBuilder()
                    .setApiKey("AIzaSyAb0hzvIy89Irmij0FLUNhYhrPJmaYXxXg")
						.build();
            Translate trService = options.getService();
            Translation translation = trService.translate(textToTranslate, Translate.TranslateOption.targetLanguage(targetLanguage));
            MWord word = new MWord();
            word.langFrom = "en";
            word.langTo = targetLanguage.toLowerCase();
            word.wordFrom = textToTranslate.toLowerCase();
            word.wordTo = translation.getTranslatedText();
            callback.onSuccess(word);
        } catch (Exception e) {
            callback.onFailure();
        }
    }
}

package com.akshaykale.objecttranslator.models;

public class MWord extends ModelsBase{

    public String wordFrom;
    public String wordTo;
    public String langFrom;
    public String langTo;

    public MWord() {
        //langFrom = LocalDataStorageManager.getInstance().translationFrom();
        //langTo = LocalDataStorageManager.getInstance().translationTo();
    }

}

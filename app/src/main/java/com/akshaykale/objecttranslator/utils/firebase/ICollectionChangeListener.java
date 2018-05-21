package com.akshaykale.objecttranslator.utils.firebase;

public interface ICollectionChangeListener<M> {
    void onDocumentAdded(M model);
    void onDocumentModified(M model);
    void onDocumentRemoved(M model);
}

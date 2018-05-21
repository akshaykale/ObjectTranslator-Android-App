package com.akshaykale.objecttranslator.utils.firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.akshaykale.objecttranslator.models.MUser;
import com.akshaykale.objecttranslator.models.MWord;
import com.akshaykale.objecttranslator.utils.LocalDataStorageManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class FireDataManager {

    private final String TAG = getClass().getSimpleName();

    private final String key_timestamp = "_ts_utc";
    private final DatabaseReference mDatabase;

    /*FireStore DB*/
    private FirebaseFirestore firestoreDb;
    private FirebaseStorage storage;

    private CollectionReference userCollReference;
    private StorageReference imagesStorageReference;
    private CollectionReference wordCollReference;

    public FireDataManager() {
        this.firestoreDb = FirebaseFirestore.getInstance();

        userCollReference = firestoreDb.collection("users");
        wordCollReference = firestoreDb.collection("words");
        this.storage = FirebaseStorage.getInstance();
        imagesStorageReference = storage.getReference().child("images");

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    /*public CollectionReference getUserTripsCollectionRef() {
        return userCollReference //user collection
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()) //user document
                .collection(key_coll_trips); //collection of trips
    }
*/

    public CollectionReference getWordCollReference() {
        return wordCollReference;
    }

    public void saveUser(final FirebaseUser user) {
        final DocumentReference userRef = userCollReference.document(user.getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // User exists. Change the values
                        userRef.update("last_login", System.currentTimeMillis());
                    } else {
                        _addUser(user);
                    }
                } else {
                    _addUser(user);
                }
            }
        });
    }

    private void _addUser(FirebaseUser user) {
        //construct user
        MUser mUser = new MUser();
        mUser.display_name = user.getDisplayName();
        mUser.email = user.getEmail();
        mUser.id = user.getUid();
        mUser.photo_url = String.valueOf(user.getPhotoUrl());

        DocumentReference documentReference = userCollReference.document(mUser.id);
        documentReference.set(mUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed: ==" + e.getMessage());
            }
        });
    }

    public void getKey() {
        mDatabase.child("key").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getValue(String.class);
                LocalDataStorageManager.getInstance().key(key);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"error");
            }
        });
    }

    public void addWord(MWord word) {
        wordCollReference.add(word).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Word saved successfully!");
            }
        });
    }

    public CollectionReference getTranslateApiCollReference() {
        return firestoreDb.collection("translation_api");
    }

    public void uploadImage(Bitmap bitmap, String postId, final IUploadImageListener listener) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference imageRef = imagesStorageReference.child(postId);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        UploadTask uploadTask = imageRef.putBytes(data, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG,"Upload failed"+exception.getMessage());
                listener.onImageUploadFailed();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,"Upload Success  Byter:"+taskSnapshot.getBytesTransferred()+"    url:"+taskSnapshot.getDownloadUrl().toString());
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.onImageUploadSuccess(downloadUrl.toString());
            }
        });
    }

    public void addCollectionChangeListener(CollectionReference reference, final ICollectionChangeListener listener, final Class classObj, Query.Direction ORDER_BY, HashMap<String, String> conditions) {
        if (listener == null)
            return;
        if (conditions != null) {
            for (HashMap.Entry<String, String> entry : conditions.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                reference.whereEqualTo(key, value);
            }
        }
        if (ORDER_BY !=null){
            reference.orderBy(key_timestamp, ORDER_BY);
        }
        reference
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        if (snapshots != null) {
                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                DocumentSnapshot snapshot = dc.getDocument();
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d(TAG, "New city: " + snapshot.getData());
                                        listener.onDocumentAdded(snapshot.toObject(classObj));
                                        break;
                                    case MODIFIED:
                                        Log.d(TAG, "Modified city: " + snapshot.getData());
                                        listener.onDocumentModified(snapshot.toObject(classObj));
                                        break;
                                    case REMOVED:
                                        Log.d(TAG, "Removed city: " + snapshot.getData());
                                        listener.onDocumentRemoved(snapshot.toObject(classObj));
                                        break;
                                }
                            }
                        }

                    }
                });
    }
    //https://firebasestorage.googleapis.com/v0/b/objecttranslator-d0fa1.appspot.com/o/foodimage.jpg?alt=media&token=68441310-0128-48b1-a17d-e424cd0092b4
    public static String STORAGE_URL = "https://firebasestorage.googleapis.com/v0/b/objecttranslator-d0fa1.appspot.com/o/images%2F";

    public static String generateImageUrl(String id){
        return STORAGE_URL + id + "?alt=media";
    }

}

package com.akshaykale.objecttranslator.login;

import android.content.Context;

import com.akshaykale.objecttranslator.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class GoogleApiHelper {
    private static final String TAG = GoogleApiHelper.class.getSimpleName();
    private Context context;

    private GoogleSignInClient mGoogleSignInClient;

    public GoogleApiHelper(Context context) {
        this.context = context;

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

    }

    public GoogleSignInClient getGoogleSignInClient(){
        return mGoogleSignInClient;
    }

    public void signOut(OnSuccessListener successCallback, OnFailureListener failCallback) {
        // Google sign out
        mGoogleSignInClient.signOut().addOnSuccessListener(successCallback).addOnFailureListener(failCallback);
    }

    public void signOut() {

        // Google sign out
        mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //LocalDataStorageManager.getInstance().clear();
                // Firebase sign out
                FirebaseAuth.getInstance().signOut();
            }
        });
    }
}

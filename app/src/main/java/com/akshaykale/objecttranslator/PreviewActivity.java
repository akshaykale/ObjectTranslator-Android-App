package com.akshaykale.objecttranslator;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.akshaykale.objecttranslator.models.MEinsteinResponse;
import com.akshaykale.objecttranslator.models.MEinsteinResult;
import com.akshaykale.objecttranslator.models.MWord;
import com.akshaykale.objecttranslator.utils.firebase.FireDataManager;
import com.akshaykale.objecttranslator.utils.firebase.ICollectionChangeListener;
import com.akshaykale.objecttranslator.utils.firebase.IUploadImageListener;
import com.akshaykale.objecttranslator.utils.retrofit.IRetroObjectResponseListener;
import com.akshaykale.objecttranslator.utils.retrofit.RetroApiController;
import com.akshaykale.objecttranslator.utils.ui.ImageViewSquare;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity implements IUploadImageListener, IRetroObjectResponseListener<MEinsteinResponse>,ICollectionChangeListener<MWord>, EventListener<QuerySnapshot> {

    @BindView(R.id.iv_preview_image)
    ImageViewSquare iv_preview;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progressbar)
    LinearLayout progressbar;

    FireDataManager fireDataManager;

    Bitmap image = CaptureResultHolder.getImage();
    private String TAG = getClass().getSimpleName();
    private ListFragmentAdapter adapter;

    ArrayList<MWord> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        if (image == null){
            finish();
        }

        iv_preview.setImageBitmap(image);

        //Call the API
        String imageId = String.valueOf(System.currentTimeMillis());
        fireDataManager = new FireDataManager();
        fireDataManager.uploadImage(image, imageId, this);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListFragmentAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onImageUploadSuccess(String path) {
        //Call the API
        Log.d(TAG, path);
        RetroApiController controller  = new RetroApiController(this);
        controller.start(path);
    }

    @Override
    public void onImageUploadFailed() {
        Log.d(TAG, "failed");

    }

    @Override
    public void onRetroApiSuccess(MEinsteinResponse response) {
        Log.d(TAG, "");
        for (MEinsteinResult result : response.probabilities){
            HashMap<String,String> map = new HashMap<>();
            map.put("wordFrom", result.label);
            map.put("langFrom", "en");
            map.put("langTo", "ja");

            fireDataManager.getWordCollReference().whereEqualTo("wordFrom", result.label.toLowerCase()).addSnapshotListener(this);
        }
    }

    @Override
    public void onRetroApiFailed(MEinsteinResponse error) {
        Log.d(TAG, "");

    }

    @Override
    public void onDocumentAdded(MWord model) {
        list.add(model);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDocumentModified(MWord model) {

    }

    @Override
    public void onDocumentRemoved(MWord model) {

    }

    @Override
    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "listen:error", e);
            return;
        }

        if (queryDocumentSnapshots != null) {
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot snapshot = dc.getDocument();
                switch (dc.getType()) {
                    case ADDED:
                        Log.d(TAG, "New city: " + snapshot.getData());
                        list.add(snapshot.toObject(MWord.class));
                        adapter.notifyDataSetChanged();
                        progressbar.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }
}

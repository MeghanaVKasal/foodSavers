package com.frankegan.foodsavers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import android.support.annotation.NonNull;

public class FoodDetailActivity extends AppCompatActivity {
    String mLatitude;
    String mLongitude;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final String TAG = "FoodDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Get values from bundle
        Bundle bundle = getIntent().getExtras();

        String mFoodPicUrl = bundle.getString("bundlePictureURL");
        String foodTags = bundle.getString("bundleTags");
        String foodItemsCollection = bundle.getString("bundleFoodItems");
        String mDescription = bundle.getString("bundleDescription");
        final String mProducer = bundle.getString("bundleProducer");
        final String mConsumer = bundle.getString("bundleConsumer");
        String mAddress = bundle.getString("bundleAddress");
        mLatitude = bundle.getString("bundleLatitude");
        mLongitude = bundle.getString("bundleLongitude");

        //Get view handlers
        ImageView mFoodPicView = findViewById(R.id.food_pic);
        TextView foodTagView = findViewById(R.id.tags);
        final TextView foodItemsView = findViewById(R.id.food_items);
        TextView mDescriptionView = findViewById(R.id.description);
        final TextView mProducerView = findViewById(R.id.producer_name);
        TextView mAddressView = findViewById(R.id.address);
        Button mMapBtn = findViewById(R.id.mapMe);

        //Set View handlers
        mMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mLatitude + "," + mLongitude + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        GlideApp.with(this)
                .load(mFoodPicUrl)
                .override(512)
                .into(mFoodPicView);
        foodTagView.setText(foodTags);
        mDescriptionView.setText(mDescription);
        mAddressView.setText(mAddress);

        firestore.document(mProducer).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override public void onSuccess(DocumentSnapshot documentSnapshot) {
                mProducerView.setText(documentSnapshot.getString("displayName"));
            }
        });

        firestore.collection(foodItemsCollection).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override public void onSuccess(QuerySnapshot snapshots) {
                for (DocumentSnapshot d : snapshots.getDocuments()) {
                    foodItemsView.append(d.getString("name") + ": " + d.getDouble("quantity") + "\n");
                }
            }
        });

        RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Map<String, Object> rate = new HashMap<>();
                rate.put("consumer", "users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                rate.put("producer", mProducer);
                rate.put("stars", v);

                firestore.collection("ratings")
                        .add(rate)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });

    }
}

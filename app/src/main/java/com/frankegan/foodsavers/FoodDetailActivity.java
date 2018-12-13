package com.frankegan.foodsavers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoodDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    String mLatitude;
    String mLongitude;
    String docPath;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final String TAG = "FoodDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Get values from bundle
        Bundle bundle = getIntent().getExtras();

        String mFoodPicUrl = bundle.getString("bundlePictureURL");
        String foodTags = bundle.getString("bundleTags");
        docPath = bundle.getString("bundleFoodItems");
        String foodItemsCollection = docPath + "/foodItems";
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

        RatingBar mRatingBar = findViewById(R.id.ratingBar);
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

    @OnClick(R.id.claim_button)
    void claimFood() {
        firestore.document(docPath).update("claimed", true);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng marker = new LatLng(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
        googleMap.addMarker(new MarkerOptions().position(marker)
                .title("Food Post"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        googleMap.setMinZoomPreference(15);
    }
}

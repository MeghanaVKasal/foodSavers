package com.frankegan.foodsavers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.frankegan.foodsavers.adapter.FoodItemsAdapter;
import com.frankegan.foodsavers.model.Post;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsumerActivity extends AppCompatActivity
        implements FoodItemsAdapter.OnFoodSelectedListener {
    private static final int LIMIT = 50;
    private static final String TAG = "ConsumerActivity";

    @BindView(R.id.recycler_food_items_list)
    RecyclerView foodItemsRecycler;
    FoodItemsAdapter mAdapter;

    private FirebaseFirestore firestore;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);
        ButterKnife.bind(this);
        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        // Initialize Firestore and the main RecyclerView
        initFireStore();
        initRecyclerView();
    }

    private void initFireStore() {
        firestore = FirebaseFirestore.getInstance();
        query = firestore.collection("posts")
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (query == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new FoodItemsAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    foodItemsRecycler.setVisibility(View.GONE);
                } else {
                    foodItemsRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        foodItemsRecycler.setLayoutManager(new LinearLayoutManager(this));
        foodItemsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onFoodSelected(DocumentSnapshot snap) {
        Intent intent = new Intent(this, FoodDetailActivity.class);
        Post post = snap.toObject(Post.class);
        Bundle bundle = new Bundle();
        bundle.putString("bundleAddress", post.getAddress());
        bundle.putString("bundlePictureURL", post.getPictureURL());
        bundle.putString("bundleLatitude", String.valueOf(post.getLocation().getLatitude()));
        bundle.putString("bundleLongitude", String.valueOf(post.getLocation().getLongitude()));
        List<String> foodList = post.getTags();
        String foodString = TextUtils.join(" ", foodList);
        bundle.putString("bundleFoodItems", foodString);
        bundle.putString("bundleProducer", post.getProducer());
        bundle.putString("bundleDescription", post.getDescription());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

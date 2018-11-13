package com.frankegan.foodsavers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.frankegan.foodsavers.model.Food;
import com.frankegan.foodsavers.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {
    private ListView foodItemListView;
    ArrayAdapter<String> adapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        firestore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        foodItemListView = findViewById(R.id.food_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        foodItemListView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> tags = new ArrayList<>();
                tags.add("Apple");
                final List<Food> foods = new ArrayList<>();
                for (int i = 0; i < adapter.getCount(); i++) {
                    foods.add(new Food("", adapter.getItem(i), 1));
                }
                firestore.collection("posts").add(new Post(
                        new GeoPoint(0, 0),
                        "100 Institute Rd",
                        tags,
                        "picture-url",
                        "An apple",
                        false,
                        "users/MzB8GGIQS67hxEYVP6Vu",
                        "users/MzB8GGIQS67hxEYVP6Vu",
                        new ArrayList<Food>()
                )).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override public void onSuccess(DocumentReference documentReference) {
                        for (Food f : foods) {
                            firestore.collection("posts")
                                    .document(documentReference.getId())
                                    .collection("foodItems")
                                    .add(f);
                        }
                    }
                });
            }
        });

        findViewById(R.id.addFoodItem).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                addFoodItem();
            }
        });
    }


    private void addFoodItem() {
        final EditText foodInput = new EditText(this);
        new AlertDialog
                .Builder(this)
                .setTitle("Add a Food Item")
                .setMessage("Type the name of the food you are posting")
                .setView(foodInput)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String foodName = foodInput.getText().toString();
                        adapter.add(foodName);
                    }
                })
                .setCancelable(true)
                .show();
    }

}

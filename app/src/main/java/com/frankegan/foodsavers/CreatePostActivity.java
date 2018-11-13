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

import com.google.firebase.firestore.FirebaseFirestore;

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
                firestore.collection("posts").add();
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

package com.frankegan.foodsavers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.frankegan.foodsavers.model.Post;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class ConsumerActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);

        ArrayList<Post> arrayOfItems = new ArrayList<Post>();
// Create the adapter to convert the array to views

        // just temporary data
        GeoPoint gp = new GeoPoint(73.04,74.09);
        Post p = new Post(gp,"",new ArrayList<String>(),"","",false,"Dunkin Donuts", "");
        arrayOfItems.add(p);

        //

        ItemAdapter adapter = new ItemAdapter(this, arrayOfItems);
// Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.item_list_view);
        listView.setAdapter(adapter);
    }
}

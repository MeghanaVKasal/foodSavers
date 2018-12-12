package com.frankegan.foodsavers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class FoodDetailActivity extends AppCompatActivity{

    private static final String TAG = "Food Detail";
    String mLatitude;
    String mLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Get values from bundle
        Bundle bundle = getIntent().getExtras();

        String mFoodPicUrl = bundle.getString("bundlePictureURL");
        String mFooditems = bundle.getString("bundleFoodItems");
        String mDescription = bundle.getString("bundleDescription");
        String mProducer = bundle.getString("bundleProducer");
        String mAddress = bundle.getString("bundleAddress");
        mLatitude = bundle.getString("bundleLatitude");
        mLongitude = bundle.getString("bundleLongitude");

        //Get view handlers
        ImageView mFoodPicView = (ImageView)findViewById(R.id.food_pic);
        TextView mFooditemsView = (TextView)findViewById(R.id.food_items);
        TextView mDescriptionView = (TextView)findViewById(R.id.description);
        TextView mProducerView = (TextView)findViewById(R.id.producer_name);
        TextView mAddressView = (TextView)findViewById(R.id.address);
        ImageButton mMapBtn = (ImageButton)findViewById(R.id.mapMe);

        //Set View handlers
        mMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+ mLatitude+","+mLongitude +"&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        new DownloadImageTask(mFoodPicView)
                .execute(mFoodPicUrl); //Sets image view from picture url
        mFooditemsView.setText(mFooditems);
        mDescriptionView.setText(mDescription);
        mProducerView.setText(mProducer);
        mAddressView.setText(mAddress);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}

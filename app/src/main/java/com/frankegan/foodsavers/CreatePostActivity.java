package com.frankegan.foodsavers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.frankegan.foodsavers.model.Food;
import com.frankegan.foodsavers.model.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {
    private final static int IMAGE_CAPTURE_REQ = 201;
    private static final String TAG = CreatePostActivity.class.getSimpleName();

    private ListView foodItemListView;
    ArrayAdapter<String> adapter;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private EditText addressInput;
    private EditText descInput;
    private ImageView thumbnail;
    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addressInput = findViewById(R.id.addressInput);
        descInput = findViewById(R.id.descInput);
        thumbnail = findViewById(R.id.theumbnail);

        foodItemListView = findViewById(R.id.food_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        foodItemListView.setAdapter(adapter);
        findViewById(R.id.attachPhotoButton).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                openCamera();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> tags = new ArrayList<>();
                tags.add("Apple");
                final List<Food> foods = new ArrayList<>();
                for (int i = 0; i < adapter.getCount(); i++) {
                    foods.add(new Food(adapter.getItem(i), 1));
                }
                firestore.collection("posts").add(new Post(
                        new GeoPoint(0, 0),
                        addressInput.getText().toString(),
                        tags,
                        photoUrl,
                        descInput.getText().toString(),
                        false,
                        "users/MzB8GGIQS67hxEYVP6Vu",
                        "users/MzB8GGIQS67hxEYVP6Vu"
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

    /**
     * This callback is ran after the external camera has taken a photo. The photo is provided to us
     * as intent extras.
     *
     * @param data Contains an Intent extra called "data" which contains the bitmap of our photo.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_CAPTURE_REQ && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras == null) return;
            Bitmap photo = rotate((Bitmap) extras.get("data"));
            if (photo == null) return;
            thumbnail.setVisibility(View.VISIBLE);
            thumbnail.setImageBitmap(photo);
            addTags(photo);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();

            final StorageReference imageRef = storage.getReference(System.currentTimeMillis() + ".jpg");
            final UploadTask uploadTask = imageRef.putBytes(bytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("CreatePostActivity", exception.getMessage(), exception);
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) throw task.getException();
                    // Continue with the task to get the download URL
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        photoUrl = task.getResult().toString();
                        Log.d("CreatePostActivity", photoUrl);
                    } else {
                        Log.e("CreatePostActivity", task.getException().getMessage(), task.getException());
                    }
                }
            });
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQ);
        }
    }

    private void addTags(Bitmap photo) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                .getVisionLabelDetector();
        detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionLabel> labels) {
                                // Task completed successfully
                                for (FirebaseVisionLabel l : labels) {
                                    Log.d(TAG, String.format("%s: %f %%", l.getLabel(), l.getConfidence()));
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                Log.d(TAG, e.getMessage(), e);
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

    Bitmap rotate(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}

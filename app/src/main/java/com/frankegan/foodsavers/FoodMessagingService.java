package com.frankegan.foodsavers;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FoodMessagingService extends FirebaseMessagingService {
    private static final String TAG = FoodMessagingService.class.getSimpleName();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override public void onNewToken(String newToken) {
        Log.d(TAG, "Refreshed token: " + newToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            firestore.collection("users").document(user.getUid())
                    .update("fcmToken", newToken);
        }
    }
}

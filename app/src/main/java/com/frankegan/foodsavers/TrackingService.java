package com.frankegan.foodsavers;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class TrackingService extends Service {
    public static final String LOCATION_CHANNEL = "LOCATION_CHANNEL";
    private static final String TAG = TrackingService.class.getSimpleName();
    private FirebaseAuth firebaseAuth;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        buildNotification();
        loginToFirebase();
    }

    /**
     * Create the persistent notification
     */
    private void buildNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(LOCATION_CHANNEL, "Location Tracking", importance);
            channel.setDescription("Alerts you when the app is tracking your location");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        final String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification//
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, LOCATION_CHANNEL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))
                .setOngoing(true)
                .setContentIntent(broadcastIntent);
               // .setSmallIcon(R.drawable.ic_location_on_black_24dp);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped
            unregisterReceiver(stopReceiver);
            //Stop the Service
            stopSelf();
        }
    };

    private void loginToFirebase() {
        //Call OnCompleteListener if the user is signed in successfully//
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //If the user has been authenticated...//
        if (user != null) {
            //...then call requestLocationUpdates//
            requestLocationUpdates();
        } else {
            //If sign in fails, then log the error//
            Log.d(TAG, "Firebase authentication failed");
        }
    }

    /**
     * Initiate the request to track the device's location//
     */
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        //Specify how often your app should request the device’s location
        request.setInterval(10_000L);

        //Get the most accurate location data available//
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        //If the app currently has access to the location permission...
        if (permission == PackageManager.PERMISSION_GRANTED) {
            //...then request location updates//
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    //Get a reference to the database, so your app can perform read and write operations//
                    DocumentReference ref = FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(firebaseAuth.getCurrentUser().getUid());
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        //Save the location data to the database
                        ref.update("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
                    }
                }
            }, null);
        }
    }

    @Override public void onDestroy() {
//        unregisterReceiver(stopReceiver);
        super.onDestroy();
    }
}

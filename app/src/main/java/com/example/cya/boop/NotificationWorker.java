package com.example.cya.boop;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationWorker extends Service {
    protected GeoLocation user_location;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO FUERTE FELIPE HALP
        if(intent != null) {
            double latitude = intent.getDoubleExtra("latitude", 5);
            double longitude = intent.getDoubleExtra("longitude", 5);
            Log.e("notification latlog", Double.toString(latitude));
            Log.e("notification latlog", Double.toString(longitude));
            Log.d("notification worker", "notification worker awaked");

            this.user_location = new GeoLocation(latitude, longitude);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
            GeoFire geofire = new GeoFire(ref);

            GeoQuery query = geofire.queryAtLocation(this.user_location, 16);
            query.addGeoQueryEventListener(new NotificationBManager(this));
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package com.example.cya.boop;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationWorker extends Service {
    protected GeoLocation user_location;

    public NotificationWorker() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        double latitude = intent.getDoubleExtra("latitude",0.0);
        double longitude = intent.getDoubleExtra("longitude",0.0);
        this.user_location = new GeoLocation(latitude, longitude);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        GeoFire geofire = new GeoFire(ref);

        GeoQuery query = geofire.queryAtLocation(this.user_location,16);
        query.addGeoQueryEventListener(new NotificationBManager(this));



        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

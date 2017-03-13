package com.example.cya.boop;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.example.cya.boop.core.Boop;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by noboru on 13/03/17.
 */

public class NotificationBManager implements GeoQueryEventListener{

    protected DatabaseReference mDatabase;
    protected Context context;
    protected Boolean ready = false;

    public NotificationBManager(Context c){
        mDatabase = FirebaseDatabase.getInstance().getReference("BoopInfo");
        context = c;
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        if(ready){
            mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boop b = dataSnapshot.getValue(Boop.class);
                    notifyBoop(b);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {
        ready = true;
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

    private void notifyBoop(Boop b){
        Notification n = new Notification.Builder(this.context)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentTitle("New event: "+b.getNombre())
                .setContentText(b.getDescripcion()).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,n);
    }
}

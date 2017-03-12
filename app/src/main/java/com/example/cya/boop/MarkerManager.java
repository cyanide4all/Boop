package com.example.cya.boop;

import com.example.cya.boop.core.Boop;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by noboru on 12/03/17.
 */

public class MarkerManager implements GeoQueryEventListener {

    protected GoogleMap mMap;
    protected DatabaseReference mDatabase;
    protected GeoFire geoFire;
    protected HashMap<String,Marker> markers;

    public MarkerManager(GoogleMap map, DatabaseReference mDatabase, GeoFire geoFire){
        this.mMap = map;
        this.mDatabase = mDatabase;
        this.geoFire = geoFire;
        this.markers = new HashMap<>();
    }

    @Override
    public void onKeyEntered(final String key, final GeoLocation location) {
        // cuando vengan llegando los datos de la base de datos vamos rellenando
        // el mapa de marcadores de eventos
        mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boop b = dataSnapshot.getValue(Boop.class);
                manageBoop(b,location,key);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void manageBoop(Boop b, GeoLocation location, String key){
        final Date now = new Date();

        MarkerOptions op = new MarkerOptions()
                .position(new LatLng(location.latitude,location.longitude));

        if(b.getFechaIni().after(now)){
            // aun no ha pasado
            op.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }else if(b.getFechaIni().before(now) && b.getFechaFin().after(now)){
            // esta pasando
            op.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        }else if(b.getFechaFin().before(now)){
            // el evento ya ha pasado
            mDatabase.child(key).removeValue();
            geoFire.removeLocation(key);
        }

        Marker m = mMap.addMarker(op);
        m.setTag(b);
        markers.put(key,m);
    }

    @Override
    public void onKeyExited(String key) {
        Marker m = markers.get(key);
        m.remove();
        markers.remove(m);
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }
}

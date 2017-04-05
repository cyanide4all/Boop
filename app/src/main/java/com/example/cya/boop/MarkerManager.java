package com.example.cya.boop;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.cya.boop.core.Boop;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by noboru on 12/03/17.
 */

public class MarkerManager implements GeoQueryEventListener {

    protected GoogleMap mMap;
    protected DatabaseReference mDatabase;
    protected GeoFire geoFire;
    protected HashMap<String,mapBundle> markers;
    protected RecyclerView cardsView;
    protected BoopCardsManager manager;

    protected class mapBundle {
        public Marker marker;
        public Boop boop;

        public mapBundle(Marker m, Boop b){
            this.marker = m;
            this.boop = b;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public MarkerManager(GoogleMap map, DatabaseReference mDatabase, GeoFire geoFire, RecyclerView view){
        this.mMap = map;
        this.mDatabase = mDatabase;
        this.geoFire = geoFire;
        this.markers = new HashMap<>();
        this.cardsView = view;
        this.manager = (BoopCardsManager) view.getAdapter();
        manager.clear();

        cardsView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(cardsView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING){
                    LinearLayoutManager layoutManager = ((LinearLayoutManager) cardsView.getLayoutManager());
                    int status = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if(status<0){
                        status = layoutManager.findLastVisibleItemPosition();
                    }
                    zoomToBoop(status);
                }
            }
        });

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
                manager.insert(b);
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
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://boop-4ec7a.appspot.com");
            StorageReference boopRef = storageRef.child("Boopimages/"+key);
            boopRef.delete();
        }

        Marker m = mMap.addMarker(op);
        m.setTag(key);
        markers.put(key,new mapBundle(m,b));
    }

    @Override
    public void onKeyExited(String key) {
        // todo borrar tambien las locations
        mapBundle m = markers.get(key);
        m.marker.remove();
        manager.remove(m.boop);
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

    protected void zoomToBoop(int pos){
        try {
            Boop targetBoop = manager.get(pos);
            for (mapBundle i : markers.values()) {
                if (i.boop == targetBoop) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(i.marker.getPosition(), 16);
                    mMap.animateCamera(yourLocation);
                    break;
                }
            }
        } catch (Exception e){
            // none it's just a scroll event the user does this all the time
        }
    }


}

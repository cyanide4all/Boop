package com.example.cya.boop;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.cya.boop.core.Boop;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.google.android.gms.maps.GoogleMap.*;

public class BoopMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private ImageButton boopBtn;
    private ImageButton boopCncl;
    private ImageButton boopPerfil;
    private Location mLastLocation;
    private GeoFire geofire;
    private Boolean aceptando_clicks = false;
    private DatabaseReference mDatabase;
    private Marker myEventLocation;
    public static DisplayMetrics displayMetrics;

    private void createNewBoop(){
        if(myEventLocation != null){
            Intent toSend = new Intent(this,NuevoBoop.class);
            toSend.putExtra("longitude", myEventLocation.getPosition().longitude);
            toSend.putExtra("latitude",myEventLocation.getPosition().latitude);
            cancelPlacing();
            startActivity(toSend);
        }else{
            if(!aceptando_clicks){
                Snackbar.make(findViewById(R.id.map),"Now touch where do you want to host your event in the map",Snackbar.LENGTH_LONG).show();
                boopCncl.setVisibility(View.VISIBLE);
                aceptando_clicks = true;
            }else{
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Oops")
                        .setMessage("You must set a place to host your event")
                        .setPositiveButton("OK",null ).show();
            }
        }
    }

    private void cancelPlacing(){
        aceptando_clicks = false;
        if(myEventLocation != null){
            myEventLocation.remove();
            myEventLocation = null;
        }
        boopCncl.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Meter el layout
        super.onCreate(savedInstanceState);


        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();

        setContentView(R.layout.boop_map);

        //Mas manageamientos de layout
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Meter el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Meter un evento al boton boop
        boopBtn = (ImageButton) findViewById(R.id.boopBtn);
        boopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewBoop();
            }
        });

        boopCncl = (ImageButton) findViewById(R.id.boopCncl);
        boopCncl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPlacing();
            }
        });

        boopPerfil = (ImageButton) findViewById(R.id.boopPerfil);
        boopPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BoopMap.this, VerPerfil.class));
            }
        });

        // Conectarse a googleApiClient para pedirle la localizacion
        // *Continua en OnConnected*
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Obtener una referencia y conectarse a firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        geofire = new GeoFire(ref);
        mDatabase = FirebaseDatabase.getInstance().getReference("BoopInfo");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // TODO mover la camara y indicar con un puntico o algo donde esta el usuario. En azul su radio
        // de recibir boops

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(aceptando_clicks){
                    if(myEventLocation != null){
                        myEventLocation.remove();
                    }
                    myEventLocation = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latLng.latitude,latLng.longitude))
                    ); // marcamos la posicion donde el usuario quiere crear un evento
                    // en un futuro podra tener otro color.
                    createNewBoop();
                }
            }
        });

        //ESTO LLAMA A CREAR UN POPUP CON EL INFO DEL BOOP
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Pillamos el boop del tag
                Boop b = (Boop) marker.getTag();
                //Lo metemos en un bundle porque los constructores son nuestros enemigos
                Bundle humble = new Bundle();
                humble.putSerializable("boop",b);
                //Y lo mandamos a verBoop, el popup
                VerBoop verBoop = new VerBoop();
                verBoop.setArguments(humble);

                verBoop.show(getFragmentManager(), "VerBoopTag");
                return true;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // me han dado permisos top!
            // TODO SI no permisos pues cerrar la aplicacion o algo
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        // TENEMOS LATITUD Y LONGITUD AQUI DEL SENOR
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),16));
            Log.d("moved","map");
        }

        if (mLastLocation != null) {
            GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),10);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, final GeoLocation location) {
                    // cuando vengan llegando los datos de la base de datos vamos rellenando
                    // el mapa de marcadores de eventos
                    mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boop b = dataSnapshot.getValue(Boop.class);
                            if(mMap != null){
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.latitude,location.longitude))
                                        .title(b.getNombre())
                                        .snippet(b.getDescripcion())

                                ); //.showInfoWindow()
                                marker.setTag(b);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onKeyExited(String key) {

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
            });
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume(){
        super.onResume();
        //TODO usar aqui un AuthStateListener en vez de esta solucion cutre
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            finish();
        }

    }
}

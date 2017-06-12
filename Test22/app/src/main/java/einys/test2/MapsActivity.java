package einys.test2;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.skp.Tmap.TMapTapi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Google Corp. and Kim Yangsun on 2017-06-11.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient = null;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private LatLng curLatLng = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = new FusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                curLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Route();


                            }
                        }
                    });

            TMapTapi tmaptapi = new TMapTapi(this);
            tmaptapi.setSKPMapAuthentication ("6c5dc4df-49da-32f6-ad3b-4bf375e36ce9");

        }else {
            // Request permission.
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }


        TcpClient tc = new TcpClient("192.168.0.14", 8787);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);



        } else {
            // Request permission.
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }


        mark(mMap);
    }

    private void Route(){

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(curLatLng);
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(curLatLng));
        mMap.addMarker(markerOptions);
        Double lat =curLatLng.latitude;
        Double lng =curLatLng.longitude;
        Log.i("Location",  curLatLng.latitude + " " + curLatLng.longitude);

        String endName = "용인시 처인구 삼가동 늘푸른아파트";

        GoogleGeoRequest ggr = new GoogleGeoRequest();
        LatLng dest = ggr.addr2Co("용인시 처인구 삼가동 늘푸른아파트");

                                /*
                                404에러 이런거 핸들링하기 @
                                 */

        TmapHttpReq thr = new TmapHttpReq();

        Log.i("Location22",  curLatLng.latitude + " " + curLatLng.longitude);



    }


    private void mark(final GoogleMap map) {

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);
                markerOptions.title(latLng.toString());

                map.clear();
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                map.addMarker(markerOptions);

            }

        });

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }

/*
//Google Play 서비스 접근승인 요청
public GoogleApiClient.Builder setGoogleServiceBuilder(){
//Google Api Client 생성

    GoogleApiClient.Builder mGoogleApiClientBuilder = new GoogleApiClient.Builder(this.activity);

mGoogleApiClientBuilder.addApi(LocationServices.API);//Fused Location Provider API 사용요청



//Google Client Connection Callback 클래스

    CallbackConnectedGoogleService callbackConnectedGoogleService = new CallbackConnectedGoogleService(this);

mGoogleApiClientBuilder.addConnectionCallbacks(callbackConnectedGoogleService);

mGoogleApiClientBuilder.addOnConnectionFailedListener(callbackConnectedGoogleService);



    GoogleApiClient mGoogleApiClient = mGoogleApiClientBuilder.build();

mGoogleApiClient.connect();



return mGoogleApiClientBuilder;

}

*/

}

/*
class CallbackConnectedGoogleService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



    private String LOG_TAG = "CallbackGooglePlacesService";

    private GoogleServiceControl googleServiceControl;



    public CallbackConnectedGoogleService(GoogleServiceControl googleServiceControl){

        this.googleServiceControl = googleServiceControl;

    }



    @Override
    public void onConnectionFailed(ConnectionResult result) {

// TODO Auto-generated method stub

        Log.d(LOG_TAG, "GoogleService onConnectionSuspended");

        Log.d(LOG_TAG, "Connected Failed : " + result.getErrorCode());

    }



    @Override

    public void onConnected(Bundle connectionHint) {

// TODO Auto-generated method stub

        Log.d(LOG_TAG, "GoogleService onConnected");

        Log.d(LOG_TAG, "Connected Success");

    }



    @Override

    public void onConnectionSuspended(int cause) {

// TODO Auto-generated method stub

        Log.d(LOG_TAG, "GoogleService onConnectionSuspended");

        Log.d(LOG_TAG, "Suspended cause : " + cause);

    }

}
*/
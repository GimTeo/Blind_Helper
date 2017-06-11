package einys.test2;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.connection.Payload;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient = null;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private HttpURLConnection conn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TMapTapi tmaptapi = new TMapTapi(this);
        tmaptapi.setSKPMapAuthentication ("6c5dc4df-49da-32f6-ad3b-4bf375e36ce9");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            mFusedLocationClient = new FusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                markerOptions.position(latLng);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.addMarker(markerOptions);
                                Log.i("Location", latLng.latitude + " " + latLng.longitude);


                                // ...
                                Log.i("rqtest", requestHttpGet("http://apis.skplanetx.com/tmap/routes?version=1&callback=%22%22&startX=14&startY=1&endX=1&endY=1"));
                            }
                        }
                    });

            TMapTapi tmaptapi = new TMapTapi(this);
            tmaptapi.setSKPMapAuthentication ("6c5dc4df-49da-32f6-ad3b-4bf375e36ce9");




        } else {
            // Request permission.
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }


        mark(mMap);
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

    public String requestHttpGet(final String urlStr){

        final StringBuilder sb = new StringBuilder();

        try {

            URL url = new URL(urlStr); //요청 URL을 입력
            conn = (HttpURLConnection) url.openConnection();

            new Thread(){
                public void run(){

                    try {
                        conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)

                        conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                        conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                        conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)

                        OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
                        bw.write(""); // 매개변수 전송
                        bw.flush();
                        bw.close();
                        os.close();

                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); //캐릭터셋 설정

                        String line = null;
                        while ((line = br.readLine()) != null) {
                            if (sb.length() > 0) {
                                sb.append("\n");
                            }
                            sb.append(line);
                        }

                        System.out.println("response:" + sb.toString());
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        return sb.toString();

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
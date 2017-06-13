package einys.test2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


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

import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

/**
 * Created by Google Corp. and Kim Yangsun on 2017-06-11.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient = null;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private CurLatLng cLatLng = null;
    private MyLatLng myDest = null;
    private LatLng curLatLng = null;
    private LatLng destLatLng = null;
    private ArrayList<wayPoint> wayPoints = null;
    private String curLocAddr;
    private TcpClient tc;

    private TextToSpeech myTTS;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String DEST; // STT로 변환한 TEXT값




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TMapTapi tmaptapi = new TMapTapi(this);
        tmaptapi.setSKPMapAuthentication("6c5dc4df-49da-32f6-ad3b-4bf375e36ce9");




        tc = new TcpClient("192.168.42.139", 8001);


        cLatLng = new CurLatLng(0.0, 0.0, new CurLatLng.ChangeListener() {
            @Override
            public void onChange() {

            }

            @Override
            public void onInit() {

            }
        });

        myDest = new MyLatLng(0.0, 0.0, new MyLatLng.ChangeListener() {
            @Override
            public void onSetName() {
                navigate();
            }

            @Override
            public void onChange() {

            }

            @Override
            public void onInit() {

            }
        });

        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //myTTS.speak("시작", QUEUE_ADD, null, "init")

                promptSpeechInput();

            }
        });



        //route(curLatLng);


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


    private void navigate(){
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

                                //현재위치에 마커
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(curLatLng);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(curLatLng));
                                mMap.addMarker(markerOptions);
                                Double lat = curLatLng.latitude;
                                Double lng = curLatLng.longitude;
                                //~ 현재위치에 마커

                                TmapHttpReq thr = new TmapHttpReq("http://apis.skplanetx.com/tmap/geo/reversegeocoding?" +
                                        "lon="+Double.toString(curLatLng.longitude)+"&callback=&coordType=WGS84GEO&addressType=&lat="+Double.toString(curLatLng.latitude)+"&version=1");
                                thr.threadGET.start();
                                try {
                                    thr.threadGET.join();
                                    String s = thr.getResponse();
                                    JSONObject jo = new JSONObject(s);
                                    curLocAddr = jo.getJSONObject("addressInfo").getString("fullAddress");
                                    ;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                myTTS.speak("현재 위치"+ curLocAddr + "좌표는 경도"+curLatLng.latitude+ " 위도"+curLatLng.longitude+ "입니다.", QUEUE_ADD, null, "curLoc");
                                Log.i("Location", curLatLng.latitude + " " + curLatLng.longitude);

                                cLatLng.setLatLng(curLatLng.latitude, curLatLng.longitude);
                                route(curLatLng);


                            }
                        }
                    });


        } else {
            // Request permission.
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }

    }

    public interface LocationResponse {
        void onResponseReceived(LatLng curLoc);
    }


    private void route(LatLng curLatLng) {

        myTTS.speak("목적지는" + DEST, QUEUE_ADD, null, "dest");
        myTTS.speak("경로를 탐색합니다.", QUEUE_ADD, null, "dest");

        String endName = "삼가동 진우아파트";
        GoogleGeoRequest ggr = new GoogleGeoRequest(endName);
        ggr.addr2co.start();

        try {
            ggr.addr2co.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        destLatLng = ggr.getResultLatlng();

        /*
                       404에러 이런거 핸들링하기 @
         */

        myTTS.speak("경로를 안내합니다.", QUEUE_ADD, null, "dest");


        TmapHttpReq thr = new TmapHttpReq("http://apis.skplanetx.com/tmap/routes?version=1&callback=%22%22&" +
                "startX="+Double.toString(curLatLng.longitude) +
                "&startY="+Double.toString(curLatLng.latitude) +
                "&endX=" +Double.toString(destLatLng.longitude)+
                "&endY="+Double.toString(destLatLng.latitude)+
                "&startName="+"현재위치"+
                "&endName="+"목적지"+
                "&reqCoordType="+"WGS84GEO"+
                "&resCoordType="+"WGS84GEO");
        thr.threadGET.start();
        try {
            thr.threadGET.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wayPoints = thr.getWayPoints();
        speakWays(wayPoints);

        Log.i("Location22", curLatLng.latitude + " " + curLatLng.longitude);

    }

    private void speakWays(ArrayList<wayPoint> wayPoints){
        for(wayPoint wp : wayPoints){
            String s = wp.nextDir;
            myTTS.speak(s, QUEUE_ADD, null, "dir");
        }
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
                tc.sendSTOP();

            }

        });

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say command...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device not support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    DEST = result.get(0);
                    myDest.setName(DEST);

                    //myTTS.speak(DEST+"가 맞습니까", TextToSpeech.QUEUE_ADD, null); // 주소 되물어보기
                }
                break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }

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
package einys.test2;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kim Yangsun on 2017-06-11.
 *
 * Geocoding
 * Only for Kor
 */

public class GoogleGeoRequest {

    private String myAppKey = "AIzaSyAogtwygranZEhQ4sOBd4Ftu9gQKB_61Uw";
    private HttpURLConnection conn = null;
    private StringBuilder sb = new StringBuilder();
    private URL url = null;
    private LatLng latLng = null;
    private JSONObject jObject = null;

    public LatLng addr2Co(final String address){
        try {
            //conn.setRequestProperty("appKey", myAppKey);
            new Thread(){
                public void run(){

                    try {

                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("key", myAppKey);
                        params.put("components", "country:KR");
                        params.put("address", address);

                        //https 사용함
                        StringBuilder postData = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?");
                        for (Map.Entry<String, Object> param : params.entrySet()) {
                            if (postData.length() != 0) postData.append('&');
                            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                            postData.append('=');
                            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                        }

                        URL url = new URL(postData.toString());

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)

                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); //캐릭터셋 설정

                        String line = null;
                        while ((line = br.readLine()) != null) {
                            if (sb.length() > 0) {
                                sb.append("\n");
                            }
                            sb.append(line);
                        }

                        //System.out.println("response:" + sb.toString());
                        latLng = jparsing(sb.toString());
                        Log.i("Addr2Co", address + "="+latLng.toString());




                        TmapHttpReq thr = new TmapHttpReq();
                        String str = thr.post("http://apis.skplanetx.com/tmap/routes?version=1&callback=%22%22&" +
                                "startX="+Double.toString(127.1721242) +
                                "&startY="+Double.toString(37.2387829) +
                                "&endX=" +Double.toString(latLng.longitude)+
                                "&endY="+Double.toString(latLng.latitude)+
                                "&startName="+"현재위치"+
                                "&endName="+"목적지"+
                                "&reqCoordType="+"WGS84GEO"+
                                "&resCoordType="+"WGS84GEO");


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

        return latLng;


    }

    private LatLng jparsing(String jsonGeoStr){

        Double lat = 0.0;
        Double lon = 0.0;

        try {
            JSONObject jObject = new JSONObject(jsonGeoStr);  // JSONObject 추출
            JSONArray jarray = jObject.getJSONArray("results");
            JSONObject zero = jarray.getJSONObject(0);
            JSONObject geo = zero.getJSONObject("geometry");
            JSONObject loc = geo.getJSONObject("location");
            lat = loc.getDouble("lat");
            lon = loc.getDouble("lng");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng latLng = new LatLng(lat, lon);


        return latLng;

    }


}

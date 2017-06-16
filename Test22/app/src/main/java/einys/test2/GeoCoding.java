package einys.test2;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by einaeby on 2017-06-11.
 */

public class GeoCoding {

    JSONObject jObject = null;

    public LatLng Addr2Co(String addr){

        Double lat = 0.0;
        Double lon = 0.0;

        TmapHttpReq thr = new TmapHttpReq();
        String str = thr.get("http://apis.skplanetx.com/tmap/geo/fullAddrGeo?count=&page=&addressFlag=&fullAddr="+ addr +"&callback=&coordType=WGS84GEO&format=&version=1");

        try {
            JSONArray jarray = new JSONArray(str);
            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출

            }
            lat = jObject.getDouble("lat");
            lon = jObject.getDouble("lon");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng latLng = new LatLng(lat, lon);
        Log.i("Addr2Co", addr + "="+latLng.toString());

        return latLng;
    }


}

package einys.test2;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ysunny on 2017-06-14.
 */

public class CurLatLng {

    private ChangeListener myChangeListener;
    private Double Lat;
    private Double Lng;

    public void setLatLng(Double Lat, Double Lng){
        this.Lat = Lat;
        this.Lng = Lng;
        myChangeListener.onChange();
    }

    public CurLatLng(Double Lat, Double Lng, ChangeListener changeListener) {
        myChangeListener = changeListener;
        this.Lat = Lat;
        this.Lng = Lng;
        myChangeListener.onInit();
    }

    public interface ChangeListener{
        void onChange();
        void onInit();
    }

}
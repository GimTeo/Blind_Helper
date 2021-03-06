package einys.test2;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ysunny on 2017-06-14.
 */

public class MyLatLng {

    private ChangeListener myChangeListener;
    private String name;
    private Double Lat;
    private Double Lng;

    public void setName(String name){
        this.name = name;
        myChangeListener.onSetName();
    }

    public void setLatLng(Double Lat, Double Lng){
        this.Lat = Lat;
        this.Lng = Lng;
        myChangeListener.onChange();
    }

    public MyLatLng(Double Lat, Double Lng, ChangeListener changeListener) {
        myChangeListener = changeListener;
        this.Lat = Lat;
        this.Lng = Lng;
        myChangeListener.onInit();
    }

    public interface ChangeListener{
        void onSetName();
        void onChange();
        void onInit();
    }

}
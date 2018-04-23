package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by Shin on 1/12/2017.
 */

public class LocationModel {

    private int id;
    private String latitude;
    private String longitude;
    private String date;
    private String keterangan;
    private String flag;

    public LocationModel(){}

    public LocationModel(String latitude, String longitude, String date, String keterangan, String flag) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.keterangan = keterangan;
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}

package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by Shin on 1/16/2017.
 */

public class Piutang {

    private int id;
    private String noNota;
    private String tanggal;
    private String kdCustomer;
    private String namaCustomer;
    private String piutang;
    private String status;

    public Piutang(){}

    public Piutang(int id, String noNota, String tanggal, String kdCustomer, String namaCustomer, String piutang, String status) {
        this.id = id;
        this.noNota = noNota;
        this.tanggal = tanggal;
        this.kdCustomer = kdCustomer;
        this.namaCustomer = namaCustomer;
        this.piutang = piutang;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoNota() {
        return noNota;
    }

    public void setNoNota(String noNota) {
        this.noNota = noNota;
    }

    public String getKdCustomer() {
        return kdCustomer;
    }

    public void setKdCustomer(String kdCustomer) {
        this.kdCustomer = kdCustomer;
    }

    public String getNamaCustomer() {
        return namaCustomer;
    }

    public void setNamaCustomer(String namaCustomer) {
        this.namaCustomer = namaCustomer;
    }

    public String getPiutang() {
        return piutang;
    }

    public void setPiutang(String piutang) {
        this.piutang = piutang;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

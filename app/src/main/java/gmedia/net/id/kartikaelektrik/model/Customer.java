package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by indra on 23/12/2016.
 */

public class Customer {
    private String kodeCustomer, namaCustomer, alamat, tempo, kota, totalPiutang, maxPiutang;

    public Customer(String kdCus, String nama, String alamat, String tempo) {
        this.kodeCustomer = kdCus;
        this.namaCustomer = nama;
        this.alamat = alamat;
        this.tempo = tempo;
    }

    public Customer(String kdCus, String nama, String alamat, String kota, String totalPiutang, String maxPiutang){
        this.kodeCustomer = kdCus;
        this.namaCustomer = nama;
        this.alamat = alamat;
        this.kota = kota;
        this.totalPiutang = totalPiutang;
        this.maxPiutang = maxPiutang;
    }

    public String getKodeCustomer() {
        return kodeCustomer;
    }

    public void setKodeCustomer(String kdCus) {
        this.kodeCustomer = kdCus;
    }

    public String getNamaCustomer() {
        return namaCustomer;
    }

    public void setNamaCustomer(String nama) {
        this.namaCustomer = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String almt) {
        this.alamat = almt;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getTotalPiutang() {
        return totalPiutang;
    }

    public void setTotalPiutang(String totalPiutang) {
        this.totalPiutang = totalPiutang;
    }

    public String getMaxPiutang() {
        return maxPiutang;
    }

    public void setMaxPiutang(String maxPiutang) {
        this.maxPiutang = maxPiutang;
    }
}

package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by Shin on 1/8/2017.
 */

public class SalesOrderDetail {

    private String soDetailID;
    private String idBarang;
    private String namaBarang;
    private String jumlah;
    private String jumlahpcs;
    private String satuan;
    private String harga;
    private String hargapcs;
    private String diskon;
    private String hargaNetto;
    private String hargaTotal;
    private String status;
    private String kdPaket;
    private String namaPaket;
    private String jensiPaket;

    public SalesOrderDetail(){}

    public SalesOrderDetail(String soDetailID, String idBarang,String namaBarang, String jumlah, String satuan, String hargaTotal){
        this.soDetailID = soDetailID;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.hargaTotal = hargaTotal;
    }

    public String getSoDetailID() {
        return soDetailID;
    }

    public void setSoDetailID(String soDetailID) {
        this.soDetailID = soDetailID;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getHargaTotal() {
        return hargaTotal;
    }

    public void setHargaTotal(String hargaTotal) {
        this.hargaTotal = hargaTotal;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getHargapcs() {
        return hargapcs;
    }

    public void setHargapcs(String hargapcs) {
        this.hargapcs = hargapcs;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJumlahpcs() {
        return jumlahpcs;
    }

    public void setJumlahpcs(String jumlahpcs) {
        this.jumlahpcs = jumlahpcs;
    }

    public String getHargaNetto() {
        return hargaNetto;
    }

    public void setHargaNetto(String hargaNetto) {
        this.hargaNetto = hargaNetto;
    }

    public String getNamaPaket() {
        return namaPaket;
    }

    public void setNamaPaket(String namaPaket) {
        this.namaPaket = namaPaket;
    }

    public String getJensiPaket() {
        return jensiPaket;
    }

    public void setJensiPaket(String jensiPaket) {
        this.jensiPaket = jensiPaket;
    }

    public String getKdPaket() {
        return kdPaket;
    }

    public void setKdPaket(String kdPaket) {
        this.kdPaket = kdPaket;
    }
}

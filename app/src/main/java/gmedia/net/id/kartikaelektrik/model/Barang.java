package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by indra on 24/12/2016.
 */

public class Barang {

    private String kodeBarang, namaBarang, harga, jumlah, satuan, stok, idKategori, sisa, noKonsinyasi, kdMerk, kdJenis;

    public Barang(String kodeBarang, String namaBarang) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
    }

    // for SO detail
    public Barang(String kodeBarang, String namaBarang, String jumlah, String satuan) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
        this.satuan = satuan;
    }

    // for Info Stok Barang
    public Barang(String kodeBarang, String namaBarang, String stok, String harga, String idKategori) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.stok = stok;
        this.harga = harga;
        this.idKategori = idKategori;
    }

    // for Info Stok Canvas
    public Barang(String kodeBarang, String namaBarang, String stok, String harga, String satuan, String sisa) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.stok = stok;
        this.harga = harga;
        this.satuan = satuan;
        this.sisa = sisa;
    }

    public Barang(String kodeBarang, String namaBarang, String stok, String harga, String satuan, String sisa, String noKonsinyasi) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.stok = stok;
        this.harga = harga;
        this.satuan = satuan;
        this.sisa = sisa;
        this.noKonsinyasi = noKonsinyasi;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kdBarang) {
        this.kodeBarang = kdBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String nmBarang) {
        this.namaBarang = nmBarang;
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

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public String getSisa() {
        return sisa;
    }

    public void setSisa(String sisa) {
        this.sisa = sisa;
    }

    public String getNoKonsinyasi() {
        return noKonsinyasi;
    }

    public void setNoKonsinyasi(String noKonsinyasi) {
        this.noKonsinyasi = noKonsinyasi;
    }

    public String getKdMerk() {
        return kdMerk;
    }

    public void setKdMerk(String kdMerk) {
        this.kdMerk = kdMerk;
    }

    public String getKdJenis() {
        return kdJenis;
    }

    public void setKdJenis(String kdJenis) {
        this.kdJenis = kdJenis;
    }
}

package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by Shin on 2/2/2017.
 */

public class KomisiDenda {

    private String namaSales, namaPelanggan, noNota, tanggal, piutang, potongan, jumlah, tanggalBayar, bayar, selisihHari, persenKomisiDenda, nilaiKomisiDenda, persenDenda, denda, pembayaran, nik;

    public KomisiDenda(){}
    // Komisi

    public KomisiDenda(String namaSales, String namaPelanggan, String noNota, String tanggal, String piutang, String potongan, String jumlah, String tanggalBayar, String bayar, String selisihHari, String persenKomisiDenda, String nilaiKomisiDenda, String pembayaran) {
        this.namaSales = namaSales;
        this.namaPelanggan = namaPelanggan;
        this.noNota = noNota;
        this.tanggal = tanggal;
        this.piutang = piutang;
        this.potongan = potongan;
        this.jumlah = jumlah;
        this.tanggalBayar = tanggalBayar;
        this.bayar = bayar;
        this.selisihHari = selisihHari;
        this.persenKomisiDenda = persenKomisiDenda;
        this.nilaiKomisiDenda = nilaiKomisiDenda;
        this.pembayaran = pembayaran;
    }

    public String getNamaSales() {
        return namaSales;
    }

    public void setNamaSales(String namaSales) {
        this.namaSales = namaSales;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getPiutang() {
        return piutang;
    }

    public void setPiutang(String piutang) {
        this.piutang = piutang;
    }

    public String getPotongan() {
        return potongan;
    }

    public void setPotongan(String potongan) {
        this.potongan = potongan;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(String tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }

    public String getBayar() {
        return bayar;
    }

    public void setBayar(String bayar) {
        this.bayar = bayar;
    }

    public String getSelisihHari() {
        return selisihHari;
    }

    public void setSelisihHari(String selisihHari) {
        this.selisihHari = selisihHari;
    }

    public String getPersenKomisiDenda() {
        return persenKomisiDenda;
    }

    public void setPersenKomisiDenda(String persenKomisiDenda) {
        this.persenKomisiDenda = persenKomisiDenda;
    }

    public String getNilaiKomisiDenda() {
        return nilaiKomisiDenda;
    }

    public void setNilaiKomisiDenda(String nilaiKomisiDenda) {
        this.nilaiKomisiDenda = nilaiKomisiDenda;
    }

    public String getPersenDenda() {
        return persenDenda;
    }

    public void setPersenDenda(String persenDenda) {
        this.persenDenda = persenDenda;
    }

    public String getDenda() {
        return denda;
    }

    public void setDenda(String denda) {
        this.denda = denda;
    }

    public String getPembayaran() {
        return pembayaran;
    }

    public void setPembayaran(String pembayaran) {
        this.pembayaran = pembayaran;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNoNota() {
        return noNota;
    }

    public void setNoNota(String noNota) {
        this.noNota = noNota;
    }
}
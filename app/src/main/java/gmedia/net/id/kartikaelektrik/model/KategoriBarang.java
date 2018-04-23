package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by indra on 28/12/2016.
 */

public class KategoriBarang {
    private String kodeKategori, namaKategori;

    public KategoriBarang(String kodeKategori, String namaKategori) {
        this.kodeKategori = kodeKategori;
        this.namaKategori = namaKategori;
    }

    public String getKodeKategori() {
        return kodeKategori;
    }

    public void setKodeKategori(String kdKategori) {
        this.kodeKategori = kdKategori;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String nmKategori) {
        this.namaKategori = nmKategori;
    }
}

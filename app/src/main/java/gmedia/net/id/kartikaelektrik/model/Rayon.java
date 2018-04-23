package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by Shin on 1/19/2017.
 */

public class Rayon {

    private String kode, rayon;

    public Rayon (String kode, String rayon){
        this.kode = kode;
        this.rayon = rayon;
    }

    @Override
    public String toString() {
        return this.rayon;
    }

    public String getRayon() {
        return rayon;
    }

    public void setRayon(String rayon) {
        this.rayon = rayon;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }
}

package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by Shin on 2/8/2017.
 */

public class OrderBukti {

    private String noBukti, nama;

    public OrderBukti(){}

    public OrderBukti(String noBukti, String nama) {
        this.noBukti = noBukti;
        this.nama = nama;
    }

    public String getNoBukti() {
        return noBukti;
    }

    public void setNoBukti(String noBukti) {
        this.noBukti = noBukti;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    @Override
    public String toString() {
        return this.noBukti;
    }
}

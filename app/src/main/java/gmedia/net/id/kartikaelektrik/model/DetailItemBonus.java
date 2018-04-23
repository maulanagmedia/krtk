package gmedia.net.id.kartikaelektrik.model;

/**
 * Created by Shin on 2/5/2017.
 */

public class DetailItemBonus {

    private String id, noBukti, noProgram, total, bonus, flag, nilai, keterangan, merk;

    public DetailItemBonus(){}

    public DetailItemBonus(String id, String noBukti, String noProgram, String total, String bonus, String flag, String nilai, String keterangan, String merk) {
        this.id = id;
        this.noBukti = noBukti;
        this.noProgram = noProgram;
        this.total = total;
        this.bonus = bonus;
        this.flag = flag;
        this.nilai = nilai;
        this.keterangan = keterangan;
        this.merk = merk;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoProgram() {
        return noProgram;
    }

    public void setNoProgram(String noProgram) {
        this.noProgram = noProgram;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getNoBukti() {
        return noBukti;
    }

    public void setNoBukti(String noBukti) {
        this.noBukti = noBukti;
    }
}



package gmedia.net.id.kartikaelektrik.util;

/**
 * Created by Shinmaul on 3/15/2018.
 */

public class ServerURL {

    private static final String baseURL = "http://kartika.gmedia.bz/api/";

    public static final String getSetoranHeader = baseURL + "Setoran/view_header_setoran";
    public static final String saveSetoran = baseURL + "Setoran/process";
    public static final String getMasterBayar = baseURL + "Barang/master_bayar";
}

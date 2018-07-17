package gmedia.net.id.kartikaelektrik.util;

/**
 * Created by Shinmaul on 3/15/2018.
 */

public class ServerURL {

    private static final String baseURL = "http://kartika.gmedia.bz/api/";

    public static final String getSetoranHeader = baseURL + "Setoran/view_header_setoran/";
    public static final String getSetoranDetail = baseURL + "Setoran/view_detail_setoran/";
    public static final String getDetailSetoran = baseURL + "Setoran/detail_setoran/";
    public static final String saveSetoran = baseURL + "Setoran/process/";
    public static final String deleteSetoran = baseURL + "Setoran/hapus/";
    public static final String getPiutangSales = baseURL + "Setoran/view_piutang/";
    public static final String getMutasiSetoran = baseURL + "Setoran/view_setoran_mutasi/";
    public static final String saveMutasiSetoran = baseURL + "Setoran/add_mutasi/";
    public static final String deleteMutasiSetoran = baseURL + "Setoran/hapus_mutasi/";
    public static final String getMasterBayar = baseURL + "Barang/master_bayar/";
    public static final String getDetailStokBarang = baseURL + "Barang/get_stok_detail/";
    public static final String getBarangTakLaku = baseURL + "Barangtaklaku/get_data/";
}

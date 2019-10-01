package gmedia.net.id.kartikaelektrik.util;

/**
 * Created by Shinmaul on 3/15/2018.
 */

public class ServerURL {

    private static final String baseURL = "http://api.kartikaelectric.com/";

    public static final String getSetoranHeader = baseURL + "Setoran/view_header_setoran/";
    public static final String getSetoranDetail = baseURL + "Setoran/view_detail_setoran/";
    public static final String getDetailRekapSetoran = baseURL + "setoran/view_setoran_details/";
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
    public static final String getBarangPalingLaku = baseURL + "BarangLaku/get_data/";
    public static final String getBarangPerKategori = baseURL + "Barang/barang_perkategori/";
    public static final String getDashboard = baseURL + "Dashboard/get_dashboard/";
    public static final String getLimitOrder = baseURL + "Canvas/limit_order/";
    public static final String getListSales = baseURL + "sales/";
    public static final String getSetoranPerNobukti = baseURL + "setoran/view_setoran_nobukti/";
    public static final String getSetoranNobukti = baseURL + "setoran/view_detail_nobukti/";
    public static final String getApproveSOBarang = baseURL + "barang/order_approve/";
    public static final String sendCustomerLimit = baseURL + "customer/limit/";
    public static final String getCustomerLimit  = baseURL + "customer/view_pengajuan/";
    public static final String getCustomerLimitApprove = baseURL + "customer/view_limit/";
    public static final String saveStatusCustomerLimit = baseURL + "customer/approval/";
    public static final String getGiroJatuhTempo = baseURL + "Giro/jatuh_tempo/";
    public static final String getLabaRugiOmsetJual = baseURL + "LabaRugi/penjualan_barang/";
    public static final String getLabaRugiOmsetSetoran = baseURL + "LabaRugi/pelunasan_piutang/";
    public static final String kunciSetoran = baseURL + "Setoran/kunci_setoran/";
    public static final String loginByNik = baseURL + "Auth/login_nik/";
    public static final String getHistoryBarangCanvas = baseURL + "Barang/history_canvas/";
    public static final String getStatus = baseURL + "Barang/master_status/";
    public static final String getCustomerHistoryLimit = baseURL + "customer/header_limit/";
    public static final String getCustomerHistoryLimitDetail = baseURL + "customer/detail_limit/";
    public static final String getHeaderImages = baseURL + "promo/view_promo/";
    public static final String saveHeaderImages = baseURL + "promo/index/";
    public static final String getSalesLocation = baseURL + "sales/log/";
    public static final String getCustomerDenda = baseURL + "Denda/view_customer/";
    public static final String getNotaCustomerDenda = baseURL + "Denda/list_nota/";
    public static final String savePengajuanHapusDenda = baseURL + "Denda/pengajuan/";
    public static final String getPengajuanHapusDendaCustomer = baseURL + "Denda/list_pengajuan/";
    public static final String getDetailPengajuanHapusDendaCustomer = baseURL + "Denda/apv_pengajuan/";
    public static final String saveApvHapusDenda = baseURL + "Denda/persetujuan/";
    public static final String getSummaryHapusDenda = baseURL + "Denda/view_denda/";
    public static final String getBarangPerKatogory = baseURL + "barang/all_barang/kategori/";
    public static final String savePengeluaran = baseURL + "pengeluaran/simpan_pengeluaran/";
    public static final String getPengeluaran = baseURL + "pengeluaran/view_pengeluaran/";
    public static final String getJenisPengeluaran = baseURL + "pengeluaran/view_jenis_pengeluaran/";
    public static final String getMenuAdmin = baseURL + "Auth/get_menu_admin/";
    public static final String getSalesAdmin = baseURL + "Sales/get_child/";
    public static final String deletePengeluaran = baseURL + "pengeluaran/delete_pengeluaran/";
    public static final String getTanggalTempo = baseURL + "Barang/get_tanggal_tempo/";
}

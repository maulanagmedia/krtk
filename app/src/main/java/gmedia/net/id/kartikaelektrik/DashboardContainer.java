package gmedia.net.id.kartikaelektrik;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuHistoryPenjualanBarang;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaAdmin;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaBonus;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaCustomer;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaCustomerLimit;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaCustomerOrder;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaCustomerOrderKh;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaDenda;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaEntryCanvas;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaEntryPaket;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaHapusDenda;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaInformasiStok;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaKomisi;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaOmsetManager;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaOmsetPenjualan;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaOmsetSales;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaOrderCustom;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaPengajuanTempo;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaPengeluaran;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaPermintaanHargaOrder;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaPotensiDenda;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaRetur;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaSalesOrder;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaSetoran;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaTagihanPiutang;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaTambahCanvas;

public class DashboardContainer extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment = null;
    Toolbar toolbar;
    private TextView tvTittleBar;
    private static int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gmedia.net.id.kartikaelektrik.R.layout.activity_dashboard_container);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

//        tvTittleBar = (TextView) findViewById(R.id.tv_title_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        FrameLayout flContainer = (FrameLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.fl_dashboard_container);
        flContainer.removeAllViews();

        fragmentManager = getFragmentManager();
        Bundle bundle = getIntent().getExtras();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        if(bundle != null){
            String kodeMenu = bundle.getString("kodemenu");

            switch (kodeMenu){
                case "tambahpelanggan":
                    state = 0;
                    MenuUtamaCustomer menuUtamaCustomer = new MenuUtamaCustomer();
                    View childLayoutCustomer = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_customer, flContainer);
                    menuUtamaCustomer.setView(DashboardContainer.this, childLayoutCustomer);
                    toolbar.setTitle("Tambah Pelanggan");
                    setTitle("Tambah Pelanggan");
                    break;
                case "permintaanhargaorder":
                    state = 1;
                    MenuUtamaPermintaanHargaOrder menuUtamaPermintaan = new MenuUtamaPermintaanHargaOrder();
                    View childLayoutPermintaan = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_permohonan_harga_order, flContainer);
                    menuUtamaPermintaan.setView(DashboardContainer.this, childLayoutPermintaan);
                    toolbar.setTitle("Permintaan Persetujuan Harga");
                    setTitle("Permintaan Persetujuan Harga");
                    break;
                case "tambahso":
                    state = 2;
                    MenuUtamaCustomerOrder menuUtamaCustomerOrder = new MenuUtamaCustomerOrder();
                    View childLayoutCustomerOrder = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_customer_order, flContainer);
                    menuUtamaCustomerOrder.setView(DashboardContainer.this, childLayoutCustomerOrder);
                    toolbar.setTitle("Pilih Nama Pelanggan");
                    setTitle("Pilih Nama Pelanggan");
                    break;
                case "entrypaket":
                    state = 3;
                    MenuUtamaEntryPaket menuUtamaEntryPaket = new MenuUtamaEntryPaket();
                    View childLayoutEntryPaket = inflater.inflate(R.layout.menu_utama_entry_paket, flContainer);
                    menuUtamaEntryPaket.setView(DashboardContainer.this, childLayoutEntryPaket);
                    toolbar.setTitle("Pilih Nama Pelanggan");
                    setTitle("Pilih Nama Pelanggan");
                    break;
                case "daftarso":
                    state = 4;
                    MenuUtamaSalesOrder menuUtamaSalesOrder = new MenuUtamaSalesOrder();
                    View childLayoutSO = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_sales_order, flContainer);
                    menuUtamaSalesOrder.setView(DashboardContainer.this, childLayoutSO);
                    toolbar.setTitle("Daftar Order");
                    setTitle("Daftar Order");
                    break;
                case "tagihanpiutang":
                    state = 5;
                    MenuUtamaTagihanPiutang menuUtamaPiutang = new MenuUtamaTagihanPiutang();
                    View childLayoutPiutang = inflater.inflate(R.layout.menu_utama_tagihan_piutang, flContainer);
                    menuUtamaPiutang.setView(DashboardContainer.this, childLayoutPiutang);
                    toolbar.setTitle("Tagihan / Piutang");
                    setTitle("Tagihan / Piutang");
                    break;
                case "infostok":
                    state = 6;
                    MenuUtamaInformasiStok menuUtamaInfoStok = new MenuUtamaInformasiStok();
                    View childLayoutStok = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_informasi_stok, flContainer);
                    menuUtamaInfoStok.setView(DashboardContainer.this, childLayoutStok);
                    toolbar.setTitle("Informasi Stok");
                    setTitle("Informasi Stok");
                    break;
                case "tambahcanvas":
                    state = 7;
                    MenuUtamaTambahCanvas menuUtamaTambahCanvas = new MenuUtamaTambahCanvas();
                    View childLayoutTambahCanvas = inflater.inflate(R.layout.menu_utama_tambah_canvas, flContainer);
                    menuUtamaTambahCanvas.setView(DashboardContainer.this, childLayoutTambahCanvas);
                    toolbar.setTitle("Pilih Customer");
                    setTitle("Pilih Customer");
                    break;
                case "entrycanvas":
                    state = 8;
                    MenuUtamaEntryCanvas menuUtamaEntryCanvas = new MenuUtamaEntryCanvas();
                    View childLayoutCanvas = inflater.inflate(R.layout.menu_utama_entry_canvas, flContainer);
                    menuUtamaEntryCanvas.setView(DashboardContainer.this, childLayoutCanvas);
                    toolbar.setTitle("Penjualan Canvas");
                    setTitle("Penjualan Canvas");
                    break;
                case "komisi":
                    state = 9;
                    MenuUtamaKomisi menuUtamaKomisi = new MenuUtamaKomisi();
                    View childLayoutKomisi = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_komisi, flContainer);
                    menuUtamaKomisi.setView(DashboardContainer.this, childLayoutKomisi);
                    toolbar.setTitle("Komisi");
                    setTitle("Komisi");
                    break;
                case "denda":
                    state = 10;
                    MenuUtamaDenda menuUtamaDenda = new MenuUtamaDenda();
                    View childLayoutDenda = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_denda, flContainer);
                    menuUtamaDenda.setView(DashboardContainer.this, childLayoutDenda);
                    toolbar.setTitle("Denda");
                    setTitle("Denda");
                    break;
                case "bonus":
                    state = 11;
                    //fragment = new MenuUtamaBonus();
                    MenuUtamaBonus menuUtamaBonus = new MenuUtamaBonus();
                    View childLayoutBonus = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_bonus, flContainer);
                    menuUtamaBonus.setView(DashboardContainer.this, childLayoutBonus);
                    toolbar.setTitle("Bonus");
                    setTitle("Bonus");
                    break;
                case "retur":
                    state = 12;
                    //fragment = new MenuUtamaRetur();
                    MenuUtamaRetur menuUtamaRetur = new MenuUtamaRetur();
                    View childLayoutRetur = inflater.inflate(R.layout.menu_utama_retur, flContainer);
                    menuUtamaRetur.setView(DashboardContainer.this, childLayoutRetur);
                    toolbar.setTitle("Retur");
                    setTitle("Retur");
                    break;
                case "omsetsales":
                    state = 13;
                    //fragment = new MenuUtamaOmsetSales();
                    MenuUtamaOmsetSales menuUtamaOmsetSales = new MenuUtamaOmsetSales();
                    View childLayoutOmsetSales = inflater.inflate(R.layout.menu_utama_omset_sales, flContainer);
                    menuUtamaOmsetSales.setView(DashboardContainer.this, childLayoutOmsetSales);
                    toolbar.setTitle("Omset Sales");
                    setTitle("Omset Sales");
                    break;
                case "omsetpenjualan":
                    state = 14;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaOmsetPenjualan menuUtamaOmsetPenjualan = new MenuUtamaOmsetPenjualan();
                    View childLayoutOmsetPenjualan = inflater.inflate(R.layout.menu_utama_omset_penjualan, flContainer);
                    menuUtamaOmsetPenjualan.setView(DashboardContainer.this, childLayoutOmsetPenjualan);
                    toolbar.setTitle("Omset Sales");
                    setTitle("Omset Sales");
                    break;
                case "ordercustom":
                    state = 15;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaOrderCustom menuUtamaOrderCustom = new MenuUtamaOrderCustom();
                    View childLayoutOrderCustom = inflater.inflate(R.layout.menu_utama_order_custom, flContainer);
                    menuUtamaOrderCustom.setView(DashboardContainer.this, childLayoutOrderCustom);
                    toolbar.setTitle("Daftar Order Custom");
                    setTitle("Daftar Order Custom");
                    break;
                case "setoran":
                    state = 16;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaSetoran menuUtamaSetoran = new MenuUtamaSetoran();
                    View childLayoutSetoran = inflater.inflate(R.layout.menu_utama_setoran, flContainer);
                    menuUtamaSetoran.setView(DashboardContainer.this, childLayoutSetoran);
                    toolbar.setTitle("Setoran");
                    setTitle("Setoran");
                    break;
                case "barangtaklaku":
                    state = 17;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuHistoryPenjualanBarang menuUtamaBarangTakLaku = new MenuHistoryPenjualanBarang();
                    View childLayoutBarangTakLaku = inflater.inflate(R.layout.menu_utama_history_penjualan_barang, flContainer);
                    menuUtamaBarangTakLaku.setView(DashboardContainer.this, childLayoutBarangTakLaku);
                    toolbar.setTitle("History Penjualan Barang");
                    setTitle("History Penjualan Barang");
                    break;
                case "menuadmin":
                    state = 18;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaAdmin menuUtamaAdmin = new MenuUtamaAdmin();
                    View childLayoutMenuAdmin = inflater.inflate(R.layout.menu_utama_admin, flContainer);
                    menuUtamaAdmin.setView(DashboardContainer.this, childLayoutMenuAdmin);
                    toolbar.setTitle("Menu Admin");
                    setTitle("Menu Admin");
                    break;
                case "menucustomerlimit":
                    state = 19;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaCustomerLimit menuUtamaCustomerLimit = new MenuUtamaCustomerLimit();
                    View childLayoutMenuCustomerLimit = inflater.inflate(R.layout.menu_utama_customer_limit, flContainer);
                    menuUtamaCustomerLimit.setView(DashboardContainer.this, childLayoutMenuCustomerLimit);
                    toolbar.setTitle("Customer Limit");
                    setTitle("Customer Limit");
                    break;
                case "menuhapusdenda":
                    state = 20;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaHapusDenda menuUtamaHapusDenda = new MenuUtamaHapusDenda();
                    View childLayoutMenuHapusDenda = inflater.inflate(R.layout.menu_utama_hapus_denda, flContainer);
                    menuUtamaHapusDenda.setView(DashboardContainer.this, childLayoutMenuHapusDenda);
                    toolbar.setTitle("Hapus Denda");
                    setTitle("Hapus Denda");
                    break;
                case "menupengeluaran":
                    state = 21;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaPengeluaran menuUtamaPengeluaran = new MenuUtamaPengeluaran();
                    View childLayoutMenuPengeluaran = inflater.inflate(R.layout.menu_utama_pengeluaran, flContainer);
                    menuUtamaPengeluaran.setView(DashboardContainer.this, childLayoutMenuPengeluaran);
                    toolbar.setTitle("Rekap Pengeluaran");
                    setTitle("Rekap Pengeluaran");
                    break;
                case "menupengajuantempo":
                    state = 22;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaPengajuanTempo menuUtamaPengajuanTempo = new MenuUtamaPengajuanTempo();
                    View childPengajuanTempo = inflater.inflate(R.layout.menu_utama_pengajuan_tempo, flContainer);
                    menuUtamaPengajuanTempo.setView(DashboardContainer.this, childPengajuanTempo);
                    toolbar.setTitle("Pengajuan Tempo");
                    setTitle("Pengajuan Tempo");
                    break;
                case "menupotensidenda":
                    state = 23;
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaPotensiDenda menuUtamaPotensiDenda = new MenuUtamaPotensiDenda();
                    View childPotensiDenda = inflater.inflate(R.layout.menu_utama_potensi_denda, flContainer);
                    menuUtamaPotensiDenda.setView(DashboardContainer.this, childPotensiDenda);
                    toolbar.setTitle("Potensi Denda");
                    setTitle("Potensi Denda");
                    break;
                case "tambahsokhusus":
                    state = 24;
                    MenuUtamaCustomerOrderKh menuUtamaCustomerOrderKh = new MenuUtamaCustomerOrderKh();
                    View childLayoutCustomerOrderKh = inflater.inflate(R.layout.menu_utama_customer_orderkh, flContainer);
                    menuUtamaCustomerOrderKh.setView(DashboardContainer.this, childLayoutCustomerOrderKh);
                    toolbar.setTitle("Pilih Nama Pelanggan");
                    setTitle("Pilih Nama Pelanggan");
                    break;
                case "menuomsetmanager":
                    state = 24;
                    MenuUtamaOmsetManager menuUtamaOmsetManager = new MenuUtamaOmsetManager();
                    View childLayoutOmsetManager = inflater.inflate(R.layout.fragment_menu_utama_omset_manager, flContainer);
                    menuUtamaOmsetManager.setView(DashboardContainer.this, childLayoutOmsetManager);
                    toolbar.setTitle("Omset Manager");
                    setTitle("Omset Manager");
                    break;
                default:
                    fragment = new MenuUtamaCustomer();
                    callFragment(fragment);
                    break;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(state == 16){

            //MenuUtamaSetoran.getDataSetoran();
        }
    }

    private void callFragment(Fragment fragment) {

        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.replace(gmedia.net.id.kartikaelektrik.R.id.fl_dashboard_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
//        Intent intent = new Intent(DashboardContainer.this, Dashboard.class);
        Intent intent = new Intent(DashboardContainer.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        finish();
    }
}

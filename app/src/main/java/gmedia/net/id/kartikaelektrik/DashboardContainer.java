package gmedia.net.id.kartikaelektrik;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaBonus;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaCustomer;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaCustomerOrder;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaDenda;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaEntryCanvas;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaEntryPaket;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaInformasiStok;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaKomisi;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaOmsetPenjualan;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaOmsetSales;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaOrderCustom;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaPermintaanHargaOrder;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaRetur;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaSalesOrder;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaTagihanPiutang;
import gmedia.net.id.kartikaelektrik.navMenuUtama.MenuUtamaTambahCanvas;

public class DashboardContainer extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment = null;
    Toolbar toolbar;
    private TextView tvTittleBar;

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
                    MenuUtamaCustomer menuUtamaCustomer = new MenuUtamaCustomer();
                    View childLayoutCustomer = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_customer, flContainer);
                    menuUtamaCustomer.setView(DashboardContainer.this, childLayoutCustomer);
                    toolbar.setTitle("Tambah Pelanggan");
                    setTitle("Tambah Pelanggan");
                    break;
                case "permintaanhargaorder":
                    MenuUtamaPermintaanHargaOrder menuUtamaPermintaan = new MenuUtamaPermintaanHargaOrder();
                    View childLayoutPermintaan = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_permohonan_harga_order, flContainer);
                    menuUtamaPermintaan.setView(DashboardContainer.this, childLayoutPermintaan);
                    toolbar.setTitle("Permintaan Persetujuan Harga");
                    setTitle("Permintaan Persetujuan Harga");
                    break;
                case "tambahso":
                    MenuUtamaCustomerOrder menuUtamaCustomerOrder = new MenuUtamaCustomerOrder();
                    View childLayoutCustomerOrder = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_customer_order, flContainer);
                    menuUtamaCustomerOrder.setView(DashboardContainer.this, childLayoutCustomerOrder);
                    toolbar.setTitle("Pilih Nama Pelanggan");
                    setTitle("Pilih Nama Pelanggan");
                    break;
                case "entrypaket":
                    MenuUtamaEntryPaket menuUtamaEntryPaket = new MenuUtamaEntryPaket();
                    View childLayoutEntryPaket = inflater.inflate(R.layout.menu_utama_entry_paket, flContainer);
                    menuUtamaEntryPaket.setView(DashboardContainer.this, childLayoutEntryPaket);
                    toolbar.setTitle("Pilih Nama Pelanggan");
                    setTitle("Pilih Nama Pelanggan");
                    break;
                case "daftarso":
                    MenuUtamaSalesOrder menuUtamaSalesOrder = new MenuUtamaSalesOrder();
                    View childLayoutSO = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_sales_order, flContainer);
                    menuUtamaSalesOrder.setView(DashboardContainer.this, childLayoutSO);
                    toolbar.setTitle("Daftar Order");
                    setTitle("Daftar Order");
                    break;
                case "tagihanpiutang":
                    MenuUtamaTagihanPiutang menuUtamaPiutang = new MenuUtamaTagihanPiutang();
                    View childLayoutPiutang = inflater.inflate(R.layout.menu_utama_tagihan_piutang, flContainer);
                    menuUtamaPiutang.setView(DashboardContainer.this, childLayoutPiutang);
                    toolbar.setTitle("Tagihan / Piutang");
                    setTitle("Tagihan / Piutang");
                    break;
                case "infostok":
                    MenuUtamaInformasiStok menuUtamaInfoStok = new MenuUtamaInformasiStok();
                    View childLayoutStok = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_informasi_stok, flContainer);
                    menuUtamaInfoStok.setView(DashboardContainer.this, childLayoutStok);
                    toolbar.setTitle("Informasi Stok");
                    setTitle("Informasi Stok");
                    break;
                case "tambahcanvas":
                    MenuUtamaTambahCanvas menuUtamaTambahCanvas = new MenuUtamaTambahCanvas();
                    View childLayoutTambahCanvas = inflater.inflate(R.layout.menu_utama_tambah_canvas, flContainer);
                    menuUtamaTambahCanvas.setView(DashboardContainer.this, childLayoutTambahCanvas);
                    toolbar.setTitle("Pilih Customer");
                    setTitle("Pilih Customer");
                    break;
                case "entrycanvas":
                    MenuUtamaEntryCanvas menuUtamaEntryCanvas = new MenuUtamaEntryCanvas();
                    View childLayoutCanvas = inflater.inflate(R.layout.menu_utama_entry_canvas, flContainer);
                    menuUtamaEntryCanvas.setView(DashboardContainer.this, childLayoutCanvas);
                    toolbar.setTitle("Penjualan Canvas");
                    setTitle("Penjualan Canvas");
                    break;
                case "komisi":
                    MenuUtamaKomisi menuUtamaKomisi = new MenuUtamaKomisi();
                    View childLayoutKomisi = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_komisi, flContainer);
                    menuUtamaKomisi.setView(DashboardContainer.this, childLayoutKomisi);
                    toolbar.setTitle("Komisi");
                    setTitle("Komisi");
                    break;
                case "denda":
                    MenuUtamaDenda menuUtamaDenda = new MenuUtamaDenda();
                    View childLayoutDenda = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_denda, flContainer);
                    menuUtamaDenda.setView(DashboardContainer.this, childLayoutDenda);
                    toolbar.setTitle("Denda");
                    setTitle("Denda");
                    break;
                case "bonus":
                    //fragment = new MenuUtamaBonus();
                    MenuUtamaBonus menuUtamaBonus = new MenuUtamaBonus();
                    View childLayoutBonus = inflater.inflate(gmedia.net.id.kartikaelektrik.R.layout.menu_utama_bonus, flContainer);
                    menuUtamaBonus.setView(DashboardContainer.this, childLayoutBonus);
                    toolbar.setTitle("Bonus");
                    setTitle("Bonus");
                    break;
                case "retur":
                    //fragment = new MenuUtamaRetur();
                    MenuUtamaRetur menuUtamaRetur = new MenuUtamaRetur();
                    View childLayoutRetur = inflater.inflate(R.layout.menu_utama_retur, flContainer);
                    menuUtamaRetur.setView(DashboardContainer.this, childLayoutRetur);
                    toolbar.setTitle("Retur");
                    setTitle("Retur");
                    break;
                case "omsetsales":
                    //fragment = new MenuUtamaOmsetSales();
                    MenuUtamaOmsetSales menuUtamaOmsetSales = new MenuUtamaOmsetSales();
                    View childLayoutOmsetSales = inflater.inflate(R.layout.menu_utama_omset_sales, flContainer);
                    menuUtamaOmsetSales.setView(DashboardContainer.this, childLayoutOmsetSales);
                    toolbar.setTitle("Omset Sales");
                    setTitle("Omset Sales");
                    break;
                case "omsetpenjualan":
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaOmsetPenjualan menuUtamaOmsetPenjualan = new MenuUtamaOmsetPenjualan();
                    View childLayoutOmsetPenjualan = inflater.inflate(R.layout.menu_utama_omset_penjualan, flContainer);
                    menuUtamaOmsetPenjualan.setView(DashboardContainer.this, childLayoutOmsetPenjualan);
                    toolbar.setTitle("Omset Sales");
                    setTitle("Omset Sales");
                    break;
                case "ordercustom":
                    //fragment = new MenuUtamaOmsetPenjualan();
                    MenuUtamaOrderCustom menuUtamaOrderCustom = new MenuUtamaOrderCustom();
                    View childLayoutOrderCustom = inflater.inflate(R.layout.menu_utama_order_custom, flContainer);
                    menuUtamaOrderCustom.setView(DashboardContainer.this, childLayoutOrderCustom);
                    toolbar.setTitle("Daftar Order Custom");
                    setTitle("Daftar Order Custom");
                    break;
                default:
                    fragment = new MenuUtamaCustomer();
                    callFragment(fragment);
                    break;
            }
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

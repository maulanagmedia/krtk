package gmedia.net.id.kartikaelektrik.util;

import android.app.ProgressDialog;
import android.content.Context;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.Barang;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shin on 1/18/2017.
 * Description : class to update Master Data after desire time
 */

public class MasterDataHandler {

    private String lastDate;
    private String formatDate;
    private final int numberDay = 7; // check every 7 Days
    private ItemValidation iv = new ItemValidation();
    private Context context;
    private SharedPreferenceHandler sph = new SharedPreferenceHandler();
    private String urlGetAllBarang, urlGetAllKategori, urlgetAllBarangByKategori;
    List<Barang> barangList;

    public MasterDataHandler(Context context){
        this.context = context;

        lastDate = sph.getLastMasterDataUpdate(context);

        // if there's no last date, update with today
        formatDate = context.getResources().getString(R.string.format_date);
        if(lastDate == null){
            sph.setLastMasterDataUpdate(context,iv.getCurrentDate(formatDate));
            lastDate = iv.getCurrentDate(formatDate);
        }

        urlGetAllBarang = context.getResources().getString(R.string.url_get_all_barang);
        urlGetAllKategori = context.getResources().getString(R.string.url_get_all_kategori_barang);
        urlgetAllBarangByKategori = context.getResources().getString(R.string.url_get_all_barang_by_kategori);

    }

    public void checkWeeklyUpdate(){
        // check today
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(lastDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, numberDay);
        Date dateLast = c.getTime();
        Date dateCurrent = null;
        try {
            dateCurrent = sdf.parse(iv.getCurrentDate(formatDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // if expired day match with today
        if(dateCurrent.after(dateLast) || dateLast.equals(dateCurrent)){
            sph.setLastMasterDataUpdate(context,sdf.format(dateCurrent));

            updateMasterData();
        }
    }

    // Master Data Barang
    public void updateMasterData(){

        // Get All Barang
        JSONObject jsonBody = new JSONObject();
        final ProgressDialog progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Retro_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating master data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiVolley restService = new ApiVolley(context, jsonBody, "GET", urlGetAllBarang , "Master data has been updated", "Failed to Download Master Data", 1,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            JSONArray arrayJSON = responseAPI.getJSONArray("response");
                            barangList = new ArrayList<Barang>();

                            for(int i = 0; i < arrayJSON.length(); i++){
                                JSONObject jo = arrayJSON.getJSONObject(i);
                                barangList.add(new Barang(jo.getString("kdbrg"),jo.getString("namabrg"),jo.getString("stok"),jo.getString("hargajual"),jo.getString("kdkat")));
                            }

                            if(barangList.size() > 0){
                                ArrayList<Barang> itemListBarang = new ArrayList<>(barangList);
                                sph.saveListBarang(context, itemListBarang);
//                                updateKategoryBarangData();
                                FilterBarangByKategori();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(String result) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void FilterBarangByKategori() {

        // Filter By Pembayaran
        HashMap<String, List<Barang>> filteredBarang = new HashMap<String, List<Barang>>();

        List<Barang> list0 = new ArrayList<Barang>();

        for (Barang barang : barangList) {
            String key  = barang.getIdKategori();
            if(filteredBarang.containsKey(key)){
                List<Barang> list = filteredBarang.get(key);
                list.add(barang);

            }else{
                List<Barang> list = new ArrayList<Barang>();
                list.add(barang);
                filteredBarang.put(key, list);
            }
            list0.add(barang);
        }

        filteredBarang.put("0", list0);

        // Autocomplete & Table item
        sph.deleteListKategoriBarang(context);
        for(String key: filteredBarang.keySet()){
            List<Barang> list = filteredBarang.get(key);
            ArrayList<Barang> kategoriList = new ArrayList<>(list);
            sph.saveListBarangToLocalByKategori(context, kategoriList, key);
        }
    }

    // Get All kateory
    private void updateKategoryBarangData() {

        // Get All Kategori Barang
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(context, jsonBody, "GET", urlGetAllKategori, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                sph.deleteListKategoriBarang(context);
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    String id = jo.getString("kdkat");
                                    updateBarangByCategory(id);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                    }
                });
    }

    // Save Barang By Category
    private void updateBarangByCategory(final String idKat) {

        List<Barang> barangListByKat = new ArrayList<Barang>();

        if (barangList.size() > 0){

            for(Barang barang: barangList){
                if(barang.getIdKategori().toUpperCase().equals(idKat.toString())) barangListByKat.add(new Barang(barang.getKodeBarang(),barang.getNamaBarang()));
            }

            ArrayList<Barang> barangByKatMasterData = new ArrayList<>(barangListByKat);
            if(barangByKatMasterData.size() > 0) sph.saveListBarangToLocalByKategori(context, barangByKatMasterData, idKat);
        }
    }
}
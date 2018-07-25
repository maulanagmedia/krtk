package gmedia.net.id.kartikaelektrik.util;

import android.content.Context;
import android.content.SharedPreferences;

import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.model.Customer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by indra on 30/12/2016.
 */

public class SharedPreferenceHandler {

    public void saveListBarang(Context context, ArrayList<Barang> items){
        SharedPreferences.Editor editor = context.getSharedPreferences("Barang", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String jsonItems = gson.toJson(items);
        editor.putString("listBarang", jsonItems);
        editor.commit();
    }

    public ArrayList<Barang> getListBarang(Context context){

        ArrayList<Barang> items = new ArrayList<Barang>();
        Type tipeBarang = new TypeToken<List<Barang>>(){}.getType();
        Gson gson = new Gson();

        SharedPreferences prefs = context.getSharedPreferences("Barang", MODE_PRIVATE);
        String rawListBarang = prefs.getString("listBarang", "");
        if(rawListBarang != null){
            items = gson.fromJson(rawListBarang,tipeBarang);
        }

        return items;
    }

    public void deleteListKategoriBarang(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("ListBarang", MODE_PRIVATE).edit();
        editor.clear().commit();
    }

    public void saveListBarangToLocalByKategori(Context context, ArrayList<Barang> items, String idKategori){
        SharedPreferences.Editor editor = context.getSharedPreferences("ListBarang", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String jsonItems = gson.toJson(items);
        editor.putString("listBarang" + idKategori, jsonItems);
        editor.commit();
    }

    public ArrayList<Barang> getListBarangByCategoryFromLocal(Context context, String idKategori){

        ArrayList<Barang> items = new ArrayList<Barang>();
        Type tipeBarang = new TypeToken<List<Barang>>(){}.getType();
        Gson gson = new Gson();

        SharedPreferences prefs = context.getSharedPreferences("ListBarang", MODE_PRIVATE);
        String rawListBarang = prefs.getString("listBarang" + idKategori, null);
//        prefs.edit().remove("listBarang"+idKategori).commit(); // delete row
        if(rawListBarang != null){
            items = gson.fromJson(rawListBarang,tipeBarang);
        }

        return items;
    }

    public void saveLastSelectedCustomer(Context context, Customer customer){
        SharedPreferences.Editor editor = context.getSharedPreferences("ListSelectedCustomer", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String jsonItems = gson.toJson(customer);
        editor.putString("customer", jsonItems);
        editor.commit();
    }

    public Customer getLastSelectedCustomer(Context context){

        Customer customer = null;
        Type customerType = new TypeToken<Customer>(){}.getType();
        Gson gson = new Gson();

        SharedPreferences prefs = context.getSharedPreferences("ListSelectedCustomer", MODE_PRIVATE);
        String customerString = prefs.getString("customer", "");
        if(customerString != null){
            customer = gson.fromJson(customerString,customerType);
        }

        return customer;
    }

    public void setLastMasterDataUpdate(Context context, String date){
        SharedPreferences.Editor editor = context.getSharedPreferences("MasterData", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        editor.putString("date", date);
        editor.commit();
    }

    public String getLastMasterDataUpdate(Context context){

        SharedPreferences prefs = context.getSharedPreferences("MasterData", MODE_PRIVATE);
        String lastDate = prefs.getString("date", "");
        return lastDate;
    }

    public void setLastUpdatedLocationDate(Context context, String date){
        SharedPreferences.Editor editor = context.getSharedPreferences("Location", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        editor.putString("date", date);
        editor.commit();
    }

    public String getLastUpdatedLocationDate(Context context){

        SharedPreferences prefs = context.getSharedPreferences("Location", MODE_PRIVATE);
        String lastDate = prefs.getString("date", "");
        return lastDate;
    }
}

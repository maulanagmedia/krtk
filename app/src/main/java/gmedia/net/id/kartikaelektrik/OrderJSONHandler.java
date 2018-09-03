package gmedia.net.id.kartikaelektrik;

/**
 * Created by indra on 15/12/2016.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderJSONHandler {

    // For Sales Order
    public static String[] nobukti;
    public static String[] kdcus;
    public static String[] nik;
    public static String[] tgl;
    public static String[] tgltempo;
    public static String[] nopo;
    public static String[] kirimanText;
    public static String[] kiriman;
    public static String[] total;
    public static String[] keterangan;
    public static String[] userid;
    public static String[] useru;
    public static String[] usertgl;
    public static String[] status;
    public static String[] biayalain;
    public static String[] flag;
    public static String[] nama;
    public static String[] alamat;
    public static String[] namaSales;

    public static final String KEY_NoBukti = "nobukti";
    public static final String KEY_KDCus = "kdcus";
    public static final String KEY_NIK = "nik";
    public static final String KEY_TGL = "tgl";
    public static final String KEY_TGLTempo = "tgltempo";
    public static final String KEY_NOPO = "nopo";
    public static final String KEY_Kiriman_Text = "kiriman_text";
    public static final String KEY_Kiriman = "kiriman";
    public static final String KEY_Total = "total";
    public static final String KEY_Keterangan = "keterangan";
    public static final String KEY_UserID = "userid";
    public static final String KEY_UserU = "useru";
    public static final String KEY_UserTGL = "usertgl";
    public static final String KEY_Status = "status";
    public static final String KEY_BiayaLain = "biayalain";
    public static final String KEY_Flag = "flag";
    public static final String KEY_Nama = "nama";
    public static final String KEY_Alamat = "alamat";
    public static final String KEY_Nama_Sales = "nama_sales";

    public static final String JSON_METADATA = "metadata";
    public static final String JSON_ARRAY = "response";

    private JSONArray salesOrders = null;
    public  String formProses="";

    private String json;

    public OrderJSONHandler(String json, String proses){
        this.json = json;
        this.formProses = proses;
    }

    public void ParseOrderJSON(){
        JSONObject jsonObject=null;
        try {

            jsonObject = new JSONObject(json);
            salesOrders = jsonObject.getJSONArray(JSON_ARRAY);

            nobukti = new String[salesOrders.length()];
            kdcus = new String[salesOrders.length()];
            nik = new String[salesOrders.length()];
            tgl = new String[salesOrders.length()];
            tgltempo = new String[salesOrders.length()];
            nopo = new String[salesOrders.length()];
            kirimanText = new String[salesOrders.length()];
            kiriman = new String[salesOrders.length()];
            total = new String[salesOrders.length()];
            keterangan = new String[salesOrders.length()];
            userid = new String[salesOrders.length()];
            useru = new String[salesOrders.length()];
            usertgl = new String[salesOrders.length()];
            status = new String[salesOrders.length()];
            biayalain = new String[salesOrders.length()];
            flag = new String[salesOrders.length()];
            nama = new String[salesOrders.length()];
            alamat = new String[salesOrders.length()];
            namaSales = new String[salesOrders.length()];

            if (formProses == "all"){
                for(int i=0;i<salesOrders.length();i++){
                    JSONObject jo = salesOrders.getJSONObject(i);

                    nobukti[i] = jo.getString(KEY_NoBukti);
                    kdcus[i] = jo.getString(KEY_KDCus);
                    nik[i] = jo.getString(KEY_NIK);
                    tgl[i] = jo.getString(KEY_TGL);
                    tgltempo[i] = jo.getString(KEY_TGLTempo);
                    nopo[i] = jo.getString(KEY_NOPO);
                    kirimanText[i] = jo.getString(KEY_Kiriman_Text);
                    kiriman[i] = jo.getString(KEY_Kiriman);
                    total[i] = jo.getString(KEY_Total);
                    keterangan[i] = jo.getString(KEY_Keterangan);
                    userid[i] = jo.getString(KEY_UserID);
                    useru[i] = jo.getString(KEY_UserU);
                    usertgl[i] = jo.getString(KEY_UserTGL);
                    status[i] = jo.getString(KEY_Status);
                    biayalain[i] = jo.getString(KEY_BiayaLain);
                    flag[i] = jo.getString(KEY_Flag);
                    nama[i] = jo.getString(KEY_Nama);
                    alamat[i] = jo.getString(KEY_Alamat);
                    namaSales[i] = jo.getString(KEY_Nama_Sales);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

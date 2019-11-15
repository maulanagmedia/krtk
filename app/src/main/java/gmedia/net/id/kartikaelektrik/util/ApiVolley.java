package gmedia.net.id.kartikaelektrik.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by indra on 16/12/2016.
 * Description : Class to Access Rest API with Volley
 */

public class ApiVolley {

    public static RequestQueue requestQueue;
    private SessionManager session;
    private String token = "";
    private String id = "";
    private String level = "";
    private String laba = "";
    private String nikAsli = "", username = "";
    private ItemValidation iv = new ItemValidation();

    public ApiVolley(final Context context, JSONObject jsonBody, String requestMethod, final String REST_URL, final String successDialog, final String failDialog, final int showDialogFlag, final VolleyCallback callback){

        /*
        context : Application context
        jsonBody : jsonBody (usually be used for POST and PUT)
        requestMethod : GET, POST, PUT, DELETE
        REST_URL : Rest API URL
        successDialog : custom Dialog when success call API
        failDialog : custom Dialog when failed call API
        showDialogFlag : 1 = show successDialog / failDialog with filter
        callback : return of the response
        */

        session = new SessionManager(context);

        HashMap<String, String> user = session.getUserDetails();
        token = user.get(SessionManager.TAG_TOKEN);
        id = user.get(SessionManager.TAG_UID);
        level = user.get(SessionManager.TAG_LEVEL);
        laba = user.get(SessionManager.TAG_LABA);
        nikAsli = user.get(SessionManager.TAG_NIK_ASLI);
        username = user.get(SessionManager.TAG_USERNAME);

        final String requestBody = jsonBody.toString();

        int method = 0;

        switch(requestMethod.toUpperCase()){

            case "GET" :
                method = Request.Method.GET;
                break;
            case "POST" :
                method = Request.Method.POST;
                break;
            case "PUT" :
                method = Request.Method.PUT;
                break;
            case "DELETE" :
                method = Request.Method.DELETE;
                break;
            default: method = Request.Method.GET;
                break;
        }

        //region initial of stringRequest
        StringRequest stringRequest = new StringRequest(method, REST_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){

                String ok = REST_URL;
                if(response == null || response.equals("null")){

                    Toast.makeText(context, "Anda tidak memiliki ijin untuk mengakses halaman ini", Toast.LENGTH_LONG).show();
                    try {
                        context.stopService(new Intent(context, LocationUpdater.class));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(session.isLoggedIn()){

                        session.logoutUser((Activity) context);
                    }

                    return;
                }

                // Important Note : need to use try catch when parsing JSONObject, no need when parsing string
                try {

                    JSONObject responseAPI = new JSONObject(response);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 401) { // unauthorized

                        try {
                            context.stopService(new Intent(context, LocationUpdater.class));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        String message = responseAPI.getJSONObject("metadata").getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                        if (session.isLoggedIn()) {

                            session.logoutUser((Activity)context);
                        }
                        responseAPI = null;

                    }else{

                        if(status != null){
                            responseAPI = null;
                            callback.onSuccess(response);
                        }else{
                            callback.onError(response);
                            responseAPI = null;
                            Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                        }
                    }
                    responseAPI = null;
                    ShowCustomDialog(context, showDialogFlag, successDialog);

                } catch (Exception e) {

                    e.printStackTrace();
//                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                    callback.onError(response);

                }

            }
        }, new Response.ErrorListener() {
            @Override

            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
                //ShowCustomDialog(context,showDialogFlag,failDialog);
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                return;
            }
        }) {

            // Request Header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Client-Service", "frontend-client");
                params.put("Auth-Key", "gmedia_kartika");
                params.put("token", token);
                params.put("id", id);
                params.put("level", level);
                params.put("laba", laba);
                params.put("Nik-Asli", nikAsli);
                params.put("username", username);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };
        //endregion

        if(requestQueue == null) requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        // retry when timeout
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                40*1000, -1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
        requestQueue.getCache().clear();

    }

    // interface for call callback from response API
    public interface VolleyCallback{
        void onSuccess(String result);
        void onError(String result);
    }

    public void ShowCustomDialog(Context context, int flag, String message){
        if(flag == 1){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}

package gmedia.net.id.kartikaelektrik.activitySetoran.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.TambahSetoran;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

/**
 * Created by Shin on 1/19/2017.
 */

public class SetoranPernobuktiAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private View listViewItem;
    private int rowPerTableItem;
    private ProgressDialog progressDialog;

    private ItemValidation iv = new ItemValidation();

    public SetoranPernobuktiAdapter(Activity context, List items) {
        super(context, R.layout.adapter_setoran_pernota, items);
        this.context = context;
        this.items = items;
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
        private LinearLayout container, llFile;
        private TextView item1;
        private TextView item2;
        private TextView item3;
        private TextView item4;
        private TextView tvFile;
        private ImageView ivDelete;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_setoran_pernota, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.llFile = (LinearLayout) convertView.findViewById(R.id.ll_file);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.tvFile = (TextView) convertView.findViewById(R.id.tv_file);
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomListItem item = items.get(position);
        holder.item1.setText(item.getListItem1());
        holder.item2.setText(item.getListItem2());

        String formatDate = context.getResources().getString(R.string.format_date1);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        holder.item3.setText(iv.ChangeFormatDateString(item.getListItem3(), formatDate, formatDateDisplay));
        holder.item4.setText(iv.ChangeToCurrencyFormat(item.getListItem4()));

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setIcon(R.mipmap.logo_kartika)
                        .setMessage("Anda yakin ingin menghapus setoran " + item.getListItem1() + " ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                deleteData(item.getListItem1());
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        if(item.getListItem6().equals("0")){

            holder.llFile.setVisibility(View.GONE);
        }else{

            holder.llFile.setVisibility(View.VISIBLE);
        }

        holder.tvFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DownloadFileFromURL().execute(item.getListItem5());
            }
        });

        return convertView;
    }

    public void deleteData(String nobukti) {

        final ProgressDialog progressDialog = new ProgressDialog(context,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menghapus...");
        progressDialog.show();

        ApiVolley apiVolley = new ApiVolley(context, new JSONObject(), "GET", ServerURL.deleteSetoran+nobukti, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                progressDialog.dismiss();
                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        TambahSetoran.getDataSetoran();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File f;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                Random random = new Random();

                int randInt = random.nextInt();

                String fileName = "BTrans" + String.valueOf(randInt) + ".jpg";

                // download the file
                f = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/" + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            if(f != null){
                String imagePath = String.valueOf(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f));
                // setting downloaded into image view
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imagePath), "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        }
        private void showDialog(){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        private void dismissDialog(){
            progressDialog.dismiss();
        }
    }
}

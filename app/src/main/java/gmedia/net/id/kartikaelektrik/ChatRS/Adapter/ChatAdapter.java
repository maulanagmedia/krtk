package gmedia.net.id.kartikaelektrik.ChatRS.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.FormatItem;
import gmedia.net.id.kartikaelektrik.util.ImageUtils;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by indra on 29/12/2016.
 */

public class ChatAdapter extends ArrayAdapter {

    private Context context;
    private List<CustomListItem> items;
    private View viewInflater;
    private ItemValidation iv = new ItemValidation();

    public ChatAdapter(Context context, List<CustomListItem> items) {
        super(context, R.layout.adapter_chat_send, items);
        this.context = context;
        this.items = items;
    }

    public void addMoreData(List<CustomListItem> moreData){

        items.addAll(0,moreData);
        notifyDataSetChanged();
    }

    public void addMoreChat(CustomListItem moreData){

        items.add(moreData);
        notifyDataSetChanged();
    }

    public void removeChat(String id){

        int x = 0;
        boolean isFind = false;
        for(CustomListItem item: items){

            if(item.getListItem2().equals(id)){
                isFind = true;
                break;
            }
            x++;
        }

        if(isFind){

            items.remove(x);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private static class ViewHolder {
        private TextView tvNama, tvPesan, tvTime;
        private LinearLayout llAttach;
        private ImageView ivImage;
        private TextView tvFileName, tvFileSize;
    }

    @Override
    public int getItemViewType(int position) {

        int hasil = 0;
        final CustomListItem item = items.get(position);
        String title = item.getListItem1();
        if(title.equals("0")){
            hasil = 0;
        }else if(title.equals("1")){
            hasil = 1;
        }

        return hasil;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        final CustomListItem item = items.get(position);
        int tipeViewList = getItemViewType(position);

        if(convertView == null){

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            if(tipeViewList == 0){
                convertView = inflater.inflate(R.layout.adapter_chat_send, null);
            }else{
                convertView = inflater.inflate(R.layout.adapter_chat_receive, null);
            }

            holder.tvNama = (TextView) convertView.findViewById(R.id.tv_nama);
            holder.tvPesan = (TextView) convertView.findViewById(R.id.tv_pesan);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.llAttach = (LinearLayout) convertView.findViewById(R.id.ll_attach);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.tvFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            holder.tvFileSize = (TextView) convertView.findViewById(R.id.tv_file_size);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if (item.getListItem1().equals("0")){

            holder.tvNama.setText(item.getListItem7());
        }else{
            holder.tvNama.setText(item.getListItem7());
        }

        holder.tvPesan.setText(item.getListItem3());
        holder.tvTime.setText(iv.ChangeFormatDateString(item.getListItem5(), FormatItem.formatTimestamp, FormatItem.formatDateTime));

        /*listChat.add(new CustomItem(
                item.getString("is_balasan")    // 1
                ,item.getString("id")           // 2
                ,item.getString("message")      // 3
                ,item.getString("is_open")      // 4
                ,item.getString("timestamp")    // 5
                ,isFile                         // 6
                ,chatFrom.getString("name")     // 7
                ,chatTo.getString("name")       // 8
                ,isFile                         // 9
                ,fileName                       // 10
                ,fileAddress                    // 11
                ,fileSize                       // 12
                ,isImage                        // 13
                ,isDocument                     // 14
        ));*/

        String extension = "";

        int i = item.getListItem1().lastIndexOf('.');
        if (i > 0) {
            extension = item.getListItem1().substring(i+1);
        }

        if(extension.length() > 0 && iv.isImage(extension)){ // image

            holder.llAttach.setVisibility(View.VISIBLE);
            holder.tvPesan.setVisibility(View.GONE);

            ImageUtils iu = new ImageUtils();
            iu.LoadRealImage(context, item.getListItem1(), holder.ivImage);
            holder.tvFileName.setText(item.getListItem10());
            holder.tvFileSize.setText(item.getListItem12());

            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new DownloadFile().execute(item.getListItem11());
                }
            });
        }else if(extension.length() > 0){ //document

            holder.ivImage.setImageResource(R.mipmap.ic_attach);
            holder.llAttach.setVisibility(View.VISIBLE);
            holder.tvPesan.setVisibility(View.GONE);
            holder.tvFileName.setText(item.getListItem10());
            holder.tvFileSize.setText(item.getListItem12());

            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new DownloadFile().execute(item.getListItem11());
                }
            });
        }else{

            holder.ivImage.setImageResource(R.mipmap.ic_attach);
            holder.llAttach.setVisibility(View.GONE);
            holder.tvPesan.setVisibility(View.VISIBLE);
            holder.tvPesan.setText(item.getListItem3());
        }

        return convertView;
    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "PerkasaR/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    //Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(context,
                    message, Toast.LENGTH_LONG).show();

            if(message.toLowerCase().contains("downloaded at")){

                try {

                    String imagePath = message.replace("Downloaded at: ", "");
                    File file = new File(imagePath);
                    imagePath = String.valueOf(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file));

                    // setting downloaded into image view

                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension
                            (MimeTypeMap.getFileExtensionFromUrl(imagePath));

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(imagePath), mimeType);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}

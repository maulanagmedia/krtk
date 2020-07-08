package gmedia.net.id.kartikaelektrik.ChatRS;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ChatRS.Adapter.ChatAdapter;
import gmedia.net.id.kartikaelektrik.MainActivity;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ImageUtils;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.PermissionUtils;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;
import io.codetail.widget.RevealLinearLayout;

import static gmedia.net.id.kartikaelektrik.util.ImageUtils.getImageUri;

public class DetailChat extends AppCompatActivity {

    private ListView lvChat;
    private EditText edtChat;
    private ImageView ivAttach;
    private ImageView ivPhoto;
    private ImageView ivSend;
    private ItemValidation iv = new ItemValidation();
    private List<CustomListItem> listChat, moreList;
    public static boolean isChatActive = false;
    private Context context;
    private ChatAdapter adapter;
    private LinearLayout llPhoto;
    private Animator mAnimator;
    private RevealLinearLayout rllContainer;
    private Button btnBukaDokumen, btnBukaGallery, btnBukaKamera;
    private String TAG = "Chat";
    private View footerList;

    //Upload Handler
    private static int RESULT_OK = -1;
    private static int PICK_IMAGE_REQUEST = 1212;
    private ImageUtils iu = new ImageUtils();
    private Bitmap bitmap;
    private int PICKFILE_RESULT_CODE = 3;
    private String filePathURI;
    private ProgressBar pbLoading;
    private File sourceFile;
    private int totalSize;
    private int serverResponseCode = 0;
    private ProgressDialog dialog;
    private Uri photoURLCamera;
    private SessionManager session;
    private String token0 = "", token1 = "", token2 = "", token3 = "", token4 = "", token5;
    private File saveDirectory;
    private int statusUpload = 1;
    private int start = 0;
    private final int count = 10;

    private boolean isLoading = false;
    private boolean isFromNotif = false;
    public static String namaRS = "", kdcus = "";
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        if (shouldAskPermissions()) {
            askPermissions();
        }

        setTitle("Chat");
        isChatActive = true;
        context = this;
        activity = this;
        session = new SessionManager(this);

        initUI();
    }

    private void initUI() {

        lvChat = (ListView) findViewById(R.id.lv_chat);
        edtChat = (EditText) findViewById(R.id.edt_chat);
        ivAttach = (ImageView) findViewById(R.id.iv_attach);
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        ivSend = (ImageView) findViewById(R.id.iv_send);
        llPhoto = (LinearLayout) findViewById(R.id.ll_photo);
        rllContainer = (RevealLinearLayout) findViewById(R.id.rll_container);
        btnBukaDokumen = (Button) findViewById(R.id.btn_buka_dokument);
        btnBukaGallery = (Button) findViewById(R.id.btn_buka_gallery);
        btnBukaKamera = (Button) findViewById(R.id.btn_buka_kamera);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            isFromNotif = bundle.getString("notif", "").equals("1");
            namaRS = bundle.getString("nama", "");
            kdcus = bundle.getString("kdcus", "");

            setTitle(namaRS);
            getSupportActionBar().setSubtitle(namaRS);
        }

        initEvent();
    }

    private void initEvent() {

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validasi
                openAttachDialog(false);
                if(edtChat.getText().toString().length() == 0){

                    edtChat.setError("Pesan mohon diisi");
                    edtChat.requestFocus();
                    return;
                }else{

                    edtChat.setError(null);
                }

                if(isLoading){
                    return;
                }

                sendChat(edtChat.getText().toString());
            }
        });

        lvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                openAttachDialog(false);
                return false;
            }
        });

        edtChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                openAttachDialog(false);
                return false;
            }
        });

        ivAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(llPhoto.getVisibility() == View.INVISIBLE){
                    openAttachDialog(true);
                }else{
                    openAttachDialog(false);
                }
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAttachDialog(false);
                openCamera();
            }
        });

        btnBukaDokumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAttachDialog(false);
                showDocumentChooser();
            }
        });

        btnBukaGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAttachDialog(false);
                showFileChooser();
            }
        });

        btnBukaKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAttachDialog(false);
                openCamera();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(filePath, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);

            long sizeLong = returnCursor.getLong(sizeIndex);
            String sizeFile = "";

            double k = sizeLong/1024.0;
            double m = sizeLong/1048576.0;
            double g = sizeLong/1073741824.0;
            double t = sizeLong/1099511627776.0;

            if (t > 1) {
                sizeFile = iv.doubleToString(t,"1") + " TB";
            } else if (g > 1) {
                sizeFile = iv.doubleToString(g,"1") + " GB";
            } else if (m > 1) {
                sizeFile = iv.doubleToString(m,"1") + " MB";
            } else if (k > 1) {
                sizeFile = iv.doubleToString(k,"1") + " KB";
            }else{
                sizeFile = String.valueOf(sizeLong) + " B";
            }

            //Log.d(TAG, "namafile " + namaFile);
            //Log.d(TAG, "sizeFile " + sizeFile);

            //filePathURI = Environment.getExternalStorageDirectory() + File.separator + filePath.getPath().replace("/document/primary:","");
            //filePathURI = iv.getPathFromUri(context, filePath);
            copyFileFromUri(context, filePath, namaFile);

            InputStream imageStream = null, copyStream = null;
            try {
                imageStream = getContentResolver().openInputStream(
                        filePath);
                copyStream = getContentResolver().openInputStream(
                        filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            //options.inDither = true;

            // Get bitmap dimensions before reading...
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(copyStream, null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            int largerSide = Math.max(width, height);
            options.inJustDecodeBounds = false; // This time it's for real!
            int sampleSize = 1; // Calculate your sampleSize here
            if(largerSide <= 1000){
                sampleSize = 1;
            }else if(largerSide > 1000 && largerSide <= 2000){
                sampleSize = 2;
            }else if(largerSide > 2000 && largerSide <= 3000){
                sampleSize = 3;
            }else if(largerSide > 3000 && largerSide <= 4000){
                sampleSize = 4;
            }else{
                sampleSize = 6;
            }
            options.inSampleSize = sampleSize;
            //options.inDither = true;

            Bitmap bmp = BitmapFactory.decodeStream(imageStream, null, options);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            bitmap = scaleDown(bitmap, 380, true);

            try {
                stream.close();
                stream = null;
            } catch (IOException e) {

                e.printStackTrace();
            }

            if(bitmap != null){

                //image available
            }

        }else if(requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){

            Uri filePath = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(filePath, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);

            long sizeLong = returnCursor.getLong(sizeIndex);
            String sizeFile = "";

            double k = sizeLong/1024.0;
            double m = sizeLong/1048576.0;
            double g = sizeLong/1073741824.0;
            double t = sizeLong/1099511627776.0;

            if (t > 1) {
                sizeFile = iv.doubleToString(t,"1") + " TB";
            } else if (g > 1) {
                sizeFile = iv.doubleToString(g,"1") + " GB";
            } else if (m > 1) {
                sizeFile = iv.doubleToString(m,"1") + " MB";
            } else if (k > 1) {
                sizeFile = iv.doubleToString(k,"1") + " KB";
            }else{
                sizeFile = String.valueOf(sizeFile) + " B";
            }

            //Log.d(TAG, "namafile " + namaFile);
            //Log.d(TAG, "sizeFile " + sizeFile);

            /*filePathURI = iv.getPathFromUri(context, filePath);
            new UploadFileToServer().execute();*/
            copyFileFromUri(context, data.getData(), namaFile);

        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            /*try {

                Cursor returnCursor =
                        getContentResolver().query(photoURLCamera, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String namaFile = returnCursor.getString(nameIndex);

                long sizeLong = returnCursor.getLong(sizeIndex);
                String sizeFile = "";

                double k = sizeLong/1024.0;
                double m = sizeLong/1048576.0;
                double g = sizeLong/1073741824.0;
                double t = sizeLong/1099511627776.0;

                if (t > 1) {
                    sizeFile = iv.doubleToString(t,"1") + " TB";
                } else if (g > 1) {
                    sizeFile = iv.doubleToString(g,"1") + " GB";
                } else if (m > 1) {
                    sizeFile = iv.doubleToString(m,"1") + " MB";
                } else if (k > 1) {
                    sizeFile = iv.doubleToString(k,"1") + " KB";
                }else{
                    sizeFile = String.valueOf(sizeFile) + " B";
                }

                //Log.d(TAG, "namafile " + namaFile);
                //Log.d(TAG, "sizeFile " + sizeFile);

                *//*filePathURI = iv.getPathFromUri(context, photoURLCamera);
                new UploadFileToServer().execute();*//*
                copyFileFromUri(context, photoURLCamera, namaFile);

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoFromCameraURI));

                bitmap = scaleDown(bitmap, 380, true);

                if(bitmap != null){

                    //image available

                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            if(data != null){

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri filePath = getImageUri(getApplicationContext(), photo);
                Cursor returnCursor =
                        getContentResolver().query(filePath, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String namaFile = returnCursor.getString(nameIndex);

                long sizeLong = returnCursor.getLong(sizeIndex);
                String sizeFile = "";

                double k = sizeLong/1024.0;
                double m = sizeLong/1048576.0;
                double g = sizeLong/1073741824.0;
                double t = sizeLong/1099511627776.0;

                if (t > 1) {
                    sizeFile = iv.doubleToString(t,"1") + " TB";
                } else if (g > 1) {
                    sizeFile = iv.doubleToString(g,"1") + " GB";
                } else if (m > 1) {
                    sizeFile = iv.doubleToString(m,"1") + " MB";
                } else if (k > 1) {
                    sizeFile = iv.doubleToString(k,"1") + " KB";
                }else{
                    sizeFile = String.valueOf(sizeFile) + " B";
                }

                //Log.d(TAG, "namafile " + namaFile);
                //Log.d(TAG, "sizeFile " + sizeFile);

                /*filePathURI = iv.getPathFromUri(context, photoURLCamera);
                new UploadFileToServer().execute();*/
                copyFileFromUri(context, filePath, namaFile);
                bitmap = (Bitmap) data.getExtras().get("data");
                bitmap = scaleDown(bitmap, 380, true);

            }else{
                Toast.makeText(context, "Gambar tidak termuat, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean copyFileFromUri(Context context, Uri fileUri, String namaFile)
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String extension = namaFile.substring(namaFile.lastIndexOf("."));
        FileOutputStream out = null;

        try
        {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(fileUri);

            File root = Environment.getExternalStorageDirectory();
            if(root == null){
                //Log.d(TAG, "Failed to get root");
            }

            // create a directory
            saveDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "PSP Reseller"  +File.separator);
            // create direcotory if it doesn't exists
            saveDirectory.mkdirs();

            final int time = (int) (new Date().getTime()/1000);

            extension = extension.toLowerCase();
            if(extension.equals(".jpeg") || extension.equals(".jpg") || extension.equals(".png") || extension.equals(".bmp")){

                outputStream = new FileOutputStream( saveDirectory.getAbsoluteFile() + File.separator + time + namaFile); // filename.png, .mp3, .mp4 ...
                Bitmap bm2 = BitmapFactory.decodeStream(inputStream);
                int maxWidth = bm2.getWidth() > bm2.getHeight() ? bm2.getWidth() : bm2.getHeight();
                int scale = 100;
                if(maxWidth > 720){
                    maxWidth = 720;
                    scale = 80;
                }
                bm2.compress(Bitmap.CompressFormat.JPEG, scale, outputStream);
                bm2 = scaleDown(bm2, maxWidth, true);

                File file = new File(saveDirectory, time + namaFile);
                Log.i(TAG, "" + file);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream outstreamBitmap = new FileOutputStream(file);
                    bm2.compress(Bitmap.CompressFormat.JPEG, scale, outstreamBitmap);
                    bm2 = scaleDown(bm2, maxWidth, true);
                    outstreamBitmap.flush();
                    outstreamBitmap.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else{

                outputStream = new FileOutputStream( saveDirectory.getAbsoluteFile() + File.separator + time + namaFile); // filename.png, .mp3, .mp4 ...
                if(outputStream != null){
                    Log.e( TAG, "Output Stream Opened successfully");
                }

                byte[] buffer = new byte[1000];
                int bytesRead = 0;
                while ( ( bytesRead = inputStream.read( buffer, 0, buffer.length ) ) >= 0 )
                {
                    outputStream.write( buffer, 0, buffer.length );
                }
            }

            filePathURI = Environment.getExternalStorageDirectory() + File.separator + "PSP Reseller"  +File.separator + time + namaFile;

            new UploadFileToServer().execute();
        } catch ( Exception e ){
            Log.e( TAG, "Exception occurred " + e.getMessage());
        } finally{

        }
        return true;
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    private class UploadFileToServer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //pbLoading.setVisibility(View.VISIBLE);
            //pbLoading.setProgress(0);
            statusUpload = 1;
            dialog = new ProgressDialog(activity);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final AlertDialog dialogConf = new AlertDialog.Builder(context)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("Konfirmasi")
                            .setMessage("Anda yakin ingin membatalkan proses")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    statusUpload = 0;
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.show();
                                }
                            })
                            .show();
                }
            });
            dialog.show();

            //uploader_area.setVisibility(View.GONE); // Making the uploader area screen invisible
            //progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            sourceFile = new File(filePathURI);
            totalSize = (int) sourceFile.length();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //Log.d("PROG", progress[0]);
            //pbLoading.setProgress(Integer.parseInt(progress[0])); //Updating progress
            dialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected String doInBackground(String... args) {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = sourceFile.getName();
            //Log.d(TAG, "filename upload: " +fileName);
            String result = "";

            session = new SessionManager(context);
            token0  = iv.encodeMD5(iv.encodeBase64(iv.getCurrentDate("SSSHHMMddmmyyyyss")));

            try {

                connection = (HttpURLConnection) new URL(ServerURL.sendFile + kdcus).openConnection();
                connection.setRequestMethod("POST");
                String boundary = "---------------------------boundary";
                String tail = "\r\n--" + boundary + "--\r\n";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("Client-Service", "frontend-client");
                connection.setRequestProperty("Auth-Key", "gmedia_kartika");
                connection.setRequestProperty("token", session.getToken());
                connection.setRequestProperty("id", session.getUid());
                connection.setRequestProperty("level", session.getLevel());
                connection.setRequestProperty("laba", session.getLaba());
                connection.setRequestProperty("Nik-Asli", session.getNikAsli());
                connection.setRequestProperty("username", session.getUsername());
                connection.setDoOutput(true);

                String metadataPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                        + "" + "\r\n";

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"files\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";

                long fileLength = sourceFile.length() + tail.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                String stringData = metadataPart + fileHeader;

                long requestLength = stringData.length() + fileLength;
                connection.setRequestProperty("Content-length", "" + requestLength);
                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(stringData);
                out.flush();

                int progress = 0;
                int bytesRead = 0;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead; // Here progress is total uploaded bytes

                    publishProgress(""+(int)((progress*100)/totalSize)); // sending progress percent to publishProgress
                }

                // Write closing boundary and close stream
                out.writeBytes(tail);
                out.flush();
                out.close();

                // Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                    result = line;
                }

            } catch (Exception e) {
                // Exception
            } finally {
                if (connection != null) connection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                dialog.dismiss();
            } catch(Exception e) {
            }

            try {
                result = result.replace("</div>", "");
                JSONObject response = new JSONObject(result);
                String status = response.getJSONObject("metadata").getString("status");
                String message = response.getJSONObject("metadata").getString("message");
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(status.equals("200")){

                    if(statusUpload == 1){

                        getChatData();
                    }
                    //berhasil
                }else{
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }

    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    private void showDocumentChooser(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("application/pdf,text/plain,application/x-excel");
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Pilih File"), PICKFILE_RESULT_CODE);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);

    }

    private final int REQUEST_IMAGE_CAPTURE = 2;
    private String photoFromCameraURI;

    private void openCamera(){

        if(PermissionUtils.hasPermissions(context, Manifest.permission.CAMERA)){

            /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                photoURLCamera = null;
                try {
                    photoURLCamera = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            createImageFile());
                    photoFromCameraURI = photoURLCamera.toString();
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.i(TAG, "IOException");
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURLCamera);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }*/

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }else{

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                    .setTitle("Ijin dibutuhkan")
                    .setMessage("Ijin dibutuhkan untuk mengakses kamera, harap ubah ijin kamera ke \"diperbolehkan\"")
                    .setPositiveButton("Buka Ijin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        //photoFromCameraURI = "file:" + image.getAbsolutePath();
        return image;
    }

    private void openAttachDialog(boolean isOpen){

        int cx = ((llPhoto.getLeft() + llPhoto.getRight()) * 2) / 3 ;
        int cy = ((llPhoto.getTop() + llPhoto.getBottom()) / 2) + ((llPhoto.getTop() + llPhoto.getBottom()) / 4);
        // get the final radius for the clipping circle
        int dx = Math.max(cx, llPhoto.getWidth() - cx);
        int dy = Math.max(cy, llPhoto.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);


        if(isOpen){

            if (llPhoto.getVisibility() == View.INVISIBLE) {

                mAnimator =
                        ViewAnimationUtils.createCircularReveal(llPhoto, cx, cy, 0, finalRadius);
                mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimator.setDuration(500);

                llPhoto.setVisibility(View.VISIBLE);
                mAnimator.start();

            }
        }else{

            if(llPhoto.getVisibility() == View.VISIBLE){

                Animator mAnimator2 =
                        ViewAnimationUtils.createCircularReveal(llPhoto, cx, cy, finalRadius, 0);
                mAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimator2.setDuration(500);

                mAnimator2.start();

                mAnimator2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        llPhoto.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        }
    }

    private void sendChat(String s) {

        isLoading = true;
        pbLoading.setVisibility(View.VISIBLE);

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("pesan", s);
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody , "POST", ServerURL.sendChat, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                isLoading = false;
                JSONObject responseAPI;
                start = 0;

                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        getChatData();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                isLoading = false;
            }
        });

        /*CustomItem item = new CustomItem("0", "4", s);
        adapter.addMoreChat(item);*/

    }

    private void getChatData() {

        isLoading = true;
        pbLoading.setVisibility(View.VISIBLE);
        listChat = new ArrayList<>();

        JSONObject jBody = new JSONObject();
        try {
            //jBody.put("kdcus", session.getKdcus());
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDetailChat, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                pbLoading.setVisibility(View.GONE);
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    listChat = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            JSONObject chatFrom = item.getJSONObject("from");
                            JSONObject chatTo = item.getJSONObject("to");
                            String isFile = "0";
                            String fileName = "";
                            String fileAddress = "";
                            String fileSize = "";
                            String isImage = "0";
                            String isDocument = "0";
                            if(item.getString("file").equals("[]")){
                                isFile = "0";
                            }else{

                                isFile = "1";
                                JSONObject fileData = item.getJSONObject("file");

                                fileName = fileData.getString("file_name");
                                fileAddress = fileData.getString("file_address");
                                fileSize = fileData.getString("file_size");
                                isImage = fileData.getString("is_image");
                                isDocument = fileData.getString("is_document");
                            }

                            Log.d(TAG, "onSuccess: ");

                            listChat.add(new CustomListItem(
                                    item.getString("is_balasan")    // 1
                                    ,item.getString("id")           // 2
                                    ,item.getString("message")      // 3
                                    ,item.getString("is_open")      // 4
                                    ,item.getString("timestamp")    // 5
                                    ,isFile                               // 6
                                    ,chatFrom.getString("name")     // 7
                                    ,chatTo.getString("name")       // 8
                                    ,isFile                               // 9
                                    ,fileName                             // 10
                                    ,fileAddress                          // 11
                                    ,fileSize                             // 12
                                    ,isImage                              // 13
                                    ,isDocument                           // 14
                            ));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setChatRoom(listChat);
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                pbLoading.setVisibility(View.GONE);
                setChatRoom(listChat);
            }
        });
    }

    private void setChatRoom(final List<CustomListItem> listItem) {

        lvChat.setAdapter(null);

        if(listItem != null){

            adapter = new ChatAdapter(context, listItem);
            lvChat.setAdapter(adapter);
            lvChat.setSelection(listItem.size() - 1);

            /*lvChat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                    if(item.getListItem1().equals("0")){

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Hapus Pesan")
                                .setMessage("Anda ingin menghapus pesan ini?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //deleteMessage(item.getItem2());
                                    }
                                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();

                        return true;
                    }

                    return false;
                }
            });*/

            if(adapter.getCount() > 0) lvChat.setSelection(adapter.getCount() - 1);
            edtChat.setText("");

            lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                    if (i == SCROLL_STATE_IDLE) {
                        if (lvChat.getFirstVisiblePosition() == 0 && !isLoading) {

                            isLoading = true;
                            lvChat.addHeaderView(footerList);
                            start += count;
                            getMoreData();
                            Log.i(TAG, "onScroll: first ");
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });
        }
    }

    private void getMoreData() {

        isLoading = true;
        moreList = new ArrayList<>();

        JSONObject jBody = new JSONObject();
        try {
            //jBody.put("kdcus", session.getKdcus());
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDetailChat, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                JSONObject responseAPI;
                lvChat.removeHeaderView(footerList);

                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    listChat = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            JSONObject chatFrom = item.getJSONObject("from");
                            JSONObject chatTo = item.getJSONObject("to");
                            String isFile = "0";
                            String fileName = "";
                            String fileAddress = "";
                            String fileSize = "";
                            String isImage = "0";
                            String isDocument = "0";
                            if(item.getString("file").equals("[]")){
                                isFile = "0";
                            }else{

                                isFile = "1";
                                JSONObject fileData = item.getJSONObject("file");

                                fileName = fileData.getString("file_name");
                                fileAddress = fileData.getString("file_address");
                                fileSize = fileData.getString("file_size");
                                isImage = fileData.getString("is_image");
                                isDocument = fileData.getString("is_document");
                            }

                            moreList.add(new CustomListItem(
                                    item.getString("is_balasan")    // 1
                                    ,item.getString("id")           // 2
                                    ,item.getString("message")      // 3
                                    ,item.getString("is_open")      // 4
                                    ,item.getString("timestamp")    // 5
                                    ,isFile                               // 6
                                    ,chatFrom.getString("name")     // 7
                                    ,chatTo.getString("name")       // 8
                                    ,isFile                               // 9
                                    ,fileName                             // 10
                                    ,fileAddress                          // 11
                                    ,fileSize                             // 12
                                    ,isImage                              // 13
                                    ,isDocument                           // 14
                            ));
                        }
                    }

                    adapter.addMoreData(moreList);
                    if(adapter.getCount() > 0) lvChat.setSelection(moreList.size() - 1 > 0 ? moreList.size() - 1 : moreList.size());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    lvChat.removeHeaderView(footerList);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String result) {

                isLoading = false;
                pbLoading.setVisibility(View.GONE);
                setChatRoom(listChat);

                try {
                    lvChat.removeHeaderView(footerList);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void deleteMessage(final String idMessage) {

        /*isLoading = true;
        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", idMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.deleteChat, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                pbLoading.setVisibility(View.GONE);
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");
                    Toast.makeText(context, message,Toast.LENGTH_LONG).show();

                    if(status.equals("200")) adapter.removeChat(idMessage);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //getChatData();
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                pbLoading.setVisibility(View.GONE);
                //getChatData();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        isChatActive = true;

        start = 0;
        isLoading = false;
        getChatData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        isChatActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isChatActive = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        isChatActive = false;
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

        if(isFromNotif){

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
        }else{

            super.onBackPressed();
            //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
        }
    }
}
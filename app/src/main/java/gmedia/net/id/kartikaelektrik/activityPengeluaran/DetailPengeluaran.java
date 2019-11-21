package gmedia.net.id.kartikaelektrik.activityPengeluaran;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.intik.overflowindicator.OverflowPagerIndicator;
import cz.intik.overflowindicator.SimpleSnapHelper;
import gmedia.net.id.kartikaelektrik.DashboardContainer;
import gmedia.net.id.kartikaelektrik.MenuAdminPengaturanHeader.Adapter.PhotosKeteranganAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.PhotoModel;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ImageUtils;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.PermissionUtils;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class DetailPengeluaran extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private String formatDate, formatDateDisplay;
    private TextInputLayout tilTanggal;
    private EditText edtTanggal;

    private RecyclerView rvPhoto;
    private ImageButton ibPhoto;
    private List<PhotoModel> listPhoto = new ArrayList<>();
    private PhotosKeteranganAdapter adapterPhoto;

    private String imageFilePath = "";
    private File photoFile;
    private int RESULT_OK = -1;
    private int PICK_IMAGE_REQUEST = 1212;
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private Uri photoURI;
    private File saveDirectory;
    private String filePathURI = "";
    private String TAG = "DETAIL";
    private String folderName = "KartikaRes";
    private OverflowPagerIndicator opiPhoto;
    private ProgressBar pbLoading;
    private Button btnSave;
    private EditText edtKeterangan;
    private EditText edtNominal;
    private String currentString = "";
    private String idPengeluaran = "";
    private Spinner spJenis;
    private List<OptionItem> listJenis = new ArrayList<>();
    private ArrayAdapter adapterJenis;
    private String selectedJenis = "";
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengeluaran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;

        setTitle("Detail Pengeluaran");
        initUI();
        initEvent();
    }

    private void initUI() {

        formatDate = context.getResources().getString(R.string.format_date);
        formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        tilTanggal = (TextInputLayout) findViewById(R.id.til_tanggal);
        edtTanggal = (EditText) findViewById(R.id.edt_tanggal);
        edtTanggal.setText(iv.getToday(formatDateDisplay));
        spJenis = (Spinner) findViewById(R.id.sp_jenis);

        iv.datePickerEvent(context, edtTanggal, "RIGHT", formatDateDisplay);

        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        ibPhoto = (ImageButton) findViewById(R.id.ib_photo);
        opiPhoto = (OverflowPagerIndicator) findViewById(R.id.opi_photo);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);
        edtNominal = (EditText) findViewById(R.id.edt_nominal);
        btnSave = (Button) findViewById(R.id.btn_save);

        listPhoto = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapterPhoto = new PhotosKeteranganAdapter(context, listPhoto);
        rvPhoto.setLayoutManager(layoutManager);
        rvPhoto.setAdapter(adapterPhoto);

        listJenis = new ArrayList<>();
        adapterJenis = new ArrayAdapter(context, android.R.layout.simple_list_item_1, listJenis);
        spJenis.setAdapter(adapterJenis);

        spJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                OptionItem item = listJenis.get(position);
                selectedJenis = item.getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getDataJenis();

        opiPhoto.attachToRecyclerView(rvPhoto);
        new SimpleSnapHelper(opiPhoto).attachToRecyclerView(rvPhoto);
    }

    private void loadChooserDialog() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_chooser, null);
        builder.setView(view);

        final LinearLayout llBrowse = (LinearLayout) view.findViewById(R.id.ll_browse);
        final LinearLayout llCamera = (LinearLayout) view.findViewById(R.id.ll_camera);

        final android.app.AlertDialog alert = builder.create();

        llBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFileChooser();
                alert.dismiss();
            }
        });

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCamera();
                alert.dismiss();
            }
        });

        alert.show();
    }

    private void initEvent() {

        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadChooserDialog();
            }
        });

        edtNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtNominal.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtNominal.setText(formatted);
                    edtNominal.setSelection(formatted.length());
                    edtNominal.addTextChangedListener(this);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nominal = edtNominal.getText().toString().replaceAll("[,.]", "");

                if(iv.parseNullDouble(nominal) <= 0){

                    edtNominal.setError("Nominal harap diisi");
                    edtNominal.requestFocus();
                    return;
                }else{

                    edtNominal.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin menambahkan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                saveData();
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
    }

    private void initData() {

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", idPengeluaran);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPengeluaran,"", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        String nama = response.getJSONObject("response").getJSONObject("header").getString("nama");
                        String tgl = response.getJSONObject("response").getJSONObject("header").getString("tgl");
                        String keterangan = response.getJSONObject("response").getJSONObject("header").getString("keterangan");
                        String nominal = response.getJSONObject("response").getJSONObject("header").getString("nominal");
                        selectedJenis = response.getJSONObject("response").getJSONObject("header").getString("id_jenis");

                        edtTanggal.setText(iv.ChangeFormatDateString(tgl, formatDate, formatDateDisplay));
                        edtKeterangan.setText(keterangan);
                        edtNominal.setText(iv.ChangeToCurrencyFormat(nominal));

                        JSONArray jFoto = response.getJSONObject("response").getJSONArray("detail");

                        listPhoto.clear();
                        for (int i = 0; i < jFoto.length(); i ++){

                            String foto = jFoto.getJSONObject(i).getString("foto");
                            listPhoto.add(new PhotoModel(foto, foto, ""));

                        }

                        adapterPhoto.notifyDataSetChanged();

                        int jenisPosition = 0;
                        for (int j = 0; j < listJenis.size(); j++){

                            if(selectedJenis.equals(listJenis.get(j).getValue())){

                                jenisPosition = j;
                                break;
                            }
                        }

                        spJenis.setSelection(jenisPosition);

                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                    Toast.makeText(context, "Terjadi kesalahan dalam parsing data", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {

                Toast.makeText(context, "Terjadi kesalahan, harap ulangi proses", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getDataJenis() {

        final JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(context, jBody, "GET", ServerURL.getJenisPengeluaran,"", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "";
                listJenis.clear();

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jArray = response.getJSONArray("response");

                        for (int i = 0; i < jArray.length(); i ++){

                            JSONObject jo = jArray.getJSONObject(i);
                            listJenis.add(new OptionItem(jo.getString("id"), jo.getString("biaya")));

                        }

                        adapterJenis.notifyDataSetChanged();

                        Bundle bundle = getIntent().getExtras();
                        if(bundle != null) {

                            idPengeluaran = bundle.getString("id", "");

                            if(!idPengeluaran.isEmpty()){

                                btnSave.setVisibility(View.GONE);

                                initData();
                            }
                        }

                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                    Toast.makeText(context, "Terjadi kesalahan dalam parsing data", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {

                Toast.makeText(context, "Terjadi kesalahan, harap ulangi proses", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void saveData() {

        dialogBox = new DialogBox(DetailPengeluaran.this);
        dialogBox.showDialog(false);

        JSONArray jImage = new JSONArray();

        for(PhotoModel photo: listPhoto){

            jImage.put(photo.getKeterangan());
        }

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("foto", jImage);
            jsonBody.put("id_jenis", selectedJenis);
            jsonBody.put("tgl", iv.ChangeFormatDateString(edtTanggal.getText().toString(), formatDateDisplay, formatDate));
            jsonBody.put("keterangan", edtKeterangan.getText().toString());
            jsonBody.put("nominal", edtNominal.getText().toString().replaceAll("[,.]", ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(this, jsonBody, "POST", ServerURL.savePengeluaran, "", "", 0,
            new ApiVolley.VolleyCallback(){
                @Override
                public void onSuccess(String result){

                    dialogBox.dismissDialog();
                    JSONObject responseAPI = new JSONObject();
                    try {
                        responseAPI = new JSONObject(result);
                        String statusString = responseAPI.getJSONObject("metadata").getString("status");
                        String message = responseAPI.getJSONObject("metadata").getString("message");
                        Toast.makeText(DetailPengeluaran.this,message, Toast.LENGTH_LONG).show();

                        if(iv.parseNullInteger(statusString) == 200){

                            Intent intent = new Intent(DetailPengeluaran.this, DashboardContainer.class);
                            intent.putExtra("kodemenu", "menupengeluaran");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                    }catch (Exception e){
                        Toast.makeText(DetailPengeluaran.this,"Terjadi kesalahan", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(String result) {

                    dialogBox.dismissDialog();
                    Toast.makeText(DetailPengeluaran.this, result,Toast.LENGTH_LONG).show();
                }
            });

    }

    //region File Chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    private void openCamera(){

        if(PermissionUtils.hasPermissions(context, Manifest.permission.CAMERA)){

            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);*/

            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if(pictureIntent.resolveActivity(getPackageManager()) != null){
                //Create a file to store the image
                photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                }
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(context,
                            "gmedia.net.id.kartikaelektrik.provider", photoFile);

                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(pictureIntent,
                            REQUEST_IMAGE_CAPTURE);
                }
            }
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
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Hasil dari QR Code Scanner

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri imgUri = data.getData();

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(
                        imgUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);

            Uri filePath = ImageUtils.getImageUri(getApplicationContext(), imageBitmap);

            Cursor returnCursor =
                    getContentResolver().query(filePath, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);

            /*Matrix matrix = new Matrix();
            try {

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                int columnIndex = returnCursor.getColumnIndex(filePathColumn[0]);
                String picturePath = returnCursor.getString(columnIndex);
                ExifInterface exif = new ExifInterface(picturePath);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = iv.exifToDegrees(rotation);
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

            } catch (Exception e) {
                e.printStackTrace();
            }*/

            copyFileFromUri(context, filePath, namaFile, null);

        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getExtras().get("data") != null) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri filePath = ImageUtils.getImageUri(getApplicationContext(), photo);
            Cursor returnCursor =
                    getContentResolver().query(filePath, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);
            copyFileFromUri(context, filePath, namaFile, null);

        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            Cursor returnCursor =
                    getApplication().getContentResolver().query(photoURI,
                            null, null, null, null);

            Matrix matrix = new Matrix();
            try {

                ExifInterface exif = new ExifInterface(photoFile.toString());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = iv.exifToDegrees(rotation);
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

            } catch (Exception e) {
                e.printStackTrace();
            }

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String namaFile = returnCursor.getString(nameIndex);
            copyFileFromUri(context, photoURI, namaFile, matrix);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean copyFileFromUri(Context context, Uri fileUri, String namaFile, Matrix matrix)
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
            saveDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + folderName  +File.separator);
            // create direcotory if it doesn't exists
            saveDirectory.mkdirs();

            final int time = (int) (new Date().getTime()/1000);

            extension = extension.toLowerCase();
            Bitmap bm2 = null;
            if(extension.equals(".jpeg")
                    || extension.equals(".jpg")
                    || extension.equals(".png")
                    || extension.equals(".bmp")){

                outputStream = new FileOutputStream( saveDirectory.getAbsoluteFile() + File.separator + time + namaFile); // filename.png, .mp3, .mp4 ...
                bm2 = BitmapFactory.decodeStream(inputStream);
                int scale = 80;

                int imageHeight = bm2.getHeight();
                int imageWidth = bm2.getWidth();

                int newWidth = 0;
                int newHeight = 0;

                if(imageHeight > imageWidth){

                    newWidth = 640;
                    newHeight = newWidth * imageHeight / imageWidth;
                }else{

                    newHeight = 640;
                    newWidth = newHeight * imageWidth / imageHeight;
                }

                bm2 = Bitmap.createScaledBitmap(bm2, newWidth, newHeight, false);

                if(matrix != null){

                    bm2 = Bitmap.createBitmap(bm2, 0, 0, bm2.getWidth(), bm2.getHeight(), matrix, true);
                }

                bm2.compress(Bitmap.CompressFormat.JPEG, scale, outputStream);

                File file = new File(saveDirectory, time + namaFile);
                //Log.i(TAG, "" + file);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream outstreamBitmap = new FileOutputStream(file);
                    bm2.compress(Bitmap.CompressFormat.JPEG, scale, outstreamBitmap);
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

            filePathURI = Environment.getExternalStorageDirectory() + File.separator + folderName  +File.separator + time + namaFile;

            if(bm2 != null){

                String baset64Image = ImageUtils.convert(bm2);
                //Log.d(TAG, "copyFileFromUri: "+ baset64Image);
                listPhoto.add(new PhotoModel(filePathURI, "", baset64Image));
                adapterPhoto.notifyDataSetChanged();
            }

            //new UploadFileToServer().execute();
        } catch ( Exception e ){
            Log.e( TAG, "Exception occurred " + e.getMessage());
        } finally{

        }
        return true;
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

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}

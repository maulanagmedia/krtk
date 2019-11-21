package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.Manifest;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.intik.overflowindicator.OverflowPagerIndicator;
import cz.intik.overflowindicator.SimpleSnapHelper;
import gmedia.net.id.kartikaelektrik.MenuAdminPengaturanHeader.Adapter.PhotosKeteranganAdapter;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.Adapter.ListDetailNotaAdapter;
import gmedia.net.id.kartikaelektrik.model.PhotoModel;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ImageUtils;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.PermissionUtils;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailFormSetoran extends AppCompatActivity {

    private SessionManager session;
    private Context context;
    private static ItemValidation iv = new ItemValidation();
    private String formatDate = "", formatDateDisplay = "";
    private EditText edtSales;
    private EditText edtCustomer;
    private EditText edtTanggal, edtTanggalTransfer;
    private static EditText edtTotal;
    private RadioGroup rgCaraBayar;
    private RadioButton rbTunai, rbBank, rbGiro;
    private Spinner spBank;
    private LinearLayout llSaveContainer, llSisaPembayaran;
    private TextView tvSave;
    private String kdcus = "", namaCus = "";
    private String crBayar = "";
    private ProgressBar pbLoading;
    private List<OptionItem> listBank;
    private String currentString = "", currentStringDiskon = "";
    private String idSetoran = "";
    private boolean isEdit = false;
    private EditText edtDariBank, edtDariNorek, edtKeBank, edtKeNorek, edtNamaPemilik;
    private ListView lvNota;
    private List<OptionItem> listNota;
    private static EditText edtSisa, edtSisaTotal;
    public static double sisaPiutang = 0;
    private static ListDetailNotaAdapter adapterPiutangSales;
    private TextInputLayout tilNamaPemilik, tilTanggalTransfer;
    private boolean isConfirm = true;
    private TextView tvTotalPiutang;
    private double totalPiutangCurrent = 0;
    private LinearLayout llDiskon;
    private EditText edtDiskon;
    private EditText edtHargaDiskon;
    private EditText edtTotalDibayar;
    public static boolean isSinggleData = false;

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
    private boolean isKhusus = false;
    private LinearLayout llUpload;
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Setoran");
        context = this;
        session = new SessionManager(context);
        isSinggleData = false;

        intiUI();
    }

    private void intiUI() {

        llSisaPembayaran = (LinearLayout) findViewById(R.id.ll_sisa_pebayaran);
        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);

        llUpload = (LinearLayout) findViewById(R.id.ll_upload);
        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        ibPhoto = (ImageButton) findViewById(R.id.ib_photo);
        opiPhoto = (OverflowPagerIndicator) findViewById(R.id.opi_photo);

        tilNamaPemilik = (TextInputLayout) findViewById(R.id.til_nama_pemilik);
        tilTanggalTransfer = (TextInputLayout) findViewById(R.id.til_tanggal_transfer);
        edtSales = (EditText) findViewById(R.id.edt_sales);
        edtCustomer = (EditText) findViewById(R.id.edt_customer);
        edtTanggal = (EditText) findViewById(R.id.edt_tanggal);
        edtTanggalTransfer = (EditText) findViewById(R.id.edt_tanggal_transfer);
        rgCaraBayar = (RadioGroup) findViewById(R.id.rg_crbayar);
        rbTunai = (RadioButton) findViewById(R.id.rb_tunai);
        rbBank = (RadioButton) findViewById(R.id.rb_bank);
        rbGiro = (RadioButton) findViewById(R.id.rb_giro);
        spBank = (Spinner) findViewById(R.id.sp_bank);
        edtDariBank = (EditText) findViewById(R.id.edt_dari_bank);
        edtNamaPemilik = (EditText) findViewById(R.id.edt_nama_pemilik);
        edtDariNorek = (EditText) findViewById(R.id.edt_dari_norek);
        edtKeBank = (EditText) findViewById(R.id.edt_ke_bank);
        edtKeNorek = (EditText) findViewById(R.id.edt_ke_norek);
        edtTotal = (EditText) findViewById(R.id.edt_total);
        edtSisa = (EditText) findViewById(R.id.edt_sisa);
        edtSisaTotal = (EditText) findViewById(R.id.edt_sisa_total);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        lvNota = (ListView) findViewById(R.id.lv_nota);
        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tvSave.setText("Simpan Setoran");
        tvTotalPiutang = (TextView) findViewById(R.id.tv_total_piutang);

        llDiskon = (LinearLayout) findViewById(R.id.ll_diskon);
        edtDiskon = (EditText) findViewById(R.id.edt_diskon);
        edtHargaDiskon = (EditText) findViewById(R.id.edt_harga_diskon);
        edtTotalDibayar = (EditText) findViewById(R.id.edt_total_dibayar);

        isConfirm = true;
        sisaPiutang = 0;
        isEdit = false;

        listPhoto = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapterPhoto = new PhotosKeteranganAdapter(context, listPhoto);
        rvPhoto.setLayoutManager(layoutManager);
        rvPhoto.setAdapter(adapterPhoto);

        opiPhoto.attachToRecyclerView(rvPhoto);
        new SimpleSnapHelper(opiPhoto).attachToRecyclerView(rvPhoto);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idSetoran = bundle.getString("id", "");
            if(!idSetoran.equals("")){

                //llSaveContainer.setEnabled(false);
                isEdit = true;
                tvSave.setText("Hapus Setoran");
            }else{
                kdcus = bundle.getString("kdcus", "");
                namaCus = bundle.getString("namacus", "");
                isKhusus = bundle.getBoolean("khusus", false);
                if(isKhusus){

                    llUpload.setVisibility(View.VISIBLE);
                }else{
                    llUpload.setVisibility(View.GONE);
                }
                edtCustomer.setText(namaCus);

                getPiutangSales();
            }

            edtSales.setText(session.getFullName());
            initEvent();
        }
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
                if(listPhoto.size() > 0){

                    listPhoto.add(0,new PhotoModel(filePathURI, "", baset64Image));
                }else{

                    listPhoto.add(new PhotoModel(filePathURI, "", baset64Image));
                }

                adapterPhoto.notifyDataSetChanged();
            }

            //new UploadFileToServer().execute();
        } catch ( Exception e ){
            Log.e( TAG, "Exception occurred " + e.getMessage());
        } finally{

        }
        return true;
    }

    private void getPiutangSales() {

        /*pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("kdcus", kdcus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPiutangSales, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listNota = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jArray = response.getJSONArray("response");

                        for(int i = 0; i < jArray.length(); i++){

                            JSONObject jo = jArray.getJSONObject(i);
                            listNota.add(new OptionItem(
                                    jo.getString("nonota"),
                                    jo.getString("tgl"),
                                    jo.getString("sisa"),
                                    "0", // terbayar
                                    jo.getString("tanda"),
                                    false // checked
                            ));
                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    List<OptionItem> listItem = new ArrayList<>(listNota);
                    setSalesPiutang(listItem);

                } catch (JSONException e) {

                    setSalesPiutang(null);
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                setSalesPiutang(null);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
            }
        });*/

        totalPiutangCurrent = 0;
        for(OptionItem item : ListNotaPiutang.listNota){

            totalPiutangCurrent += iv.parseNullDouble(item.getAtt1());
        }

        tvTotalPiutang.setText("Total Piutang : "+ iv.ChangeToCurrencyFormat(iv.doubleToStringFull(totalPiutangCurrent)));
        setSalesPiutang(ListNotaPiutang.listNota);
    }

    private void setSalesPiutang(List<OptionItem> listItems){

        lvNota.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            if(listItems.size() == 1){
                llDiskon.setVisibility(View.VISIBLE);
                llSisaPembayaran.setVisibility(View.VISIBLE);
                isSinggleData = true;
            }else{
                llDiskon.setVisibility(View.GONE);
                llSisaPembayaran.setVisibility(View.GONE);
                isSinggleData = false;
            }

            adapterPiutangSales = new ListDetailNotaAdapter((Activity) context, listItems);
            lvNota.setAdapter(adapterPiutangSales);
        }
    }

    private void getDetailSetoran() {

        pbLoading.setVisibility(View.VISIBLE);

        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getDetailSetoran+idSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        JSONObject jo = response.getJSONObject("response");
                        edtCustomer.setText(jo.getString("nama_customer"));
                        crBayar = jo.getString("crbayar");

                        if(crBayar.equals("T")){
                            rbTunai.setChecked(true);
                        }else if(crBayar.equals("B")){
                            rbBank.setChecked(true);
                        }else{
                            rbGiro.setChecked(true);
                        }

                        int position = 0;
                        for(int i = 0; i < listBank.size();i++){
                            if(listBank.get(i).getValue().equals(jo.getString("kode_bank"))){
                                position = i;
                                break;
                            }
                        }

                        spBank.setSelection(position);
                        edtTanggal.setText(iv.ChangeFormatDateString(jo.getString("tanggal"), formatDate, formatDateDisplay));
                        edtTotal.setText(jo.getString("total"));

                        edtDiskon.setText(jo.getString("diskon"));
                        edtHargaDiskon.setText(jo.getString("totaldiskon"));
                        edtDariBank.setText(jo.getString("dari_bank"));
                        edtDariNorek.setText(jo.getString("dari_rekening"));
                        edtNamaPemilik.setText(jo.getString("daripemilik"));

                        edtKeBank.setText(jo.getString("bank"));
                        edtKeNorek.setText(jo.getString("norekening"));
                        edtTanggalTransfer.setText(iv.ChangeFormatDateString(jo.getString("tgltransfer"), formatDate, formatDateDisplay));

                        JSONArray jPiutang = jo.getJSONArray("piutang");
                        listNota = new ArrayList<>();
                        for(int j = 0; j < jPiutang.length(); j++){

                            JSONObject jdp = jPiutang.getJSONObject(j);
                            listNota.add(new OptionItem(
                                    jdp.getString("nonota"),
                                    jdp.getString("tanggal"),
                                    iv.doubleToString(iv.parseNullDouble(jdp.getString("sisa"))),
                                    iv.doubleToString(iv.parseNullDouble(jdp.getString("jumlah"))),
                                    jdp.getString("tanda"),
                                    true));
                        }

                        setSalesPiutang(listNota);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEvent() {

        edtTanggal.setText(iv.getCurrentDate(formatDateDisplay));
        edtTanggal.setKeyListener(null);

        edtTanggalTransfer.setText(iv.getCurrentDate(formatDateDisplay));
        edtTanggalTransfer.setKeyListener(null);
        iv.datePickerEvent(context,edtTanggalTransfer,"RIGHT",formatDateDisplay, iv.getCurrentDate(formatDateDisplay));

        rgCaraBayar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                getDataBank();
            }
        });

        edtTotal.addTextChangedListener(new TextWatcher() {

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
                    edtTotal.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtTotal.setText(formatted);
                    edtTotal.setSelection(formatted.length());

                    if(adapterPiutangSales != null){

                        adapterPiutangSales.resetData();
                    }

                    if(isEdit){
                        sisaPiutang = 0;
                    }else{
                        sisaPiutang = iv.parseNullDouble(cleanString);
                    }

                    edtSisa.setText(iv.ChangeToRupiahFormat(sisaPiutang));

                    edtDiskon.setText("");
                    hitungTotalDibayar();
                    edtTotal.addTextChangedListener(this);
                }
            }
        });

        edtDiskon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                calculateDiscount();
            }
        });

        edtHargaDiskon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentStringDiskon)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtHargaDiskon.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentStringDiskon = formatted;
                    hitungTotalDibayar();
                    edtHargaDiskon.setText(formatted);
                    edtHargaDiskon.setSelection(formatted.length());
                    edtHargaDiskon.addTextChangedListener(this);
                }
            }
        });

        llSaveContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEdit){

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setIcon(R.mipmap.kartika_logo)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah anda yakin ingin menghapus data?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteData();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }else{

                    validateBeforeSave();
                }

            }
        });

        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadChooserDialog();
            }
        });

        if(totalPiutangCurrent > 0) edtTotal.setText(iv.doubleToString(totalPiutangCurrent));
        getDataBank();
    }

    private void hitungTotalDibayar() {

        String totalBayar = edtTotal.getText().toString().replaceAll("[,.]", "");
        String totalHargaDiskon = edtHargaDiskon.getText().toString().replaceAll("[,.]", "");
        edtTotalDibayar.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(iv.parseNullDouble(totalBayar) - iv.parseNullDouble(totalHargaDiskon))));
    }

    private void calculateDiscount(){

        String changedString = edtDiskon.getText().toString().replaceAll("[+]", ",");

        List<String> diskonList = new ArrayList<String>(Arrays.asList(changedString.split(",")));
        List<Double> diskonListDouble = new ArrayList<Double>();
        for (String diskon: diskonList){

            try {
                Double x = iv.parseNullDouble(diskon);
                diskonListDouble.add(x);
            }catch (Exception e){
                Double df = Double.valueOf(0);
                diskonListDouble.add(df);
                e.printStackTrace();
            }
        }

        if(diskonListDouble.size()>0 && edtTotal.getText().length() > 0){

            String hargaClean = edtTotal.getText().toString().replaceAll("[,.]", "");
            double hargaAwal = iv.parseNullDouble(hargaClean);
            double newHargaNetto = 0;
            double totalDiskon = 0;
            Integer index = 1;

            if(diskonListDouble.size() > 0){
                for(Double i: diskonListDouble){
                    if(index == 1){

                        //double minDiskon = i / 100 * hargaAwal;
                        newHargaNetto = hargaAwal - (i / 100 * hargaAwal);
                        totalDiskon += (i / 100 * hargaAwal);
                    }else{

                        totalDiskon += (i / 100 * newHargaNetto);
                        newHargaNetto = newHargaNetto - ( i / 100 * newHargaNetto);
                    }
                    index++;
                }
            }else{
                newHargaNetto = hargaAwal;
            }
            edtHargaDiskon.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(totalDiskon)));

        }else{
            // clear field
            edtHargaDiskon.setText("0");
        }
    }

    public static void updateSisa(){

        edtSisa.setText(iv.ChangeToRupiahFormat(sisaPiutang));

        if(isSinggleData){

            double sisaYangDibayar = 0;

            if(adapterPiutangSales != null){

                List<OptionItem> listItems = adapterPiutangSales.getItems();
                try {

                    if(listItems.get(0).isSelected()){

                        double totalNota = iv.parseNullDouble(listItems.get(0).getAtt1());
                        double total = iv.parseNullDouble(edtTotal.getText().toString().replaceAll("[,.]", ""));

                        if(totalNota > total){

                            sisaYangDibayar = totalNota - total;
                        }
                    }
                }catch (Exception e){e.printStackTrace();}

            }

            edtSisaTotal.setText(iv.ChangeToRupiahFormat(sisaYangDibayar));
        }

    }

    private void getDataBank() {

        pbLoading.setVisibility(View.VISIBLE);

        tilNamaPemilik.setHint("Nama Sumber (Pemilik)");
        //edtNamaPemilik.setHint("Nama Pemilik");
        tilTanggalTransfer.setHint("Tanggal Transfer");
        //edtTanggalTransfer.setHint("Tanggal Transfer");

        if(rbTunai.isChecked()){
            crBayar = "T";
        }else if(rbBank.isChecked()){
            crBayar = "B";
        }else{
            crBayar = "G";

            tilNamaPemilik.setHint("Nomor Giro");
            tilTanggalTransfer.setHint("Tanggal Giro Jatuh Tempo");
            //edtNamaPemilik.setHint("Nomor Giro");
            //edtTanggalTransfer.setHint("Tanggal Giro");
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("cara_bayar", crBayar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getMasterBayar, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listBank = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listBank.add(new OptionItem(
                                    jo.getString("kode"),
                                    jo.getString("nama"),
                                    jo.getString("norekening")));
                        }
                    }else{

                        if(!crBayar.equals("T"))
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    setBankAdapter(listBank);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setBankAdapter(null);
                }

                if(!idSetoran.equals("")) getDetailSetoran();
            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setBankAdapter(null);
                if(!idSetoran.equals("")) getDetailSetoran();
            }
        });
    }

    private void validateBeforeSave(){

        if(spBank.getAdapter() == null){

            Toast.makeText(context, "Tunggu hingga data termuat",Toast.LENGTH_LONG).show();
            return;
        }

        if(edtTotal.getText().toString().isEmpty() || edtTotal.getText().toString().equals("0")){

            edtTotal.setError("Total harap diisi");
            edtTotal.requestFocus();

            //Toast.makeText(context, "Total masih kosong, periksa data anda", Toast.LENGTH_LONG).show();
            return;
        }else{
            edtTotal.setError(null);
        }

        if(crBayar.equals("G") && edtDariBank.getText().toString().isEmpty()){

            edtDariBank.setError("Harap diisi");
            edtDariBank.requestFocus();
            return;
        }else{

            edtDariBank.setError(null);
        }

        if(sisaPiutang != 0){

            Toast.makeText(context, "Sisa harus habis atau Rp 0", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Konfirmasi")
                .setIcon(R.mipmap.kartika_logo)
                .setMessage("Apakah anda yakin ingin menambahkan ke daftar setoran?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        saveData();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();

    }

    public void deleteData() {

        llSaveContainer.setEnabled(false);

        dialogBox = new DialogBox(DetailFormSetoran.this);
        dialogBox.showDialog(false);

        ApiVolley apiVolley = new ApiVolley(getApplicationContext(), new JSONObject(), "GET", ServerURL.deleteSetoran+idSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                llSaveContainer.setEnabled(true);
                dialogBox.dismissDialog();
                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        onBackPressed();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                llSaveContainer.setEnabled(true);
                dialogBox.dismissDialog();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveData() {

        String namaBank = ((OptionItem) spBank.getSelectedItem()).getText();
        String kodeBank = ((OptionItem) spBank.getSelectedItem()).getValue();

        List<OptionItem> bufferItems = new ArrayList<>();

        if(adapterPiutangSales != null){

            List<OptionItem> listItems = adapterPiutangSales.getItems();
            int i = 0;
            for(OptionItem item: listItems){

                if(item.isSelected()){

                    JSONObject jOrder = new JSONObject();
                    try {

                        String total = (listItems.size() == 1
                                && !edtTotalDibayar.getText().toString().isEmpty()
                                && !edtTotalDibayar.getText().toString().equals("0")) ? edtTotalDibayar.getText().toString() : iv.doubleToString(iv.parseNullDouble(item.getAtt2()));

                        total = total.replaceAll("[,.]", "");
                        jOrder.put("nonota", item.getValue());
                        jOrder.put("jumlah", total);
                        jOrder.put("crbayar", crBayar);
                        jOrder.put("kode_bank", kodeBank);
                        jOrder.put("bank", namaBank);
                        jOrder.put("kdcus", kdcus);
                        jOrder.put("total", edtTotal.getText().toString().replaceAll("[,.]", ""));
                        jOrder.put("diskon", edtDiskon.getText().toString());
                        jOrder.put("totaldiskon", edtHargaDiskon.getText().toString().replaceAll("[,.]", ""));
                        jOrder.put("dari_bank", edtDariBank.getText().toString());
                        jOrder.put("dari_pemilik", edtNamaPemilik.getText().toString());
                        jOrder.put("dari_rekening", edtDariNorek.getText().toString());
                        jOrder.put("norekening", edtDariNorek.getText().toString());
                        jOrder.put("tgltransfer", iv.ChangeFormatDateString(edtTanggalTransfer.getText().toString(), formatDateDisplay, formatDate));
                        if(isKhusus){

                            if(listPhoto.size() > 0){

                                jOrder.put("file", listPhoto.get(0).getKeterangan());
                            }else{
                                jOrder.put("file", "");
                            }
                        }
                        ListNotaPiutang.jaSetoran.put(jOrder);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(item.getAtt1().equals(item.getAtt2())){

                        //ListNotaPiutang.listNota.remove(i);
                    }else{

                        OptionItem newItem = new OptionItem(
                                item.getValue(),
                                item.getText(),
                                iv.doubleToStringFull(iv.parseNullDouble(item.getAtt1()) - iv.parseNullDouble(item.getAtt2())),
                                "0",
                                item.getAtt3(),
                                false
                        );

                        bufferItems.add(newItem);
                    }
                }else{

                    bufferItems.add(item);
                }

                i++;
            }

            ListNotaPiutang.listNota = new ArrayList<>(bufferItems);
        }

        Intent intent = new Intent(context, DetailCheckoutSetoran.class);
        intent.putExtra("kdcus", kdcus);
        intent.putExtra("namacus", namaCus);
        intent.putExtra("khusus", isKhusus);
        startActivity(intent);
        finish();
    }

    private void setBankAdapter(List<OptionItem> listItem) {

        spBank.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem);
            spBank.setAdapter(adapter);

            spBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    OptionItem item = (OptionItem) parent.getItemAtPosition(position);
                    edtKeBank.setText(item.getText());
                    edtKeNorek.setText(item.getAtt1());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
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

        if(isConfirm){

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Pembatalan")
                    .setMessage("Setoran belum terproses, anda yakin membatalkan proses setoran?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            isConfirm = false;
                            onBackPressed();
                        }
                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            isConfirm = true;
                        }
                    }).show();
        }else{

            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }
}

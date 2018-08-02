package gmedia.net.id.kartikaelektrik.activitySetoran.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

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

    private ItemValidation iv = new ItemValidation();

    public SetoranPernobuktiAdapter(Activity context, List items) {
        super(context, R.layout.adapter_setoran_pernota, items);
        this.context = context;
        this.items = items;
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
        private LinearLayout container;
        private TextView item1;
        private TextView item2;
        private TextView item3;
        private TextView item4;
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
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
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
}

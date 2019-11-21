package gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class ListPengeluaranAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;

    public ListPengeluaranAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_pengeluaran, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3;
        private RelativeLayout rlDelete;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_pengeluaran, null);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            holder.rlDelete = convertView.findViewById(R.id.rl_delete);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        final CustomListItem itemSelected = items.get(position);
        holder.tvItem1.setText(iv.ChangeFormatDateString(itemSelected.getListItem2(), formatDate, formatDateDisplay));
        holder.tvItem2.setText(itemSelected.getListItem3());
        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem4()));
        holder.rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menghapus " + itemSelected.getListItem3() + " ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                dialogBox = new DialogBox(context);
                                dialogBox.showDialog(false);

                                ApiVolley restService = new ApiVolley(context, new JSONObject(), "GET", ServerURL.deletePengeluaran + itemSelected.getListItem1(), "", "", 0,
                                        new ApiVolley.VolleyCallback(){
                                            @Override
                                            public void onSuccess(String result){

                                                dialogBox.dismissDialog();
                                                JSONObject responseAPI = new JSONObject();
                                                try {
                                                    responseAPI = new JSONObject(result);
                                                    String statusString = responseAPI.getJSONObject("metadata").getString("status");
                                                    String message = responseAPI.getJSONObject("metadata").getString("message");
                                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                                    if(iv.parseNullInteger(statusString) == 200){

                                                        items.remove(position);
                                                        notifyDataSetChanged();
                                                    }

                                                }catch (Exception e){
                                                    Toast.makeText(context,"Terjadi kesalahan dalam parsing data", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onError(String result) {

                                                dialogBox.dismissDialog();
                                                Toast.makeText(context, result,Toast.LENGTH_LONG).show();
                                            }
                                        });

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
}
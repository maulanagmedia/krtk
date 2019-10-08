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
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class PengajuanTempoAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private ItemValidation iv = new ItemValidation();

    public PengajuanTempoAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_list_tempo, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem0, tvItem1, tvItem2, tvItem3;
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
            convertView = inflater.inflate(R.layout.adapter_list_tempo, null);
            holder.tvItem0 = convertView.findViewById(R.id.tv_item0);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatTimestamp = context.getResources().getString(R.string.format_date1);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        final CustomListItem itemSelected = items.get(position);
        holder.tvItem0.setText(iv.ChangeFormatDateString(itemSelected.getListItem2(), formatTimestamp, formatDateDisplay));
        holder.tvItem1.setText(itemSelected.getListItem3());
        holder.tvItem2.setText(itemSelected.getListItem4());
        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem5()));

        return convertView;

    }
}
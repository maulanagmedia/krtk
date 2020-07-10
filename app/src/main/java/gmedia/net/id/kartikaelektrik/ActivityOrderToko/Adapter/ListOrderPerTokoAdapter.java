package gmedia.net.id.kartikaelektrik.ActivityOrderToko.Adapter;

import android.app.Activity;
import android.icu.text.UFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.FormatItem;
import gmedia.net.id.kartikaelektrik.util.ImageUtils;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListOrderPerTokoAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListOrderPerTokoAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_order_per_toko, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvNonota, tvTanggal, tvTempo, tvPromo, tvJmlItem, tvTotal;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ListOrderPerTokoAdapter.ViewHolder holder = new ListOrderPerTokoAdapter.ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_order_per_toko, null);
            holder.tvNonota = convertView.findViewById(R.id.tv_nonota);
            holder.tvTanggal = convertView.findViewById(R.id.tv_tgl);
            holder.tvTempo = convertView.findViewById(R.id.tv_tempo);
            holder.tvPromo = convertView.findViewById(R.id.tv_promo);
            holder.tvTempo = convertView.findViewById(R.id.tv_tempo);
            holder.tvJmlItem = convertView.findViewById(R.id.tv_item);
            holder.tvTotal = convertView.findViewById(R.id.tv_total);
            convertView.setTag(holder);
        }else{
            holder = (ListOrderPerTokoAdapter.ViewHolder) convertView.getTag();
        }

        final CustomListItem itemSelected = items.get(position);
        holder.tvNonota.setText(itemSelected.getListItem1());
        holder.tvTanggal.setText(iv.ChangeFormatDateString(itemSelected.getListItem2(), FormatItem.formatDate, FormatItem.formatDateDisplay));
        holder.tvTempo.setText(itemSelected.getListItem4());
        holder.tvPromo.setText(itemSelected.getListItem7());
        holder.tvJmlItem.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem6()));
        holder.tvTotal.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem5()));

        return convertView;


    }
}

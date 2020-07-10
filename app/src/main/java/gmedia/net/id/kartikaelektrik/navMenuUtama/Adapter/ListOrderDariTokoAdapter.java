package gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ImageUtils;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListOrderDariTokoAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListOrderDariTokoAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_order_dari_toko, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private ImageView ivProfile;
        private TextView tvNama, tvNota, tvNominal;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ListOrderDariTokoAdapter.ViewHolder holder = new ListOrderDariTokoAdapter.ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_order_dari_toko, null);
            holder.ivProfile = convertView.findViewById(R.id.iv_profile);
            holder.tvNama = convertView.findViewById(R.id.tv_nama);
            holder.tvNominal = convertView.findViewById(R.id.tv_nominal);
            holder.tvNota = convertView.findViewById(R.id.tv_nota);
            convertView.setTag(holder);
        }else{
            holder = (ListOrderDariTokoAdapter.ViewHolder) convertView.getTag();
        }

        final CustomListItem itemSelected = items.get(position);
        holder.tvNama.setText(itemSelected.getListItem2());
        holder.tvNota.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem3()) + " Pesanan");
        holder.tvNominal.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem4()));
        ImageUtils iu = new ImageUtils();
        iu.LoadProfileImage(context, itemSelected.getListItem5(), holder.ivProfile);

        return convertView;


    }
}

package gmedia.net.id.kartikaelektrik.navMenuUtama.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class PotensiDendaAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomListItem> items;
    private ItemValidation iv = new ItemValidation();

    public PotensiDendaAdapter(Activity context, List<CustomListItem> items) {
        super(context, R.layout.adapter_potensi_denda, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvLabel;
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
            convertView = inflater.inflate(R.layout.adapter_potensi_denda, null);
            holder.tvLabel = convertView.findViewById(R.id.tv_label);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = convertView.findViewById(R.id.tv_item5);
            holder.tvItem6 = convertView.findViewById(R.id.tv_item6);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        final CustomListItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getListItem2());
        holder.tvItem2.setText(itemSelected.getListItem3());
        holder.tvItem3.setText(iv.ChangeFormatDateString(itemSelected.getListItem4(), formatDate, formatDateDisplay));
        holder.tvItem4.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem5()));
        holder.tvItem5.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem6()));
        holder.tvLabel.setText(itemSelected.getListItem9());
        if(itemSelected.getListItem9().equals("Komisi")){

            holder.tvItem6.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem7()));
        }else{

            holder.tvItem6.setText(iv.ChangeToCurrencyFormat(itemSelected.getListItem8()));
        }

        return convertView;

    }
}
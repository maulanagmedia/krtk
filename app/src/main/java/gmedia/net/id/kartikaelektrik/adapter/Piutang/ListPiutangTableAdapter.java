package gmedia.net.id.kartikaelektrik.adapter.Piutang;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.Piutang;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

import java.util.List;

/**
 * Created by Shin on 1/16/2017.
 */

public class ListPiutangTableAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<Piutang> items;
    private int rowPerTableItem;
    private View viewInflater;
    private ItemValidation iv = new ItemValidation();
    private final int backgroundcolor = Color.parseColor("#E0DFDB");

    public ListPiutangTableAdapter(Activity context, int resource, List<Piutang> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    @Override
    public int getCount() {
        if(items.size() < rowPerTableItem){
            return items.size();
        }else{
            return rowPerTableItem;
        }
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_retro_menu_5, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_subtitle_1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_subtitle_2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_subtitle_3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_subtitle_4);
            holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_subtitle_5);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Piutang piutang = items.get(position);
        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display);
        holder.tvItem1.setText(piutang.getNoNota());
        holder.tvItem2.setText(piutang.getNamaCustomer());
        String jumlahPiutang = iv.ChangeToRupiahFormat(Float.parseFloat(piutang.getPiutang()));
        holder.tvItem3.setText(iv.ChangeFormatDateString(piutang.getTanggal(), formatDate, formatDateDisplay));
        holder.tvItem4.setText(jumlahPiutang);
        holder.tvItem5.setText(piutang.getStatus());

        return convertView;

    }
}

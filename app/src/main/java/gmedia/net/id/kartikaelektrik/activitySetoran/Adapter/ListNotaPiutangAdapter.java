package gmedia.net.id.kartikaelektrik.activitySetoran.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.DetailFormSetoran;
import gmedia.net.id.kartikaelektrik.activitySetoran.ListNotaPiutang;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListNotaPiutangAdapter extends ArrayAdapter {

    private Activity context;
    private List<OptionItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListNotaPiutangAdapter(Activity context, List<OptionItem> items) {
        super(context, R.layout.cv_list_nota_with_cb, items);
        this.context = context;
        this.items = new ArrayList<>(items);
    }

    private static class ViewHolder {
        private CheckBox cbItem;
        private TextView tvItem1;
        private LinearLayout llContainer;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }


    public List<OptionItem> getItems(){

        List<OptionItem> outputData = new ArrayList<>(items);
        return outputData;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_nota_piutang, null);
            holder.llContainer = (LinearLayout) convertView.findViewById(R.id.ll_container);
            holder.cbItem = (CheckBox) convertView.findViewById(R.id.cb_item);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            convertView.setTag(holder);
        }else{
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_nota_piutang, null);
            holder.llContainer = (LinearLayout) convertView.findViewById(R.id.ll_container);
            holder.cbItem = (CheckBox) convertView.findViewById(R.id.cb_item);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            convertView.setTag(holder);
            //holder = (ViewHolder) convertView.getTag();
        }

        final OptionItem itemSelected = items.get(position);

        if(itemSelected.isSelected()){
            holder.cbItem.setChecked(true);
        }else{
            holder.cbItem.setChecked(false);
        }

        if(itemSelected.getAtt3().toUpperCase().equals("P")){

            holder.llContainer.setBackgroundColor(context.getResources().getColor(R.color.color_red_bg));
        }else{

            holder.llContainer.setBackgroundColor(context.getResources().getColor(R.color.color_blue_bg));
        }

        holder.cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                items.get(position).setSelected(b);
                ListNotaPiutang.updateHarga();
                notifyDataSetChanged();
            }
        });

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDate2 = context.getResources().getString(R.string.format_date_display);
        holder.cbItem.setText(itemSelected.getValue()+" ("+ iv.ChangeFormatDateString(itemSelected.getText(),formatDate, formatDate2)+")");
        holder.tvItem1.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(itemSelected.getAtt1())));
        return convertView;

    }
}

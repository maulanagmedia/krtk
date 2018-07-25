package gmedia.net.id.kartikaelektrik.activitySetoran.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySetoran.DetailFormSetoran;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListDetailNotaAdapter extends ArrayAdapter {

    private Activity context;
    private List<OptionItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListDetailNotaAdapter(Activity context, List<OptionItem> items) {
        super(context, R.layout.cv_list_nota_with_cb, items);
        this.context = context;
        this.items = new ArrayList<>(items);
    }

    private static class ViewHolder {
        private CheckBox cbItem;
        private TextView tvItem1, tvItem2;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public void resetData(){

        for(int i = 0; i < items.size(); i++){

            items.get(i).setAtt2("0");
            items.get(i).setSelected(false);
        }

        notifyDataSetChanged();
    }

    public List<OptionItem> getItems(){


        List<OptionItem> outputData = new ArrayList<>(items);
        return outputData;
    }

    @Override
    public int getViewTypeCount() {

        if (getCount() != 0)
            return getCount();

        return 1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.cv_list_nota_with_cb, null);
            holder.cbItem = (CheckBox) convertView.findViewById(R.id.cb_item);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            convertView.setTag(holder);
        }else{
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.cv_list_nota_with_cb, null);
            holder.cbItem = (CheckBox) convertView.findViewById(R.id.cb_item);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            convertView.setTag(holder);
            //holder = (ViewHolder) convertView.getTag();
        }

        final OptionItem itemSelected = items.get(position);

        if(itemSelected.isSelected()){
            holder.cbItem.setChecked(true);
        }else{
            holder.cbItem.setChecked(false);
        }

        holder.cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                //items.get(position).setSelected(b);

                if(b){ // ditambahkan ke list

                    if(DetailFormSetoran.sisaPiutang > 0){

                        if(DetailFormSetoran.sisaPiutang - iv.parseNullDouble(itemSelected.getAtt1()) >= 0){

                            items.get(position).setAtt2(itemSelected.getAtt1());
                            DetailFormSetoran.sisaPiutang -= iv.parseNullDouble(itemSelected.getAtt1());

                        }else{ // disisakan

                            double sisa = iv.parseNullDouble(itemSelected.getAtt1()) - DetailFormSetoran.sisaPiutang;
                            items.get(position).setAtt2(iv.doubleToStringFull(iv.parseNullDouble(itemSelected.getAtt1())-sisa));
                            DetailFormSetoran.sisaPiutang = 0;
                        }

                        items.get(position).setSelected(true);

                    }else{

                        holder.cbItem.setChecked(false);
                        items.get(position).setSelected(false);
                    }

                    //items.get(position).setAtt2(itemSelected.getAtt1());


                }else{ // dikurangi dari list

                    DetailFormSetoran.sisaPiutang += (iv.parseNullDouble(itemSelected.getAtt2()));
                    items.get(position).setAtt2("0");
                    items.get(position).setSelected(false);
                }

                DetailFormSetoran.updateSisa();
                notifyDataSetChanged();
            }
        });

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDate2 = context.getResources().getString(R.string.format_date_display);
        holder.cbItem.setText(itemSelected.getValue()+" ("+ iv.ChangeFormatDateString(itemSelected.getText(),formatDate, formatDate2)+")");
        holder.tvItem1.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(itemSelected.getAtt1())));
        holder.tvItem2.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(itemSelected.getAtt2())));
        return convertView;

    }
}

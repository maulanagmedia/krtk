package gmedia.net.id.kartikaelektrik.MenuAdminApvHapusDenda.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.ActivityHapusDenda.DetailDendaCustomerActivity;
import gmedia.net.id.kartikaelektrik.MenuAdminApvHapusDenda.DetailApvHapusDendaCustomer;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListPiutangDendaCustomerdapter extends ArrayAdapter{

    private Activity context;
    private List<OptionItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListPiutangDendaCustomerdapter(Activity context, List<OptionItem> items) {
        super(context, R.layout.adapter_denda_piutang, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private CheckBox cbItem;
        private TextView tvItem1, tvItem2, tvItem3, tvItem4;
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

        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(R.layout.adapter_denda_piutang, null);
        holder.cbItem = (CheckBox) convertView.findViewById(R.id.cb_item);
        holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
        holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
        holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
        holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item4);

        convertView.setTag(holder);

        final OptionItem itemSelected = items.get(position);

        if(itemSelected.isSelected()){
            holder.cbItem.setChecked(true);
        }else{
            holder.cbItem.setChecked(false);
        }

        String formatDate = context.getResources().getString(R.string.format_date);
        String formatDateDisplay = context.getResources().getString(R.string.format_date_display);

        holder.cbItem.setText(itemSelected.getText());
        holder.tvItem1.setText(itemSelected.getAtt1());
        holder.tvItem2.setText(iv.ChangeToCurrencyFormat(itemSelected.getAtt2()));
        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(itemSelected.getAtt3()));
        holder.tvItem4.setText(iv.ChangeToCurrencyFormat(itemSelected.getAtt4()));

        holder.cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                items.get(position).setSelected(b);
                ((DetailApvHapusDendaCustomer) context).updateCCID();
            }
        });

        return convertView;

    }
}

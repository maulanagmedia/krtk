package gmedia.net.id.kartikaelektrik.activityCustomerLimit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityCustomer.CustomerDetail;
import gmedia.net.id.kartikaelektrik.model.Customer;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 1/19/2017.
 */

public class ListCustomerLimitAdapter extends ArrayAdapter {

    private Activity context;
    private List<Customer> items;
    private final int backgroundcolor = Color.parseColor("#E0DFDB");
    private View listViewItem;
    private String urlDeleteCustomer;
    private int rowPerTableItem;

    private ItemValidation iv = new ItemValidation();

    public ListCustomerLimitAdapter(Activity context, int resource, List items) {
        super(context, R.layout.adapter_customer, items);
        this.context = context;
        this.items = items;
        urlDeleteCustomer = context.getResources().getString(R.string.url_delete_customer_by_id);
        rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
    }

    private static class ViewHolder {
        private LinearLayout container;
        private TextView item1;
        private TextView item2;
        private TextView item3;
        private TextView item4;
        private LinearLayout deleteContainer;
    }

    @Override
    public int getCount() {
        if(items.size() < rowPerTableItem){
            return items.size();
        }else{
            return rowPerTableItem;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_customer, null);
            holder.container = (LinearLayout) convertView.findViewById(R.id.ll_item_container);
            holder.item1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.item2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.item3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.item4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.deleteContainer = (LinearLayout) convertView.findViewById(R.id.ll_delete_container);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Customer customer = items.get(position);
        holder.item1.setText(customer.getNamaCustomer());
        holder.item2.setText(customer.getAlamat() + " " + customer.getKota());
        holder.item3.setText(iv.ChangeToRupiahFormat(iv.parseNullFloat(customer.getMaxPiutang())));
        holder.item4.setText(iv.ChangeToRupiahFormat(iv.parseNullFloat(customer.getTotalPiutang())));

        return convertView;
    }

}

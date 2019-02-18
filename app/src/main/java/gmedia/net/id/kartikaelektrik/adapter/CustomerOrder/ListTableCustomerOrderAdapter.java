package gmedia.net.id.kartikaelektrik.adapter.CustomerOrder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityTambahOrderSales.ListBarangSalesOrder;
import gmedia.net.id.kartikaelektrik.model.Customer;

import java.util.List;

/**
 * Created by indra on 27/12/2016.
 */

public class ListTableCustomerOrderAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<Customer> items;
    private final int rowPerTableItem = 100;
    private final int backgroundcolor = Color.parseColor("#E0DFDB");
    private View viewInflater;

    public ListTableCustomerOrderAdapter(Activity context, int resource, List<Customer> customerList) {
        super(context, resource, customerList);
        this.context = context;
        this.resource = resource;
        this.items = customerList;
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
        private TextView edCustomerName, edCustomerAddress;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_double_menu, null);
            holder.edCustomerName = (TextView) convertView.findViewById(R.id.tv_primary_menu_name);
            holder.edCustomerAddress = (TextView) convertView.findViewById(R.id.tv_secondary_menu_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

//        if(position % 2 != 0) convertView.setBackgroundColor(backgroundcolor);
        final Customer customer = items.get(position);
        holder.edCustomerName.setText(customer.getNamaCustomer());
        holder.edCustomerAddress.setText(customer.getAlamat());

        if(customer.getXdenda().equals("0")){
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x0));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x0));
        }else if(customer.getXdenda().equals("1")){
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x1));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x1));
        }else if(customer.getXdenda().equals("2")){
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x2));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x2));
        }else if(customer.getXdenda().equals("3")){
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x3));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x3));
        }else{
            holder.edCustomerName.setTextColor(context.getResources().getColor(R.color.color_x4));
            holder.edCustomerAddress.setTextColor(context.getResources().getColor(R.color.color_x4));
        }

        //region click event when tap on order list
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategory(customer.getKodeCustomer(),customer.getNamaCustomer(),customer.getTempo());
            }
        });
        //endregion

        return convertView;

    }

    private void showCategory(String kdCus, String customerName, String tempo){
        Intent intent = new Intent(context, ListBarangSalesOrder.class);
        intent.putExtra("kdCus",kdCus);
        intent.putExtra("namaPelanggan",customerName);
        intent.putExtra("tempo",tempo);
        context.startActivity(intent);
    }
}

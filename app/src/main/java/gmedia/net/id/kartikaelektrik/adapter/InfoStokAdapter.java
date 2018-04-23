package gmedia.net.id.kartikaelektrik.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.model.Barang;

import java.util.List;

/**
 * Created by Shin on 1/19/2017.
 */

public class InfoStokAdapter extends ArrayAdapter {

    private Activity context;
    private int resource;
    private List<Barang> items;
    private int rowPerTableItem;
    private View viewInflater;
    private ItemValidation iv = new ItemValidation();
    private String jenis = "";

    public InfoStokAdapter(Activity context, int resource, List items, String jenis) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.rowPerTableItem = Integer.parseInt(context.getResources().getString(R.string.rows_per_page_table));
        this.jenis = jenis;
    }

    public void AddListItemAdapter(List items){
        this.items.addAll(items);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        /*if(items.size() < rowPerTableItem){
            return items.size();
        }else{
            return rowPerTableItem;
        }*/
        return items.size();
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3;
        private TextView tvNamaBarang, tvStok, tvHarga, tvSisa;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){

            if(jenis.equals("canvas")){

                LayoutInflater inflater = context.getLayoutInflater();
                convertView = inflater.inflate(R.layout.adapter_stok, null, true);
                holder.tvNamaBarang = (TextView) convertView.findViewById(R.id.tv_nama_barang);
                holder.tvStok = (TextView) convertView.findViewById(R.id.tv_stok);
                holder.tvHarga = (TextView) convertView.findViewById(R.id.tv_harga);
                holder.tvSisa = (TextView) convertView.findViewById(R.id.tv_sisa);
                convertView.setTag(holder);
            }else{
                LayoutInflater inflater = context.getLayoutInflater();
                convertView = inflater.inflate(R.layout.adapter_stok, null, true);
                holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_nama_barang);
                holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_stok);
                holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_harga);
                convertView.setTag(holder);
            }
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Barang barang = items.get(position);

        if(jenis.equals("canvas")){

            holder.tvNamaBarang.setText(barang.getNamaBarang());
            holder.tvStok.setText(barang.getStok());
            String harga = iv.ChangeToRupiahFormat(Float.parseFloat(barang.getHarga()));
            holder.tvHarga.setText(harga);
            holder.tvSisa.setText(barang.getSisa());
        }else{

            holder.tvItem1.setText(barang.getNamaBarang());
            holder.tvItem2.setText(barang.getStok());
            String harga = iv.ChangeToRupiahFormat(Float.parseFloat(barang.getHarga()));
            holder.tvItem3.setText(harga);
        }
        return convertView;
    }
}

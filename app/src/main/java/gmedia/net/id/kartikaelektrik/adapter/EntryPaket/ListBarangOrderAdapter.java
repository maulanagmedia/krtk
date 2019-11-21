package gmedia.net.id.kartikaelektrik.adapter.EntryPaket;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityEntryPaket.OrderDetailPaket;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

/**
 * Created by Shin on 24/07/2017.
 */

public class ListBarangOrderAdapter extends RecyclerView.Adapter<ListBarangOrderAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItem1, tvItem2;
        public EditText edtItem1, edtItem2, edtItem3;

        public MyViewHolder(View view) {
            super(view);
            tvItem1 = (TextView) view.findViewById(R.id.tv_item_1);
            tvItem2 = (TextView) view.findViewById(R.id.tv_item_2);
            edtItem1 = (EditText) view.findViewById(R.id.edt_item_1);
            edtItem2 = (EditText) view.findViewById(R.id.edt_item_2);
            edtItem3 = (EditText) view.findViewById(R.id.edt_item_3);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_paket_barang, parent, false);

        return new MyViewHolder(itemView);
    }

    public ListBarangOrderAdapter(Context context, List masterList){
        this.context = context;
        this.masterList = masterList;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CustomListItem cli = masterList.get(position);
        holder.tvItem1.setText(cli.getListItem3());
        holder.tvItem2.setText(cli.getListItem5());
        holder.edtItem1.setText(cli.getListItem4()); // jumlah
        holder.edtItem2.setText(cli.getListItem6()); // harga
        holder.edtItem3.setText(cli.getListItem7()); // total

        holder.edtItem1.addTextChangedListener(new TextWatcher() {

            private String s;
            private long after;
            private Thread t;
            private Runnable runnable_EditTextWatcher = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if ((System.currentTimeMillis() - after) > 1300)
                        {

                            // Hitung ulang total
                            final Long jumlahLong = (holder.edtItem1.length() > 0 ? iv.parseNullLong(holder.edtItem1.getText().toString()) : 0);
                            Double jumlah = (holder.edtItem1.length() > 0 ? iv.parseNullDouble(holder.edtItem1.getText().toString()) : Double.valueOf(0));
                            final Double harga = iv.parseNullDouble(cli.getListItem6());
                            final Double totalBaru = jumlah * harga;

                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.edtItem3.setText(iv.doubleToString(totalBaru));
                                }
                            });

                            CustomListItem editedItem = cli;

                            editedItem.setListItem4(holder.edtItem1.getText().toString());
                            editedItem.setListItem7(iv.doubleToString(totalBaru));

                            masterList.set(position, editedItem);

                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    long jumlahTotal = 0;
                                    Double hargaTotal = Double.valueOf(0);
                                    for (CustomListItem item : masterList){

                                        jumlahTotal += iv.parseNullLong(item.getListItem4());
                                        hargaTotal += iv.parseNullDouble(item.getListItem7());
                                    }

                                    OrderDetailPaket.setTotal(jumlahTotal, hargaTotal);
                                }
                            });


                            t = null;
                            break;
                        }
                    }
                }
            };

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                s = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                OrderDetailPaket.onEditing = true;
                after = System.currentTimeMillis();
                if (t == null)
                {
                    t = new Thread(runnable_EditTextWatcher);
                    t.start();

                }
            }
        });
    }

    public List<CustomListItem> getMasterData(){
        return masterList;
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }
}

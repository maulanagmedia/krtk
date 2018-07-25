package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import gmedia.net.id.kartikaelektrik.R;

/**
 * Created by Shin on 2/1/2017.
 */

public class MenuUtamaCustomerLimit extends Fragment {

    private View layout;
    private Context context;

    public MenuUtamaCustomerLimit(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.menu_utama_customer_limit, container, false);
        getActivity().setTitle("Menu Admin");
        context = getActivity();
        initUI();
        return layout;
    }

    public void setView(Context context, View layout){
        this.context = context;
        this.layout = layout;
        initUI();
    }

    private void initUI() {

    }
}

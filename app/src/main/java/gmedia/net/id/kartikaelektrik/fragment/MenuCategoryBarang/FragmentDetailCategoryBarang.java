package gmedia.net.id.kartikaelektrik.fragment.MenuCategoryBarang;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gmedia.net.id.kartikaelektrik.R;

/**
 * Created by indra on 28/12/2016.
 */

public class FragmentDetailCategoryBarang extends Fragment {

    public FragmentDetailCategoryBarang(){}
    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_detail_category_barang, container, false);
        initUI(layout);
        return layout;
    }

    private void initUI(View view){

    }
}

package gmedia.net.id.kartikaelektrik.fragment.MenuCategoryBarang;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gmedia.net.id.kartikaelektrik.R;

/**
 * Created by indra on 27/12/2016.
 */

public class FragmentListCategoryBarang extends Fragment {

    public FragmentListCategoryBarang(){}
    private View layout;
    private final String TAG = "listCategory";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_list_category_barang, container, false);
        return layout;
    }
}

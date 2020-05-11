package gmedia.net.id.kartikaelektrik.navMenuUtama;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class MenuUtamaOmsetManager extends Fragment {

    private View layout;
    private Context context;
    private ItemValidation iv  = new ItemValidation();
    private SessionManager session;

    public MenuUtamaOmsetManager() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        layout = inflater.inflate(R.layout.fragment_menu_utama_omset_manager, container, false);
        getActivity().setTitle("Menu Omset Manager");
        context = getActivity();
        session = new SessionManager(context);
        initUI();
        return layout;
    }

    public void setView(Context context, View layout){
        this.context = context;
        this.layout = layout;
        session = new SessionManager(context);
        initUI();
    }

    private void initUI() {

    }
}

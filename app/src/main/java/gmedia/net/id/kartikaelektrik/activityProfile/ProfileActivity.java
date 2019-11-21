package gmedia.net.id.kartikaelektrik.activityProfile;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private TextView tvNama, tvNik, tvJabatan, tvNikLogin, tvNamaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);

        setTitle("Profile");
        initUI();
    }

    private void initUI() {

        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvNik = (TextView) findViewById(R.id.tv_nik);
        tvJabatan = (TextView) findViewById(R.id.tv_jabatan);
        tvNamaLogin = (TextView) findViewById(R.id.tv_nama_login);
        tvNikLogin = (TextView) findViewById(R.id.tv_nik_login);

        tvNama.setText(session.getNamaAsli());
        tvNik.setText(session.getNikAsli());
        tvJabatan.setText(session.getJabatan());
        tvNamaLogin.setText(session.getFullName());
        tvNikLogin.setText(session.getNik());
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}

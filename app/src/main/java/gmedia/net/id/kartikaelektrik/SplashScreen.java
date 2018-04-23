package gmedia.net.id.kartikaelektrik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreen extends AppCompatActivity {

    Intent intent;
    String email, ip;
    private int firstTaskTimer = 4 * 1000;
    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gmedia.net.id.kartikaelektrik.R.layout.splash_screen);
        getSupportActionBar().hide();

        Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        ivLogo = (ImageView) findViewById(R.id.iv_logo);
                        intent = new Intent(SplashScreen.this, LoginScreen.class);
                        SplashScreen.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreen.this, ivLogo, "splashimage");
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        });

                    }
                },
                2000 // 2 sec
        );

    }

}
package net.kibotu.android.bloodhound.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.kibotu.android.bloodhound.BloodHound;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init, probably better to do that in your application
        BloodHound.with(getApplication(), getString(R.string.google_analytics_tracking_id))
                .enableDryRun(BuildConfig.DEBUG)
                .enableLogging(BuildConfig.DEBUG);

        // tracking screen
        BloodHound.track("main_screen");

        // tracking events
        BloodHound.track("main_screen", "category", "action", "app_start");
    }
}

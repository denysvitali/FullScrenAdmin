package it.denv.fullscreenadmin;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothClass;
import android.content.ComponentName;
import android.content.Context;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private DevicePolicyManager dpm;
    private boolean kioskEnabled = false;
    private ComponentName deviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        Button resetOwner = (Button) findViewById(R.id.resetOwner);
        Button toggleKiosk = (Button) findViewById(R.id.kioskToggle);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(!dpm.isDeviceOwnerApp(getPackageName()))
        {
            TextView status = new TextView(this);
            status.setText("App is not device owner, set it with `dpm set-device-owner it.denv.fullscreenadmin/.AdmRcvr` by adb shell and restart the app");
            resetOwner.setVisibility(View.GONE);
            toggleKiosk.setVisibility(View.GONE);
            ll.addView(status);
            return;
        }

        deviceAdmin = new ComponentName(this, AdmRcvr.class);

        toggleKiosk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dpm.isDeviceOwnerApp(getPackageName()))
                {
                    toggleKioskMode();
                }
            }
        });

        resetOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dpm.isDeviceOwnerApp(getPackageName()))
                {
                    dpm.clearDeviceOwnerApp(getPackageName());
                }
            }
        });
    }

    public void enableKioskMode() {
        if (dpm.isDeviceOwnerApp(getPackageName())) {
            String[] packages = {getPackageName()};
            dpm.setLockTaskPackages(deviceAdmin, packages);
            try {
                startLockTask();
                kioskEnabled = true;
            } catch (Exception e) {
                Log.e("MainActivity", e.toString());
            }
        }
    }

    public void toggleKioskMode() {
        if (kioskEnabled) {
            disableKioskMode();
            return;
        }
        enableKioskMode();
    }

    public boolean isKioskEnabled() {
        return kioskEnabled;
    }

    public void disableKioskMode() {
        stopLockTask();
        kioskEnabled = false;
    }
}

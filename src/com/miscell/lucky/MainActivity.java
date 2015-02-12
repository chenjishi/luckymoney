package com.miscell.lucky;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class MainActivity extends Activity {
    private static final Intent sSettingsIntent =
            new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAccessibleEnabled()) {
            showEnableAccessibilityDialog();
        }
    }

    public void onSettingsClicked(View view) {
        startActivity(sSettingsIntent);
    }

    private boolean isAccessibleEnabled() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals("com.miscell.lucky/.MonitorService")) {
                return true;
            }
        }
        return false;
    }

    private void showEnableAccessibilityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置")
                .setMessage("您需要打开\"有红包\"的辅助功能选项才能抢微信红包")
                .setPositiveButton("去打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(sSettingsIntent);
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
}

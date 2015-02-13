package com.miscell.lucky;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {
    private static String STEP_1 = "1.请确认已<font color=\"#CC0000\">关闭</font>微信的<font color=\"#CC0000\">消息免打扰</font>功能，" +
            "以确保能收到微信群发的所有信息。另外，请<font color=\"#CC0000\">更新微信至最新版";
    private static String STEP_2 = "2.在\"<font color=\"#2F5676\">设置-辅助功能-服务</font>\"中点击“<font color=\"#CC0000\">有红包</font>”，并打开开关";
    private static final Intent sSettingsIntent =
            new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private ScrollView mTipsLayout;
    private FrameLayout mNormalLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTipsLayout = (ScrollView) findViewById(R.id.tips_layout);
        mNormalLayout = (FrameLayout) findViewById(R.id.normal_layout);

        ((TextView) findViewById(R.id.label_step1)).setText(Html.fromHtml(STEP_1));
        ((TextView) findViewById(R.id.label_step2)).setText(Html.fromHtml(STEP_2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAccessibleEnabled()) {
            mNormalLayout.setVisibility(View.GONE);
            mTipsLayout.setVisibility(View.VISIBLE);
            showEnableAccessibilityDialog();
        } else {
            mTipsLayout.setVisibility(View.GONE);
            mNormalLayout.setVisibility(View.VISIBLE);
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
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle("重要!").setMessage("您需要打开\"有红包\"的辅助功能选项才能抢微信红包")
                .setPositiveButton("打开", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(sSettingsIntent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null);
        dialog.show();
    }
}

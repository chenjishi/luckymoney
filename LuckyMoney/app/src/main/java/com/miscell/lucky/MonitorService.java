package com.miscell.lucky;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static android.view.accessibility.AccessibilityEvent.*;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK;

/**
 * Created by chenjishi on 15/2/12.
 */
public class MonitorService extends AccessibilityService {
    private static final int MSG_BACK = 233;

    private static final String UI_RECEIVE = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    private static final String UI_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";

    private boolean luckyClicked, hasLucky;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_BACK) {
                performGlobalAction(GLOBAL_ACTION_BACK);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }
                }, 1000);
            }
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        if (eventType == TYPE_NOTIFICATION_STATE_CHANGED) {
            unlockScreen();
            luckyClicked = false;
        }

        if (eventType == TYPE_WINDOW_CONTENT_CHANGED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (null == rootNode) return;

            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(getString(R.string.get_lucky));
            if (null == list || list.size() == 0) return;

            AccessibilityNodeInfo parent = list.get(list.size() - 1);
            while (null != parent) {
                if (parent.isClickable() && !luckyClicked) {
                    parent.performAction(ACTION_CLICK);
                    luckyClicked = true;
                    break;
                }
                parent = parent.getParent();
            }
        }

        if (eventType == TYPE_WINDOW_STATE_CHANGED) {
            String clazzName = event.getClassName().toString();
            if (clazzName.equals(UI_RECEIVE)) {
                traverseNode(event.getSource());
            }

            if (clazzName.equals(UI_DETAIL) && hasLucky) {
                hasLucky = false;
                handler.sendEmptyMessageDelayed(MSG_BACK, 1000);
            }
        }
    }

    private void traverseNode(AccessibilityNodeInfo node) {
        if (null == node) return;
        final int count = node.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo childNode = node.getChild(i);
                if (null != childNode && childNode.getClassName().equals("android.widget.Button") && childNode.isClickable()) {
                    childNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    hasLucky = true;
                }

                traverseNode(childNode);
            }
        }
    }

    private void unlockScreen() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
        keyguardLock.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");

        wakeLock.acquire();
    }

    @Override
    public void onInterrupt() {

    }
}

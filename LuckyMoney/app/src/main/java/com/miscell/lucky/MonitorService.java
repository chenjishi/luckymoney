package com.miscell.lucky;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static android.view.accessibility.AccessibilityEvent.*;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK;

/**
 * Created by chenjishi on 15/2/12.
 */
// TODO: 2017/1/24 remember add logic do not depend on notification notify to open lucky
public class MonitorService extends AccessibilityService {
    private boolean mLuckyClicked;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        if (eventType == TYPE_NOTIFICATION_STATE_CHANGED) {
            mLuckyClicked = false;
            unlockScreen();
        }

        if (eventType == TYPE_WINDOW_CONTENT_CHANGED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (null == rootNode) return;

            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(getString(R.string.get_lucky));
            if (null == list || list.size() == 0) return;

            AccessibilityNodeInfo node = list.get(list.size() - 1);

            if (node.isClickable()) {
                node.performAction(ACTION_CLICK);
            } else {
                AccessibilityNodeInfo parentNode = node;
                for (int i = 0; i < 5; i++) {
                    if (null != parentNode) {
                        parentNode = parentNode.getParent();
                        if (null != parentNode && parentNode.isClickable() && !mLuckyClicked) {
                            mLuckyClicked = true;
                            parentNode.performAction(ACTION_CLICK);
                        }
                    }
                }
            }
        }

        if (eventType == TYPE_WINDOW_STATE_CHANGED) {
            String clazzName = event.getClassName().toString();
            if (clazzName.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                AccessibilityNodeInfo nodeInfo = event.getSource();
                traverseNode(nodeInfo);
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

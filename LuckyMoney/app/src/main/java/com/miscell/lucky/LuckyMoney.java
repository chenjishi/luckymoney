package com.miscell.lucky;

import android.app.Application;
import com.flurry.android.FlurryAgent;

/**
 * Created by jishichen on 2017/1/22.
 */
public class LuckyMoney extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        new FlurryAgent.Builder().withLogEnabled(false)
                .build(this, "37T6NSWDQZ28FNJSWYBB");
    }
}

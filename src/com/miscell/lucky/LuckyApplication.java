package com.miscell.lucky;

import android.app.Application;
import com.flurry.android.FlurryAgent;

/**
 * Created by chenjishi on 15/2/12.
 */
public class LuckyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FlurryAgent.init(this, "37T6NSWDQZ28FNJSWYBB");
    }
}

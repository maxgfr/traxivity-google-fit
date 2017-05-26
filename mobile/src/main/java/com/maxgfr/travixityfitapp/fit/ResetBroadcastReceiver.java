package com.maxgfr.travixityfitapp.fit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

/**
 * Created by maxime on 26-May-17.
 */

public class ResetBroadcastReceiver extends BroadcastReceiver  {

    @Override
    public void onReceive(Context context, Intent intent) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().remove("THE_STEP_OF_CURRENT_DAY").commit();
    }
}

package com.maxgfr.travixityfitapp.fit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

/**
 * Created by maxime on 26-May-17.
 */

public class ResetBroadcastReceiver extends BroadcastReceiver  {

    private int nbStepCumulativeMidnight;

    public int getStepCumalativeMidnight () {return nbStepCumulativeMidnight;}

    public void setStepCumulativeMidnight (int n) { nbStepCumulativeMidnight = n;}

    /** Instance unique non préinitialisée */
    private static ResetBroadcastReceiver INSTANCE = null;

    /** Constructeur privé */
    private ResetBroadcastReceiver() {
        super();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized ResetBroadcastReceiver getInstance() {
        if (INSTANCE == null)
        { 	INSTANCE = new ResetBroadcastReceiver();
        }
        return INSTANCE;
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().remove("THE_STEP_OF_CURRENT_DAY").commit();
    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putInt("THE_STEP_AT_MIDNIGHT",getStepCumalativeMidnight()).apply();
    }
}

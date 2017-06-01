package com.maxgfr.travixityfitapp.fit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.maxgfr.travixityfitapp.MainActivity;

/**
 * Created by maxime on 26-May-17.
 */

public class ResetBroadcastReceiver extends BroadcastReceiver  {

    // ShareDailyStep
    private SharedPreferences sharedPrefStepCumulutative;

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

    public int readStepSaveMidnight (Context main) {
        sharedPrefStepCumulutative = PreferenceManager.getDefaultSharedPreferences(main);
        return sharedPrefStepCumulutative.getInt("STEP_CUMULUTATIVE",0);
    }

    public void saveStepSaveMidnight(Context main, int n) {
        sharedPrefStepCumulutative = PreferenceManager.getDefaultSharedPreferences(main);
        sharedPrefStepCumulutative.edit().putInt("STEP_CUMULUTATIVE",n).apply();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //aller dans ondatapoint pour prendre la valeur actuelle

        int n = readStepSaveMidnight(context);

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putInt("THE_STEP_AT_MIDNIGHT",n).apply();
    }
}

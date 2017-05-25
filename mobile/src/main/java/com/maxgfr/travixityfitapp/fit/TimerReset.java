package com.maxgfr.travixityfitapp.fit;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by maxime on 24-May-17.
 */

public class TimerReset {

    private static TimerReset INSTANCE = null;

    private Date dateLastReboot;

    private int stepOfCurrentDay;

    /** Constructeur privé */
    private TimerReset() {
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized TimerReset getInstance() {
        if (INSTANCE == null)
        { 	INSTANCE = new TimerReset();
        }
        return INSTANCE;
    }

    public void setDateLastReboot (Date d) {
        dateLastReboot = d;
    }

    public Date getDateLastReboot () {
        return dateLastReboot;
    }

    public void setStepOfCurrentDay (int d) {
        stepOfCurrentDay = d;
    }

    public int getStepOfCurrentDay () {
        return stepOfCurrentDay;
    }

    public boolean isSameDate () {

        Date date1 = new Date();
        Date date2 = getDateLastReboot();

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        if (!sameDay) {
            Log.e("isSameDate","Not the same date");
            return false;
        }

        return true;
    }

}

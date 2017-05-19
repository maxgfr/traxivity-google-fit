package com.maxgfr.travixityfitapp.fit;

import android.os.AsyncTask;

/**
 * Created by maxime on 19-May-17.
 */

public class ViewStepDataForDay extends AsyncTask<Void, Void, Void> {

    private HistoryService hist; // singleton

    protected Void doInBackground(Void... params) {
        hist = HistoryService.getInstance();
        hist.displayStepDataForToday();
        return null;
    }
}

package com.maxgfr.travixityfitapp.fit;

import java.util.ArrayList;

/**
 * Created by maxime on 16-May-17.
 */

public class FitLab {

    /** Instance unique non préinitialisée */
    private static FitLab INSTANCE = null;

    private ArrayList<String> mActivityRecognition;
    private ArrayList<String> mStepActivity;

    /** Constructeur privé */
    private FitLab() {
        mActivityRecognition = new ArrayList<>();
        mStepActivity = new ArrayList<>();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized FitLab getInstance() {
        if (INSTANCE == null)
        { 	INSTANCE = new FitLab();
        }
        return INSTANCE;
    }

    public void addActivityRecognition (String name) {
        mActivityRecognition.add(name);
    }

    public void addStepActivity (String name) {
        mStepActivity.add(name);
    }

    public ArrayList<String> getActivityRecognition() {
        return mActivityRecognition;
    }

    public ArrayList<String> getStepActivity() {
        return mStepActivity;
    }

}

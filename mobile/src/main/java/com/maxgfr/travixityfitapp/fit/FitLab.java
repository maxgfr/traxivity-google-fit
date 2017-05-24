package com.maxgfr.travixityfitapp.fit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by maxime on 16-May-17.
 */

public class FitLab {

    /** Instance unique non préinitialisée */
    private static FitLab INSTANCE = null;

    private ArrayList<String> mActivityRecognition;
    private ArrayList<String> mStepActivity;
    private ArrayList<String> mFitActivity;

    /** Constructeur privé */
    private FitLab() {
        mActivityRecognition = new ArrayList<>();
        mStepActivity = new ArrayList<>();
        mFitActivity = new ArrayList<>();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized FitLab getInstance() {
        if (INSTANCE == null)
        { 	INSTANCE = new FitLab();
        }
        return INSTANCE;
    }

    public void addActivityRecognition (String name) {
        if(!mActivityRecognition.contains(name))
            mActivityRecognition.add(name);
    }

    public void addStepActivity (String name) {
        if(!mStepActivity.contains(name))
            mStepActivity.add(name);
    }

    public void addFitActivity (String name) {
        if(!mFitActivity.contains(name))
            mFitActivity.add(name);
    }

    public ArrayList<String> getActivityRecognition() {
        return mActivityRecognition;
    }

    public ArrayList<String> getStepActivity() {
        return mStepActivity;
    }

    public ArrayList<String> getFitActivity() {
        return mFitActivity;
    }

}

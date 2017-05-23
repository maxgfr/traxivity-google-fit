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
        mActivityRecognition.add(name);
        deleteDoublon(mActivityRecognition);
    }

    public void addStepActivity (String name) {
        mStepActivity.add(name);
        deleteDoublon(mStepActivity);
    }

    public void addFitActivity (String name) {
        mFitActivity.add(name);
        deleteDoublon(mFitActivity);
    }

    public void deleteDoublon(ArrayList<String> list) {
        Set<String> hs = new HashSet<>();
        hs.addAll(list);
        list.clear();
        list.addAll(hs);
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

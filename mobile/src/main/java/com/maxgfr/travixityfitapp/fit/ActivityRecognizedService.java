package com.maxgfr.travixityfitapp.fit;

import android.app.IntentService;
import android.content.Intent;

import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Calendar;
import java.util.List;

/**
 * Created by maxime on 16-May-17.
 */

public class ActivityRecognizedService extends IntentService {
    

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {

        FitLab lab = FitLab.getInstance();

        for( DetectedActivity activity : probableActivities ) {

            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minutes = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);
            String date = hour + ":" + minutes+ ":" + sec+ " ";

            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE:
                {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        lab.addActivityRecognition(date+"You're in vehicle.");
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        lab.addActivityRecognition(date+"You're on bicycle.");
                    }
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        lab.addActivityRecognition(date+"You're walking or running.");
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        lab.addActivityRecognition(date+"You're running.");
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        lab.addActivityRecognition(date+"You'don't moove");
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        lab.addActivityRecognition(date+"The device angle relative to gravity changed significantly.");
                    }
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        lab.addActivityRecognition(date+"You're walking.");
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        lab.addActivityRecognition(date+"Unable to detect the current activity.");
                    }
                    break;
                }
            }
        }
    }
}
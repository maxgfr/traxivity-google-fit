package com.maxgfr.travixityfitapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.request.StartBleScanRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.location.ActivityRecognition;
import com.maxgfr.travixityfitapp.adapter.SectionsPagerAdapter;
import com.maxgfr.travixityfitapp.fit.ActivityRecognizedService;
import com.maxgfr.travixityfitapp.fit.FitLab;
import com.maxgfr.travixityfitapp.fit.HistoryService;
import com.maxgfr.travixityfitapp.fit.ResetBroadcastReceiver;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private static final int REQUEST_OAUTH = 1;

    private static final String AUTH_PENDING = "auth_state_pending";

    private static final int REQUEST_BLUETOOTH = 1001;

    private int nbStepSaveMidnight = 0;

    private boolean authInProgress = false;

    //Sensor Fitness Part
    private GoogleApiClient mApiClient;

    //Activity Recognition Part
    private GoogleApiClient mApiClient2;

    // ShareDailyStep
    private SharedPreferences sharedPrefStep;

    private HistoryService hist;

    private FitLab lab;

    private final ResultCallback mResultCallback = new ResultCallback() {
        @Override
        public void onResult(@NonNull Result result) {
            Status status = result.getStatus();
            if (!status.isSuccess()) {
                switch (status.getStatusCode()) {
                    case FitnessStatusCodes.DISABLED_BLUETOOTH:
                        try {
                            status.startResolutionForResult(
                                    MainActivity.this, REQUEST_BLUETOOTH);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        lab = FitLab.getInstance();

        hist = HistoryService.getInstance();

        hist.buildFitnessClientHistory(this);

        buildActivityRecognition();

        buildSensor();

        readStepSaveMidnight();

        resetCounter(this);

    }

    @Override //Sensor + Activity
    public void onConnected(Bundle bundle) {

        //startBleScan();

        //Sensor Fitness Part
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes( DataSource.TYPE_RAW )
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    lab.addStepActivity("Data Source type: "+dataSource.getDataType().getName());
                    lab.addStepActivity("Stream Identifier: "+dataSource.getStreamIdentifier());
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);

        //Activity Recognition Part
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient2, 3000, pendingIntent );
    }

    @Override //Sensor + Activity
    public void onConnectionSuspended(int i) {

    }

    @Override //Sensor + Ble
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_BLUETOOTH:
                startBleScan();
                Log.e( "onActivityResult", "REQUEST_BLUETOOTH" );
                break;
            case REQUEST_OAUTH:
                authInProgress = false;
                if( resultCode == RESULT_OK ) {
                    if( !mApiClient.isConnecting() && !mApiClient.isConnected() ) {
                        mApiClient.connect();
                    }
                } else if( resultCode == RESULT_CANCELED ) {
                    Log.e( "onActivityResult", "RESULT_CANCELED" );
                }
                break;
            default:
                Log.e("onActivityResult", "problem");
        }
    }

    @Override //Sensor + Activity
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }
    }

    @Override //Sensor Part
    public void onDataPoint(DataPoint dataPoint) {
        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
                    int nbStepOfDay = 0;
                    if (value.asInt()>nbStepSaveMidnight) {
                        nbStepOfDay = value.asInt() - nbStepSaveMidnight;
                    } else {
                        resetStepSaveMidnight();
                        nbStepOfDay = value.asInt();
                    }
                    ResetBroadcastReceiver r = ResetBroadcastReceiver.getInstance();
                    r.setStepCumulativeMidnight(nbStepOfDay);
                    lab.addStepActivity("Field: " + field.getName() + " Value: " + nbStepOfDay);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Sensor Fitness Part
        Fitness.SensorsApi.remove( mApiClient, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            mApiClient.disconnect();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Sensor Fitness Part
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    //ActivityRecognitionPart Part
    private void buildActivityRecognition () {
        mApiClient2 = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient2.connect();
    }

    //Sensor Fitness Part
    private void buildSensor() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.BLE_API)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();
    }

    //Sensor Fitness Part
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                .setSamplingRate( 3, TimeUnit.SECONDS )
                .build();

        Fitness.SensorsApi.add( mApiClient, request, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                    }
                });
    }

    //Ble Part
    private void startBleScan () {
        // 1. Define a callback object
        BleScanCallback callback = new BleScanCallback() {
            @Override
            public void onDeviceFound(BleDevice device) {
                // A device that provides the requested data types is available
                // -> Claim this BLE device (See next example)
                claimBleDevice(device);


            }
            @Override
            public void onScanStopped() {
                // The scan timed out or was interrupted
                Log.e( "startBleScan", "onScanStopped" );
            }
        };

        // 2. Create a scan request object:
        // - Specify the data types you're interested in
        // - Provide the callback object
        StartBleScanRequest request = new StartBleScanRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setBleScanCallback(callback)
                .build();

        // 3. Invoke the Bluetooth Low Energy API with:
        // - The Google API client
        // - The scan request
        PendingResult<Status> pendingResult =
                Fitness.BleApi.startBleScan(mApiClient, request);

        // 4. Check the result (see other examples)
        pendingResult.setResultCallback(mResultCallback);
    }

    //Ble Part
    private void claimBleDevice (BleDevice bleDevice) {
        // After the platform invokes your callback
        // with a compatible BLE device (bleDevice):

        // 1. Invoke the Bluetooth Low Energy API with:
        // - The Google API client
        // - The BleDevice object provided in the callback
        PendingResult<Status> pendingResult =
                Fitness.BleApi.claimBleDevice(mApiClient, bleDevice);

        // 2. Check the result (see other examples)
        pendingResult.setResultCallback(mResultCallback);
    }

    //Ble Part
    private void unclaimBleDevice (BleDevice bleDevice) {
        // When you no longer need the BLE device

        // 1. Invoke the Bluetooth Low Energy API with:
        // - The Google API client
        // - The BLE device (from the initial scan)
        PendingResult<Status> pendingResult =
                Fitness.BleApi.unclaimBleDevice(mApiClient, bleDevice);

        // 2. Check the result (see other examples)
        pendingResult.setResultCallback(mResultCallback);
    }

    private void readStepSaveMidnight () {
        sharedPrefStep = PreferenceManager.getDefaultSharedPreferences(this);
        nbStepSaveMidnight = sharedPrefStep.getInt("THE_STEP_AT_MIDNIGHT",0);
    }

    private void resetStepSaveMidnight() {
        sharedPrefStep = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefStep.edit().remove("THE_STEP_AT_MIDNIGHT").commit();
    }

    private void resetCounter(Context context) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, ResetBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*60*24, pi);
    }

}

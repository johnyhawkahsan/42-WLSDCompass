//https://www.wlsdevelop.com/index.php/en/blog?option=com_content&view=article&id=38
//NOTE: This is a great and an advanced tutorial on Compass app, because unlike previous videos, it shows how to create a compass app which also utilize
//RotationVector Sensor, and if this is not found, it uses Accelerometer and Magnetometer(MagneticField).
//It's working flawlessly and I'm thinking about improving it a little bit and uploading on PlayStore, Insha'Allah.

package com.ahsan.a42_wlsdcompass;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    ImageView compass_img;
    TextView txt_compass;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;//mRotationV means Rotation vector
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float mCurrentDegree = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compass_img = (ImageView) findViewById(R.id.img_compass);
        txt_compass = (TextView) findViewById(R.id.txt_azimuth);

        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //=================If Sensor.TYPE_ROTATION_VECTOR is available, then it's easy=================================//
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;//We round the Azimuth value to an integer number

        }


        //=================If Sensor.TYPE_ACCELEROMETER is available, read data and if Sensor.TYPE_MAGNETIC_FIELD read data======================//
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            System.arraycopy(event.values, 0, mLastAccelerometer, 0 , event.values.length);
            mLastAccelerometerSet = true;

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){

            System.arraycopy(event.values, 0, mLastMagnetometer, 0 , event.values.length);
            mLastMagnetometerSet = true;

        }





        //=================mLastMagnetometerSet && mLastAccelerometerSet both are true, then go this long route====================//
        if (mLastMagnetometerSet && mLastAccelerometerSet){

            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);

            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;//We round the Azimuth value to an integer number

        }

        mAzimuth = Math.round(mAzimuth);//Math.round Returns the closest int to the argument, with ties rounding to positive infinity.
        compass_img.setRotation(-mAzimuth);//Comment this line out if you want the below block to execute



/*
//==============================================New block from TechRepublic=====================================================================//
//TODO: NOTE: I used this code when above compass_image.setRotation wasn't working----Now above line is working better than this one.
//This code has a little glitch, when it goes to 0, the image swings back.
        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                -mAzimuth,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(250);
        ra.setFillAfter(true);
        compass_img.startAnimation(ra);
        mCurrentDegree = -mAzimuth;//I created this mCurrentDegree = 0f in Main activity and this technique is copied from TechRepublic tutorial
//==============================================//New block from TechRepublic=====================================================================//
*/



        String where = "NW";

        if (mAzimuth >= 350 || mAzimuth <= 10)
            where = "N";
        if (mAzimuth < 350 && mAzimuth > 280)
            where = "NW";
        if (mAzimuth <= 280 && mAzimuth > 260)
            where = "W";
        if (mAzimuth <= 260 && mAzimuth > 190)
            where = "SW";
        if (mAzimuth <= 190 && mAzimuth > 170)
            where = "S";
        if (mAzimuth <= 170 && mAzimuth > 100)
            where = "SE";
        if (mAzimuth <= 100 && mAzimuth > 80)
            where = "E";
        if (mAzimuth <= 80 && mAzimuth > 10)
            where = "NE";


        txt_compass.setText(mAzimuth + "° " + where);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




    //This is our main method that starts our sensors
    public void start(){

        //First of all we verify if the device supports the RotationVector (Compass + Gyroscope).
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){

            Log.i("Sensor", "(Sensor.TYPE_ROTATION_VECTOR) == null");

            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){

                Log.i("Sensor", "(Sensor.TYPE_MAGNETIC_FIELD) == null || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null");
                noSensorAlert();//If no sensor available

            } else {

                Log.i("Sensor", "(Sensor.TYPE_MAGNETIC_FIELD) || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) are available");
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);//Sensor_Delay_UI rates suitable for user interface
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }

        } else {

            Log.i("Sensor", "(Sensor.TYPE_ROTATION_VECTOR) is available");
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);

        }

    }


    public void noSensorAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device does not support the compass")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();//Close activity and close application
                    }
                });
    }



    //Unregister listeners
    public void stop(){
        if (haveSensor && haveSensor2){
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else if (haveSensor){
            mSensorManager.unregisterListener(this, mRotationV);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }
}






























package com.example.assignment5;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity implements SensorEventListener {
    private static final float ACCELERATION_THRESHOLD = 9 ;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor ,magnetometerSensor;
    private float previousX;
    private float previousY;
    private float previousZ;
    private boolean isStepDetected;
    private final float threshold = 130;
    private long lastStepTimeNs;
    float value;
    private int stepCount = 0;
    float[] gravity = new float[3];
    float[] magnetic = new float[3];
    private boolean isTakingStairs = false;
    private boolean isTakingLift = false;
    float zprevious;
    boolean inf;
    TextView directionTextView;
    TextView methodview , stairmethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        zprevious=0;
        inf = false;
        lastStepTimeNs = 0;
        directionTextView = (TextView)findViewById(R.id.textView4);
        methodview = (TextView)findViewById(R.id.textView6);
        methodview.setBackgroundColor(Color.RED);
        stairmethod = (TextView)findViewById(R.id.textView5);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        updateStepCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void updateStepCount() {
        TextView stepCountTextView = findViewById(R.id.step_count_text_view);
        Log.d("stepvalue", String.valueOf(stepCount));
        stepCountTextView.setText(String.valueOf(stepCount));
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {
            gravity = new float[3];
            System.arraycopy(event.values, 0, gravity, 0, 3);

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float magnitude =(x * x + y * y + z * z);

        // Check if acceleration change is greater than the threshold value and enough time has passed since last step
        long currentNs = System.nanoTime();
        if (magnitude > threshold && (currentNs - lastStepTimeNs) > TimeUnit.SECONDS.toNanos(1)) {
            stepCount++;
            lastStepTimeNs = currentNs;
            updateStepCount();
        }
    }
        else if(event.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)){
            magnetic = new float[3];
            System.arraycopy(event.values, 0, magnetic, 0, 3);
            float[] magneticField = event.values;
        }



        ;






        if (gravity != null && magnetic != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
            if( magnitude > 10.5){
//                stairmethod.setBackgroundColor(Color.GREEN);
                stairmethod.setBackgroundColor(Color.GREEN);
            }
            else{
                stairmethod.setBackgroundColor(Color.RED);
            }

            float diff = z - zprevious ;
            zprevious = z ;
            if((diff > 3 || diff < -3 ) && !inf){
                inf = true;
                methodview.setBackgroundColor(Color.GREEN);
            }
            else{
                inf = false;
                methodview.setBackgroundColor(Color.RED);
            }
            float rotationMatrix[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, gravity, magnetic);
            if (success) {

                float orientationMatrix[] = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientationMatrix);
                float rotationInRadians = orientationMatrix[0];
                float pitch = orientationMatrix[1];

                value = (float) Math.toDegrees(rotationInRadians)+360;
                Log.d("value" , String.valueOf((value%360)/45));
                String dire="";

                if(Math.round(value%360)/45 == 0){
                    dire="North";
                    directionTextView.setText("North");
                }
                else if(Math.round(value%360)/45 == 1){
                    dire="NorthEast";
                    directionTextView.setText("NorthEast");
                }
                else if(Math.round(value%360)/45 == 2){
                    dire="East";
                    directionTextView.setText("East");
                }
                else if(Math.round(value%360)/45 == 3){
                    dire="SouthEast";
                    directionTextView.setText("SouthEast");
                }
                else if(Math.round(value%360)/45 == 4){
                    dire="South";
                    directionTextView.setText("South");
                }
                else if(Math.round(value%360)/45 == 5){
                    dire="SouthWest";
                    directionTextView.setText("SouthWest");
                }
                else if(Math.round(value%360)/45 == 6){
                    dire="West";
                    directionTextView.setText("West");
                }
                else if(Math.round(value%360)/45 == 7){
                    dire="NorthWest";
                    directionTextView.setText("NorthWest");
                }
                else{
                    dire="North";
                    directionTextView.setText("North");
                }



                gravity = null;
                magnetic = null;
            }
        }

}

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

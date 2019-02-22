package smin.com.sensorlist;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class FlashLightActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SensorEventListener listener;
    private Sensor linearSensor;
    private TextView textInfo;
    private CameraManager cameraManager;
    private String flashLightID;
    private boolean torchStatus = false;
    private long torchTimer;
    private long oldTorchTimer;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_light);

        textInfo = findViewById(R.id.textTorch);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try {
            flashLightID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (linearSensor == null){
            Toast.makeText(this, "Pas d'accelerometre", Toast.LENGTH_SHORT).show();
        }
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                torchTimer = System.currentTimeMillis();
                float[] values = sensorEvent.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];
                float xCarre = x * x;
                float yCarre = y * y;
                float zCarre = z * z;
                double acceleration =(xCarre+yCarre+zCarre);
                acceleration = Math.sqrt(acceleration);
                if(acceleration > 2){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    SwitchLight();
                }

            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener,linearSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void SwitchLight(){
        torchTimer = System.currentTimeMillis();
        if((torchTimer-oldTorchTimer) > 1000) {
            if (torchStatus) {
                try {
                    cameraManager.setTorchMode(flashLightID, false);
                    torchStatus = false;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    cameraManager.setTorchMode(flashLightID, true);
                    torchStatus = true;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            oldTorchTimer = System.currentTimeMillis();
        }

    }
}

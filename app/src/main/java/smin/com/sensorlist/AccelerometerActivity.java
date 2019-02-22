package smin.com.sensorlist;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccelerometerActivity extends AppCompatActivity {

    private TextView indicator;
    private SensorManager sensorManager;
    private SensorEventListener listener;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        indicator = findViewById(R.id.indicator);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null){
            Toast.makeText(this, "Pas d'accelerometre", Toast.LENGTH_SHORT).show();
        }
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] values = sensorEvent.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];
                float xCarre = x * x;
                float yCarre = y * y;
                float zCarre = z * z;
                float acceleration =(xCarre+yCarre+zCarre);
                acceleration = (float)Math.sqrt(acceleration);
                //indicator.setText("x "+x +" y "+y+" z "+z);

                if(acceleration < 10){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }
                if(acceleration>10 && acceleration<12){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }
                if(acceleration > 12){
                    getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                }

                if(x>3){
                    // gauche
                    indicator.setText("gauche");
                }
                else if(x<-3){
                    //droite
                    indicator.setText("droite");

                }
                else if(y<-3){
                    //haut
                    indicator.setText("haut");
                }
                else if(y>3){
                    //bas
                    indicator.setText("bas");
                }
                else{
                    indicator.setText("");
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
        sensorManager.registerListener(listener,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }
}

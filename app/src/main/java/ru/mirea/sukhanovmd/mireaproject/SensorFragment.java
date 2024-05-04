package ru.mirea.sukhanovmd.mireaproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SensorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor humiditySensor;

    private TextView textViewHumidity;
    private TextView textViewDescription;
    private ImageView infoImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);
        textViewHumidity = rootView.findViewById(R.id.textViewHumidity);
        textViewDescription = rootView.findViewById(R.id.textViewDescription);
        infoImage = rootView.findViewById(R.id.infoImage);

        sensorManager = (SensorManager) requireActivity().getSystemService(requireActivity().SENSOR_SERVICE);

        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (humiditySensor == null) {
            textViewHumidity.setText("Датчик влажности не поддерживается");
        } else {
            infoImage.setVisibility(View.VISIBLE);
            textViewHumidity.setVisibility(View.GONE);
            textViewDescription.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (humiditySensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    //данные датчика обновились
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            float humidityValue = event.values[0];
            textViewHumidity.setVisibility(View.VISIBLE);
            textViewDescription.setVisibility(View.VISIBLE);
            //infoImage.setVisibility(View.GONE);
            textViewHumidity.setText("Влажность: " + humidityValue + "%");
            String description = getHumidityDescription(humidityValue);
            textViewDescription.setText("Описание: " + description);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private String getHumidityDescription(float humidity) {
        if (humidity < 30) {
            return "Сухой воздух, который может вызывать пересыхание кожи";
        } else if (humidity < 50) {
            return "Комфортный уровень влажности, приятный для большинства людей";
        } else if (humidity < 70) {
            return "Оптимальная влажность для здоровья и комфортного пребывания в помещении";
        } else if (humidity < 90) {
            return "Влажный воздух, способствующий развитию плесени и грибковых инфекций";
        } else {
            return "Экстремально влажный воздух, опасный для здоровья и вызывающий дискомфорт";
        }
    }
}

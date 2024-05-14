package ru.mirea.sukhanovmd.mireaproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapFragment extends Fragment {

    private MapView mapView = null;
    private boolean gotLocation = false;
    private MyLocationNewOverlay locationNewOverlay;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().load(getContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext()));
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        TextView nameTextView = view.findViewById(R.id.text_name);
        TextView typeTextView = view.findViewById(R.id.text_type);
        TextView addressTextView = view.findViewById(R.id.text_address);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(55.795097, 37.704174);
        mapController.setCenter(startPoint);

        checkLocationPermission();

        CompassOverlay compassOverlay = new CompassOverlay(requireContext(), new
                InternalCompassOrientationProvider(requireContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        final Context context = requireActivity().getApplicationContext();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);

        addMarkers(view);

        return view;
    }

    private void checkLocationPermission() {
        int allowLocation = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (allowLocation == PackageManager.PERMISSION_GRANTED) {
            gotLocation = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        if (gotLocation) {
            loadMyLocationOverlay();
        }
    }

    private void loadMyLocationOverlay() {
        locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(locationNewOverlay);
    }

    private void addMarkers(View view) {
        Marker marker1 = new Marker(mapView);
        marker1.setPosition(new GeoPoint(55.795864, 37.700765));
        marker1.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                TextView nameTextView = view.findViewById(R.id.text_name);
                TextView typeTextView = view.findViewById(R.id.text_type);
                TextView addressTextView = view.findViewById(R.id.text_address);

                nameTextView.setText("Перекресток");
                typeTextView.setText("Тип: Магазин");
                addressTextView.setText("Адрес: ул. Стромынка, 25, стр. 1");
                selectMarkerIcon("1");

                return true;
            }
        });
        mapView.getOverlays().add(marker1);
        marker1.setIcon(requireContext().getResources().getDrawable(R.drawable.pin1, null));
        marker1.setTitle("1");


        Marker marker2 = new Marker(mapView);
        marker2.setPosition(new GeoPoint(55.794971, 37.711438));
        marker2.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                TextView nameTextView = view.findViewById(R.id.text_name);
                TextView typeTextView = view.findViewById(R.id.text_type);
                TextView addressTextView = view.findViewById(R.id.text_address);

                nameTextView.setText("Бургер кинг");
                typeTextView.setText("Тип: Фастфуд");
                addressTextView.setText("Адрес: Преображенская площадь, 6");
                selectMarkerIcon("2");

                return true;
            }
        });
        mapView.getOverlays().add(marker2);
        marker2.setIcon(requireContext().getResources().getDrawable(R.drawable.pin2, null));
        marker2.setTitle("2");


        Marker marker3 = new Marker(mapView);
        marker3.setPosition(new GeoPoint(55.795525, 37.705560));
        marker3.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                TextView nameTextView = view.findViewById(R.id.text_name);
                TextView typeTextView = view.findViewById(R.id.text_type);
                TextView addressTextView = view.findViewById(R.id.text_address);

                nameTextView.setText("Тануки");
                typeTextView.setText("Тип: Ресторан");
                addressTextView.setText("Адрес: Преображенская улица, 5/7");
                selectMarkerIcon("3");

                return true;
            }
        });
        mapView.getOverlays().add(marker3);
        marker3.setIcon(requireContext().getResources().getDrawable(R.drawable.pin3, null));
        marker3.setTitle("3");
    }

    private void selectMarkerIcon(String markerTitle) {
        for (Overlay overlay : mapView.getOverlays()) {
            if (overlay instanceof Marker) {
                Marker marker = (Marker) overlay;
                int pinDrawableId;
                if (marker.getTitle().equals(markerTitle)) {
                    pinDrawableId = getResources().getIdentifier("pin" + marker.getTitle() + "_s", "drawable", requireContext().getPackageName());
                } else {
                    pinDrawableId = getResources().getIdentifier("pin" + marker.getTitle(), "drawable", requireContext().getPackageName());
                }
                marker.setIcon(requireContext().getResources().getDrawable(pinDrawableId, null));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(getContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext()));
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(getContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext()));
        if (mapView != null) {
            mapView.onPause();
        }
    }
}
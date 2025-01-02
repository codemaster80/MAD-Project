package org.dieschnittstelle.mobile.android.skeleton;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskDetailViewModel;

public class TaskLocationViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    protected static final String LOCATION_VIEW_KEY = "locationViewObject";
    private static final int ACCESS_FINE_LOCATION_CODE = 100;
    private TaskDetailViewModel viewModel;
    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_location_map_view);
        viewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        task = (Task) getIntent().getSerializableExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY);
        if (task == null) {
            task = new Task();
        }

        getLocationInformation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.task_location_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (task.getLocation() == null || task.getLocation().getLatlng() == null) {
            LatLng defaultLatLng = new LatLng(viewModel.getDefaultLatLng().getLat(), viewModel.getDefaultLatLng().getLng());
            googleMap.addMarker(new MarkerOptions().position(defaultLatLng).title("Your location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng));
        } else {
            LatLng selectedLatLng= new LatLng(
                    task.getLocation().getLatlng().getLat(),
                    task.getLocation().getLatlng().getLng()
            );
            googleMap.addMarker(
                    new MarkerOptions().position(selectedLatLng).title(task.getLocation().getName())
            );
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLatLng));
        }
    }

    private void getLocationInformation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (getApplicationContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        } else {
            String provider = locationManager.getAllProviders().get(0);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                viewModel.setupDefaultLatLng(location.getLatitude(), location.getLongitude());
            }
        }
    }
}

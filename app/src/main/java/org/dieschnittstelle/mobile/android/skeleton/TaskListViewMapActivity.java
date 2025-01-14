package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskListViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskListViewMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public static final String TASK_LIST_VIEW_MAP_KEY = "taskListViewMapObject";
    private TaskListViewModel viewModel;
    private List<Task> tasks;
    private GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_view_map);
        viewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        tasks = (List<Task>) getIntent().getSerializableExtra(TASK_LIST_VIEW_MAP_KEY);
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        showTasksOnMap();
    }

    public void showTasksOnMap() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (Task t : tasks) {
            if(t.getLocation() != null && t.getLocation().getLatlng() != null) {
                Task.LatLng location = t.getLocation().getLatlng();
                LatLng coordinate = new LatLng(location.getLat(), location.getLng());
                // Create bound that include locations on the map
                boundsBuilder.include(coordinate);
                // Add marker to the map
                addMarker(coordinate, t.getName());
            }
        }
        // Move camera to show all markers and locations
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 20));
    }

    private void addMarker(LatLng coordinates, String title) {
        map.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(title));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }
}

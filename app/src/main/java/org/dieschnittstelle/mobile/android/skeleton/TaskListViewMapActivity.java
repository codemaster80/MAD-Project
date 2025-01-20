package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskListViewModel;

import java.util.List;

public class TaskListViewMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static List<Task> tasks;
    private GoogleMap map;
    private TaskListViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_view_map);
        viewModel = new ViewModelProvider(this).get(TaskListViewModel.class);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.task_list_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (tasks == null) {
            tasks = viewModel.getTaskList();
        }
        map = googleMap;
        showTasksOnMap();
    }

    private void showTasksOnMap() {
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
}

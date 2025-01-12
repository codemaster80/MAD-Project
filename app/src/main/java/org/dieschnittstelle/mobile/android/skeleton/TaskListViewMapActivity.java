package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class TaskListViewMapActivity extends Fragment {

    public static final String TASK_LIST_VIEW_MAP_KEY = "taskListViewMapObject";
    private TaskListViewModel viewmodel;
    private final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private final LatLng DARWIN = new LatLng(-12.459501, 130.839915);
    private GoogleMap map;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            addMarker(BRISBANE, "Test1");
            addMarker(MELBOURNE, "Test2");
            showAustralia();

//            List<Task> tasks = viewmodel.getTaskList();

//            Log.i("Location", String.valueOf(tasks.size()));
//            for (Task t : tasks) {
//                Task.LatLng location = t.getLocation().getLatlng();
//                Log.i("Location", location.getLat() + " " + t.getName());
//                LatLng locationLatLng = new LatLng(location.getLat(), location.getLng());
//                googleMap.addMarker(new MarkerOptions().position(locationLatLng).title(t.getName()));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
//            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i("Location", "onCreateView");
        return inflater.inflate(R.layout.activity_task_list_view_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

     /**
     * Move the camera to show all of Australia.
     * Construct a {@link com.google.android.gms.maps.model.LatLngBounds} from markers positions,
     * then move the camera.
     */
    public void showAustralia() {
        // Wait until map is ready
        if (map == null) {
            return;
        }

        // Create bounds that include all locations of the map
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder()
                .include(PERTH)
                .include(ADELAIDE)
                .include(MELBOURNE)
                .include(SYDNEY)
                .include(DARWIN)
                .include(BRISBANE);

        // Move camera to show all markers and locations
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 20));
    }

    private void addMarker(LatLng coordinates, String title) {
        map.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(title));
    }
}
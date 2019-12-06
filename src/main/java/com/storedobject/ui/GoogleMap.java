package com.storedobject.ui;

import com.storedobject.common.Geolocation;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.TemplateModel;

import java.util.stream.Stream;

public class GoogleMap extends PolymerTemplate<GoogleMap.Model> {

    public GoogleMap() {
        this(null);
    }

    public GoogleMap(Geolocation geolocation) {
    }

    public interface Model extends TemplateModel {
        void setApiKey(String apiKey);
        void setApiId(String apiId);
        void setMapId(String mapId);
        void setMapSize(String mapSize);
        void setLatitude(double latitude);
        double getLatitude();
        void setLongitude(double longitude);
        double getLongitude();
        void setFitToMarkers(boolean fitToMarkers);
        boolean isFitToMarkers();
    }

    public static void setAPIKey(String apiKey) {
    }

    public void setWidth(int width) {
    }

    public int getWidth() {
        return 0;
    }

    public void setHeight(int height) {
    }

    public int getHeight() {
        return 0;
    }

    public void setCenter(Geolocation geolocation) {
    }

    public Geolocation getCenter() {
        return null;
    }

    public void setFitToMarkers(boolean fitToMarkers) {
    }

    public boolean isFitToMarkers() {
        return false;
    }

    public Registration addMarkerClickedListener(MarkerClickedListener clickedListener) {
        return null;
    }

    public Registration addMarkerPositionedListener(MarkerPositionedListener positionedListener) {
        return null;
    }

    public void clearMarkers() {
    }

    public Stream<Marker> listMarkers() {
        return null;
    }

    public class Marker {

        public Marker() {
            this(null);
        }

        public Marker(Geolocation geolocation) {
            this(geolocation, null);
        }

        public Marker(boolean draggable) {
            this(null, draggable, null);
        }

        public Marker(Geolocation geolocation, boolean draggable) {
            this(geolocation, draggable, null);
        }

        public Marker(Geolocation geolocation, String title) {
            this(geolocation, false, title);
        }

        public Marker(boolean draggable, String title) {
            this(null, draggable, title);
        }

        public Marker(Geolocation geolocation, boolean draggable, String title) {
        }

        public void setVisible(boolean visible) {
        }

        public boolean isVisible() {
            return true;
        }

        public void setLocation(Geolocation location) {
        }

        public Geolocation getLocation() {
            return null;
        }

        public String getTitle() {
            return null;
        }

        public void setTitle(String title) {
        }

        public void setDraggable(boolean draggable) {
        }

        public boolean isDraggable() {
            return true;
        }

        public void delete() {
        }

        public GoogleMap getMap() {
            return GoogleMap.this;
        }
    }

    public interface MarkerClickedListener {
        void markerClicked(Marker maker);
    }

    public interface MarkerPositionedListener {
        void markerPositioned(Marker maker);
    }
}
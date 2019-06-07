package com.storedobject.ui;

import com.storedobject.common.Geolocation;
import com.storedobject.vaadin.HasSize;
import com.vaadin.flow.component.*;
import com.vaadin.flow.shared.Registration;

public class GoogleMap extends Component implements HasSize {

    public GoogleMap() {
        this(null);
    }

    public GoogleMap(Geolocation geolocation) {
    }

    public static void setAPIKey(String apiKey) {
    }

    public void add(Marker... markers) {
    }

    public void remove(Marker... markers) {
    }

    public void setLocation(Geolocation geolocation) {
    }

    public Geolocation getLocation() {
        return null;
    }

    public void fitToMarkers(boolean fit) {
    }

    public static class Marker {

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

        public void setDraggable(boolean draggable) {
        }

        public boolean isDraggable() {
            return false;
        }

        public void setTitle(String title) {
        }

        public String getTitle() {
            return null;
        }

        public void setVisible(boolean visible) {
        }

        public void setLocation(Geolocation geolocation) {
        }

        public Geolocation getLocation() {
            return null;
        }

        public GoogleMap getMap() {
            return null;
        }

        public Registration addClickedListener(ComponentEventListener<MarkerClickedEvent> listener) {
            return null;
        }

        public Registration addDraggedListener(ComponentEventListener<MarkerDraggedEvent> listener) {
            return null;
        }
    }

    public static abstract class MarkerEvent extends ComponentEvent<Component> {

        public MarkerEvent(Component source, boolean fromClient, double latitude, double longitude) {
            super(source, fromClient);
        }

        public Marker getMarker() {
            return null;
        }
    }

    @DomEvent("google-map-marker-click")
    public static class MarkerClickedEvent extends MarkerEvent {

        public MarkerClickedEvent(Component source, boolean fromClient, @EventData("element.latitude") double latitude, @EventData("element.longitude") double longitude) {
            super(source, fromClient, latitude, longitude);
        }
    }

    @DomEvent("google-map-marker-dragend")
    public static class MarkerDraggedEvent extends MarkerEvent {

        public MarkerDraggedEvent(Component source, boolean fromClient, @EventData("element.latitude") double latitude, @EventData("element.longitude") double longitude) {
            super(source, fromClient, latitude, longitude);
        }
    }
}
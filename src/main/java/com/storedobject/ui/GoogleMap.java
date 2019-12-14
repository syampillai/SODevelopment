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
        void setZoom(double zoom);
        double getZoom();
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

    public void setZoom(int zoom) {
    }

    public int getZoom() {
        return 0;
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

    public Registration addPolylinePositionedListener(PolylinePositionedListener positionedListener) {
        return null;
    }

    public Registration addPolygonPositionedListener(PolygonPositionedListener positionedListener) {
        return null;
    }

    public Registration addCirclePositionedListener(CirclePositionedListener positionedListener) {
        return null;
    }

    public void clearMarkers() {
    }

    public Stream<Marker> listMarkers() {
        return null;
    }

    public void clearPolylines() {
    }

    public Stream<Polyline> listPolylines() {
        return null;
    }

    public void clearPolygons() {
    }

    public Stream<Polygon> listPolygons() {
        return null;
    }

    public void clearCirlces() {
    }

    public Stream<Circle> listCircles() {
        return null;
    }

    public void clearShapes() {
    }

    abstract class Shape {

        public Shape(boolean draggable) {
        }

        public final void setVisible(boolean visible) {
        }

        public final boolean isVisible() {
            return false;
        }

        public final void setDraggable(boolean draggable) {
        }

        public final boolean isDraggable() {
            return false;
        }

        public final void delete() {
        }

        public final GoogleMap getMap() {
            return GoogleMap.this;
        }
    }

    public class Marker extends Shape {

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
            super(draggable);
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

        public void setIcon(String iconName) {
        }

        public void setIconURL(String iconURL) {
        }

        public String getIconURL() {
            return null;
        }
    }

    public class Circle extends Shape {

        public Circle() {
            this(null, 1000, false);
        }

        public Circle(boolean draggable) {
            this(null, 0, draggable);
        }

        public Circle(Geolocation center) {
            this(center, 0, false);
        }

        public Circle(Geolocation center, double radius, boolean draggable) {
            super(draggable);
        }

        public double getRadius() {
            return 0;
        }

        public void setRadius(double radius) {
        }

        public Geolocation getCenter() {
            return null;
        }

        public void setCenter(Geolocation center) {
        }

        public String getLineColor() {
            return null;
        }

        public void setLineColor(String lineColor) {
        }

        public double getLineOpacity() {
            return 0;
        }

        public void setLineOpacity(double lineOpacity) {
        }

        public int getLineThickness() {
            return 0;
        }

        public void setLineThickness(int lineThickness) {
        }

        public String getFillColor() {
            return null;
        }

        public void setFillColor(String fillColor) {
        }

        public double getFillOpacity() {
            return 0;
        }

        public void setFillOpacity(double fillOpacity) {
        }
    }

    public class Polyline extends Shape {

        public Polyline() {
            super(false);
        }

        public Polyline(boolean draggable) {
            super(draggable);
        }

        public void add(Iterable<Geolocation> points) {
        }

        public void add(Geolocation... points) {
        }

        public void remove(Geolocation... points) {
        }

        public void removeAll() {
        }

        public String getLineColor() {
            return null;
        }

        public void setLineColor(String lineColor) {
        }

        public double getLineOpacity() {
            return 0;
        }

        public void setLineOpacity(double lineOpacity) {
        }

        public int getLineThickness() {
            return 0;
        }

        public void setLineThickness(int lineThickness) {
        }
    }

    public class Polygon extends Polyline {

        public Polygon() {
            this(false);
        }

        public Polygon(boolean draggable) {
            super(draggable);
        }

        public String getFillColor() {
            return null;
        }

        public void setFillColor(String fillColor) {
        }

        public double getFillOpacity() {
            return 0;
        }

        public void setFillOpacity(double fillOpacity) {
        }
    }

    public interface MarkerClickedListener {
        void markerClicked(Marker maker);
    }

    public interface MarkerPositionedListener {
        void markerPositioned(Marker maker);
    }

    public interface PolylinePositionedListener {
        void polylinePositioned(Polyline polyline);
    }

    public interface PolygonPositionedListener {
        void polygonPositioned(Polygon polygon);
    }

    public interface CirclePositionedListener {
        void circlePositioned(Circle circle);
    }
}
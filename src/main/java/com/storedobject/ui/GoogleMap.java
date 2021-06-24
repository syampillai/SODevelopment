package com.storedobject.ui;

import com.storedobject.common.Geolocation;
import com.storedobject.common.MathUtility;
import com.storedobject.core.GlobalProperty;
import com.storedobject.helper.ID;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.TemplateModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag("google-map")
@JsModule("./so/google-map/google-map.js")
public class GoogleMap extends PolymerTemplate<GoogleMap.Model> {

    private static String apiKey = GlobalProperty.get("Google Map API Key");
    private int width = 600, height = 400;
    private final List<String> commands = new ArrayList<>();
    private boolean triggered = false;
    private boolean attached = false;
    private int shapeCount = 0;
    private final Map<Integer, Shape> shapes = new HashMap<>();
    private List<MarkerClickedListener> markerClickedListeners;
    private List<MarkerPositionedListener> markerPositionedListeners;
    private List<PolylinePositionedListener> polylinePositionedListeners;
    private List<PolygonPositionedListener> polygonPositionedListeners;
    private List<CirclePositionedListener> circlePositionedListeners;

    public GoogleMap() {
        this(null);
    }

    public GoogleMap(Geolocation geolocation) {
        ID.set(this);
        if(apiKey != null && !apiKey.isEmpty()) {
            getModel().setApiKey(apiKey);
        }
        getModel().setApiId("api" + ID.newID());
        getModel().setMapId("map" + ID.newID());
        setSize();
        setCenter(geolocation);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        attached = false;
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
        GoogleMap.apiKey = apiKey;
    }

    public void setWidth(int width) {
        if(width < 50) {
            width = 50;
        }
        this.width = width;
        setSize();
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        if(height < 50) {
            height = 50;
        }
        this.height = height;
        setSize();
    }

    public int getHeight() {
        return height;
    }

    private void setSize() {
        getModel().setMapSize("width:" + width + "px;height:" + height + "px");
        getElement().getStyle().set("width", width + "px");
        getElement().getStyle().set("height", height + "px");
    }

    public void setCenter(Geolocation geolocation) {
        if(geolocation != null) {
            getModel().setLatitude(geolocation.getLatitudeDegree());
            getModel().setLongitude(geolocation.getLongitudeDegree());
            trigger("C");
        }
    }

    public Geolocation getCenter() {
        return new Geolocation(getModel().getLatitude(), getModel().getLongitude());
    }

    public void setZoom(int zoom) {
        getModel().setZoom(zoom);
    }

    public int getZoom() {
        return (int)(getModel().getZoom() + 0.5);
    }

    public void setFitToMarkers(boolean fitToMarkers) {
        getModel().setFitToMarkers(fitToMarkers);
    }

    public boolean isFitToMarkers() {
        return getModel().isFitToMarkers();
    }

    public Registration addMarkerClickedListener(MarkerClickedListener clickedListener) {
        if(markerClickedListeners == null) {
            markerClickedListeners = new ArrayList<>();
        }
        markerClickedListeners.add(clickedListener);
        return () -> markerClickedListeners.remove(clickedListener);
    }

    public Registration addMarkerPositionedListener(MarkerPositionedListener positionedListener) {
        if(markerPositionedListeners == null) {
            markerPositionedListeners = new ArrayList<>();
        }
        markerPositionedListeners.add(positionedListener);
        return () -> markerPositionedListeners.remove(positionedListener);
    }

    public Registration addPolylinePositionedListener(PolylinePositionedListener positionedListener) {
        if(polylinePositionedListeners == null) {
            polylinePositionedListeners = new ArrayList<>();
        }
        polylinePositionedListeners.add(positionedListener);
        return () -> polylinePositionedListeners.remove(positionedListener);
    }

    public Registration addPolygonPositionedListener(PolygonPositionedListener positionedListener) {
        if(polygonPositionedListeners == null) {
            polygonPositionedListeners = new ArrayList<>();
        }
        polygonPositionedListeners.add(positionedListener);
        return () -> polygonPositionedListeners.remove(positionedListener);
    }

    public Registration addCirclePositionedListener(CirclePositionedListener positionedListener) {
        if(circlePositionedListeners == null) {
            circlePositionedListeners = new ArrayList<>();
        }
        circlePositionedListeners.add(positionedListener);
        return () -> circlePositionedListeners.remove(positionedListener);
    }

    private void addCommand(String command) {
        synchronized (commands) {
            if(commands.isEmpty() || !commands.get(commands.size() - 1).equals(command)) {
                commands.add(command);
            }
        }
    }

    private void trigger(String command) {
        if(!attached) {
            synchronized (commands) {
                if(!attached) {
                    addCommand(command);
                    return;
                }
            }
        }
        if(triggered) {
            synchronized (commands) {
                if(triggered) {
                    addCommand(command);
                    return;
                }
            }
        }
        triggered = true;
        if(commands.isEmpty()) {
            command(command);
        } else {
            addCommand(command);
            command(commands.remove(0));
        }
    }

    @ClientCallable
    private void ready() {
        attached = true;
        removeUnwanted();
        commandDone();
        shapes.entrySet().stream().filter(e -> e.getValue().isVisible()).forEach(e -> trigger(e.getValue().symbol() + ":a" + e.getKey()));
    }

    private void removeUnwanted() {
        boolean found = true;
        Shape shape;
        while (found) {
            found = false;
            for(Integer sid: shapes.keySet()) {
                shape = shapes.get(sid);
                if(shape.id == 0) {
                    found = true;
                    shapes.remove(sid);
                    break;
                }
            }
        }
    }

    @ClientCallable
    private void commandDone() {
        synchronized (commands) {
            triggered = !commands.isEmpty();
            if(triggered) {
                command(commands.remove(0));
            }
        }
    }

    @ClientCallable
    private void markerClicked(int id) {
        Marker m = (Marker) shapes.get(id);
        if(m != null) {
            if(markerClickedListeners != null) {
                markerClickedListeners.forEach(c -> c.markerClicked(m));
            }
        }
    }

    @ClientCallable
    private void markerPositioned(int id, double latitude, double longitude) {
        Marker m = (Marker) shapes.get(id);
        if(m != null) {
            m.location.set(latitude, longitude);
            if(markerPositionedListeners != null) {
                markerPositionedListeners.forEach(p -> p.markerPositioned(m));
            }
        }
    }

    @ClientCallable
    private void polyPositioned(int id, double latitude, double longitude) {
        Polyline m = (Polyline) shapes.get(id);
        if(m != null) {
            Geolocation first = m.points.get(0);
            latitude = latitude - first.getLatitudeDegree();
            longitude = longitude - first.getLongitudeDegree();
            for(Geolocation p: m.points) {
                p.set(p.getLatitudeDegree() + latitude, p.getLongitudeDegree() + longitude);
            }
            if(m instanceof Polygon) {
                if(polygonPositionedListeners != null) {
                    Polygon polygon = (Polygon) m;
                    polygonPositionedListeners.forEach(p -> p.polygonPositioned(polygon));
                }
            } else {
                if(polylinePositionedListeners != null) {
                    polylinePositionedListeners.forEach(p -> p.polylinePositioned(m));
                }
            }
        }
    }

    @ClientCallable
    private void circlePositioned(int id, double latitude, double longitude) {
        Circle m = (Circle) shapes.get(id);
        if(m != null) {
            m.center.set(latitude, longitude);
            if(circlePositionedListeners != null) {
                circlePositionedListeners.forEach(p -> p.circlePositioned(m));
            }
        }
    }

    private void command(String command) {
        if(command.length() > 1 && command.charAt(1) == ':') {
            char shapeCode = command.charAt(0);
            if(command.length() == 2) {
                command = "clear('" + shapeCode + "'";
            } else {
                command = command.substring(2);
                int id = Integer.parseInt(command.substring(1));
                switch(command.charAt(0)) {
                    case 'a' -> {
                        Shape shape = shapes.get(id);
                        String param = null;
                        if(shape != null) {
                            param = shape.toCommand();
                        }
                        if(param != null) {
                            command = "add" + shape.name() + "(" + id + "," + param;
                        } else {
                            commandDone();
                            return;
                        }
                    }
                    case 'd' -> command = "remove(" + id;
                    case 'h' -> command = "hide(" + id;
                    case 's' -> command = "show(" + id;
                }
            }
        } else {
            command = "command('" + command + "'";
        }
        UI ui = getUI().orElse(null);
        if(ui == null) {
            ui = UI.getCurrent();
        }
        ui.getPage().executeJs("document.getElementById('" + getId().orElse(null) + "')." + command + ");");
    }

    public void clearMarkers() {
        clearShapes('m', Marker.class);
    }

    public Stream<Marker> listMarkers() {
        return listShapes(Marker.class);
    }

    public void clearPolylines() {
        clearShapes('l', Polyline.class);
    }

    public Stream<Polyline> listPolylines() {
        return listShapes(Polyline.class);
    }

    public void clearPolygons() {
        clearShapes('p', Polygon.class);
    }

    public Stream<Polygon> listPolygons() {
        return listShapes(Polygon.class);
    }

    public void clearCirlces() {
        clearShapes('c', Circle.class);
    }

    public Stream<Circle> listCircles() {
        return listShapes(Circle.class);
    }

    public void clearShapes() {
        clearMarkers();
        clearPolygons();
        clearPolylines();
        clearCirlces();
    }

    private <S extends Shape> void clearShapes(char shapeCode, Class<S> shapeClass) {
        trigger(shapeCode + ":");
        shapes.values().stream().filter(s -> shapeClass.isAssignableFrom(s.getClass())).forEach(m -> m.id = 0);
        removeUnwanted();
    }

    private <S extends Shape> Stream<S> listShapes(Class<S> shapeClass) {
        //noinspection unchecked
        return shapes.values().stream().filter(s -> shapeClass.isAssignableFrom(s.getClass())).map(s -> (S)s);
    }

    abstract class Shape {

        int id;
        private boolean visible = true;
        private boolean draggable;

        public Shape(boolean draggable) {
            this.draggable = draggable;
            synchronized (shapes) {
                shapeCount++;
                id = shapeCount;
                shapes.put(id, this);
            }
        }

        abstract String toCommand();

        abstract String name();

        abstract char symbol();

        public final void setVisible(boolean visible) {
            if(id > 0 && visible != this.visible) {
                this.visible = visible;
                trigger(symbol() + ":" + (visible ? "s" : "h") + id);
            }
        }

        public final boolean isVisible() {
            return visible;
        }

        void changed() {
            trigger(symbol() + ":a" + id);
        }

        public final void setDraggable(boolean draggable) {
            if(id > 0 && draggable != this.draggable) {
                this.draggable = draggable;
                changed();
            }
        }

        public final boolean isDraggable() {
            return draggable;
        }

        public final void delete() {
            if(id > 0) {
                trigger(symbol() + ":d" + id);
                shapes.remove(id);
                id = 0;
            }
        }

        public final GoogleMap getMap() {
            return GoogleMap.this;
        }
    }

    public class Marker extends Shape {

        private Geolocation location;
        private String title;
        private String iconURL = "https://maps.google.com/mapfiles/ms/micons/pink-dot.png";

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
            this.location = geolocation == null ? new Geolocation(0.0033446,76.3497818) : new Geolocation(geolocation);
            this.title = title;
            changed();
        }

        String toCommand() {
            return location.getLatitudeDegree() + "," + location.getLongitudeDegree() + "," + isDraggable() + "," + title() + ",'" + iconURL + "'";
        }

        @Override
        String name() {
            return "Marker";
        }

        @Override
        char symbol() {
            return 'm';
        }

        private String title() {
            if(title == null) {
                return "null";
            }
            return "'" + title.replace("\"", "\\\"").replace("'", "\\'") + "'";
        }

        public void setLocation(Geolocation location) {
            if(id > 0 && location != null && !this.location.equals(location)) {
                this.location = new Geolocation(location);
                changed();
            }
        }

        public Geolocation getLocation() {
            return location;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            if(id > 0 && !Objects.equals(title, this.title)) {
                this.title = title;
                changed();
            }
        }

        public void setIcon(String iconName) {
            setIconURL(iconName);
        }

        public void setIconURL(String iconURL) {
            if(id == 0 || invalidURL(iconURL)) {
                return;
            }
            if(!iconURL.contains("/")) {
                iconURL = "https://maps.google.com/mapfiles/ms/micons/" + iconURL;
            }
            if(iconURL.startsWith("http://")) {
                iconURL = iconURL.replace("http://", "https://");
            }
            if(!iconURL.startsWith("https://")) {
                iconURL = "https://" + iconURL;
            }
            if(!iconURL.endsWith(".png")) {
                iconURL += ".png";
            }
            if(this.iconURL.equals(iconURL)) {
                return;
            }
            this.iconURL = iconURL;
            changed();
        }

        public String getIconURL() {
            return iconURL;
        }

        private boolean invalidURL(String u) {
            if(u == null || u.isEmpty()) {
                return true;
            }
            u = u.replace("://", "");
            if(u.contains("//") || u.contains("..")) {
                return true;
            }
            char c;
            for(int i = 0; i < u.length(); i++) {
                c = u.charAt(i);
                switch (c) {
                    case '.':
                    case '-':
                    case '/':
                        continue;
                }
                if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                    continue;
                }
                return true;
            }
            return false;
        }
    }

    public class Circle extends Shape {

        private Geolocation center;
        private double radius;
        private String lineColor = "#000000";
        private double lineOpacity = 0.8;
        private int lineThickness = 2;
        private String fillColor = null;
        private double fillOpacity = 0.35;

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
            this.center = center == null ? GoogleMap.this.getCenter() : new Geolocation(center);
            this.radius = radius <= 0 ? 1000 : radius;
            changed();
        }

        @Override
        String toCommand() {
            if(center == null) {
                return null;
            }
            return center.getLatitudeDegree() + "," + center.getLongitudeDegree() + "," + radius + "," +
                    isDraggable() + ",'" + lineColor + "'," + lineOpacity + "," + lineThickness + "," + fc() + "," + fillOpacity;
        }

        private String fc() {
            if(fillColor == null) {
                return "null";
            }
            return "'" + fillColor + "'";
        }

        @Override
        String name() {
            return "Circle";
        }

        @Override
        char symbol() {
            return 'c';
        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            if(id == 0 || radius < 0 || MathUtility.equals(radius, this.radius)) {
                return;
            }
            this.radius = radius;
            changed();
        }

        public Geolocation getCenter() {
            return new Geolocation(center);
        }

        public void setCenter(Geolocation center) {
            if(id == 0) {
                return;
            }
            if(center == null) {
                center = GoogleMap.this.getCenter();
            }
            if(center == null || center.equals(this.center)) {
                return;
            }
            this.center = center;
            changed();
        }

        public String getLineColor() {
            return lineColor;
        }

        public void setLineColor(String lineColor) {
            if(id == 0) {
                return;
            }
            lineColor = color(lineColor);
            if(lineColor == null || this.lineColor.equals(lineColor)) {
                return;
            }
            this.lineColor = lineColor;
            changed();
        }

        public double getLineOpacity() {
            return lineOpacity;
        }

        public void setLineOpacity(double lineOpacity) {
            if(id == 0 || lineOpacity < 0 || lineOpacity > 1 || MathUtility.equals(this.lineOpacity, lineOpacity)) {
                return;
            }
            this.lineOpacity = lineOpacity;
            changed();
        }

        public int getLineThickness() {
            return lineThickness;
        }

        public void setLineThickness(int lineThickness) {
            if(id == 0 || lineThickness < 1 || lineThickness > 10 || lineThickness == this.lineThickness) {
                return;
            }
            this.lineThickness = lineThickness;
            changed();
        }

        public String getFillColor() {
            return fillColor;
        }

        public void setFillColor(String fillColor) {
            if(id == 0) {
                return;
            }
            fillColor = color(fillColor);
            if(fillColor == null || fillColor.equals(this.fillColor)) {
                return;
            }
            this.fillColor = fillColor;
            changed();
        }

        public double getFillOpacity() {
            return fillOpacity;
        }

        public void setFillOpacity(double fillOpacity) {
            if(id == 0 || fillOpacity < 0 || fillOpacity > 1 || MathUtility.equals(this.fillOpacity, fillOpacity)) {
                return;
            }
            this.fillOpacity = fillOpacity;
            changed();
        }
    }

    public class Polyline extends Shape {

        List<Geolocation> points = new ArrayList<>();
        private String lineColor = "#000000";
        private double lineOpacity = 0.8;
        private int lineThickness = 2;

        public Polyline() {
            super(false);
        }

        public Polyline(boolean draggable) {
            super(draggable);
        }

        @Override
        String toCommand() {
            if(points.size() < 2) {
                return null;
            }
            return "[" + points() + "]," + isDraggable() + ",'" + lineColor + "'," + lineOpacity + "," + lineThickness;
        }

        @Override
        String name() {
            return "Polyline";
        }

        @Override
        char symbol() {
            return 'l';
        }

        String points() {
            return points.stream().map(p -> "{lat:" + p.getLatitudeDegree() + ",lng:" + p.getLongitudeDegree() + "}").collect(Collectors.joining(","));
        }

        public void add(Iterable<Geolocation> points) {
            if(id == 0 || points == null) {
                return;
            }
            points.forEach(p -> this.points.add(new Geolocation(p)));
            changed();
        }

        public void add(Geolocation... points) {
            if(id == 0 || points == null || points.length == 0) {
                return;
            }
            for(Geolocation p: points) {
                this.points.add(new Geolocation(p));
            }
            changed();
        }

        public void remove(Geolocation... points) {
            if(id == 0 || points == null || points.length == 0) {
                return;
            }
            for(Geolocation p: points) {
                this.points.remove(p);
            }
            changed();
        }

        public void removeAll() {
            if(id == 0 || points.isEmpty()) {
                return;
            }
            points.clear();
            changed();
        }

        public String getLineColor() {
            return lineColor;
        }

        public void setLineColor(String lineColor) {
            if(id == 0) {
                return;
            }
            lineColor = color(lineColor);
            if(lineColor == null || this.lineColor.equals(lineColor)) {
                return;
            }
            this.lineColor = lineColor;
            changed();
        }

        public double getLineOpacity() {
            return lineOpacity;
        }

        public void setLineOpacity(double lineOpacity) {
            if(id == 0 || lineOpacity < 0 || lineOpacity > 1 || MathUtility.equals(this.lineOpacity, lineOpacity)) {
                return;
            }
            this.lineOpacity = lineOpacity;
            changed();
        }

        public int getLineThickness() {
            return lineThickness;
        }

        public void setLineThickness(int lineThickness) {
            if(id == 0 || lineThickness < 1 || lineThickness > 10 || lineThickness == this.lineThickness) {
                return;
            }
            this.lineThickness = lineThickness;
            changed();
        }
    }

    public class Polygon extends Polyline {

        private String fillColor = null;
        private double fillOpacity = 0.35;

        public Polygon() {
            this(false);
        }

        public Polygon(boolean draggable) {
            super(draggable);
        }

        @Override
        String toCommand() {
            String c = super.toCommand();
            if(c == null) {
                return null;
            }
            return c + "," + fc() + "," + fillOpacity;
        }

        @Override
        String name() {
            return "Polygon";
        }

        @Override
        char symbol() {
            return 'p';
        }

        private String fc() {
            if(fillColor == null) {
                return "null";
            }
            return "'" + fillColor + "'";
        }

        public String getFillColor() {
            return fillColor;
        }

        public void setFillColor(String fillColor) {
            if(id == 0) {
                return;
            }
            fillColor = color(fillColor);
            if(fillColor == null || fillColor.equals(this.fillColor)) {
                return;
            }
            this.fillColor = fillColor;
            changed();
        }

        public double getFillOpacity() {
            return fillOpacity;
        }

        public void setFillOpacity(double fillOpacity) {
            if(id == 0 || fillOpacity < 0 || fillOpacity > 1 || MathUtility.equals(this.fillOpacity, fillOpacity)) {
                return;
            }
            this.fillOpacity = fillOpacity;
            changed();
        }
    }

    private static String color(String color) {
        if(color == null || color.isEmpty()) {
            return null;
        }
        if(color.startsWith("#")) {
            color = color.substring(1);
        }
        color = color.toUpperCase();
        char c;
        for(int i = 0; i < color.length(); i++) {
            c = color.charAt(i);
            if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F')) {
                continue;
            }
            return null;
        }
        StringBuilder colorBuilder = new StringBuilder(color);
        while (colorBuilder.length() < 6) {
            colorBuilder.insert(0, "0");
        }
        color = colorBuilder.toString();
        if(color.length() > 6) {
            color = color.substring(0, 6);
        }
        return "#" + color;
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
package com.storedobject.ui;

import com.storedobject.common.Geolocation;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;

public class GeolocationField extends CustomTextField<Geolocation> {

    private static final Geolocation EMPTY = new Geolocation(37.77493, -122.4194);
    private MapView mapView;

    public GeolocationField() {
        this(null, null);
    }

    public GeolocationField(String label) {
        this(label, null);
    }

    public GeolocationField(Geolocation geolocation) {
        this(null, geolocation);
    }

    public GeolocationField(String label, Geolocation geolocation) {
        super(EMPTY);
        setLabel(label);
        setValue(geolocation);
        setPresentationValue(getValue());
        TextField f = (TextField)getField();
        f.setWidth("19em");
        f.setSuffixComponent(new ImageButton(VaadinIcon.GLOBE, c -> GoogleMapClick()));
    }

    @Override
    public void setValue(Geolocation value) {
        if(value == null) {
            value = EMPTY;
        }
        super.setValue(new Geolocation(value));
    }

    @Override
    public Geolocation getValue() {
        return new Geolocation(super.getValue());
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        if(mapView != null) {
            mapView.setCaption(label);
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    protected Geolocation getModelValue(String string) {
        return new Geolocation(string);
    }

    private void GoogleMapClick() {
        if(mapView == null) {
            mapView = new MapView();
        }
        mapView.execute();
    }

    private class MapView extends View {

        private static final String clickMessage = "Click on the anchor to set new location";
        private final GoogleMap.Marker marker;
        private final ELabel message = new ELabel();

        private MapView() {
            setCaption(getLabel());
            ButtonLayout b = new ButtonLayout(new ImageButton("Close", VaadinIcon.CLOSE_SMALL, e -> abort()), message);
            GoogleMap map = new GoogleMap();
            map.setFitToMarkers(true);
            marker = map.new Marker(true);
            Div div = new Div(b, map);
            map.setWidth(800);
            map.setHeight(600);
            setComponent(new Window(div));
            map.addMarkerClickedListener(m -> {
                if(!GeolocationField.this.isReadOnly() && GeolocationField.this.isEnabled()) {
                    Geolocation value = marker.getLocation();
                    GeolocationField.this.setValue(value);
                    GeolocationField.this.setPresentationValue(value);
                    close();
                }
            });
        }

        @Override
        public void setCaption(String caption) {
            super.setCaption((caption == null || caption.isEmpty()) ? "Select Location" : caption);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            message.clear();
            if(!GeolocationField.this.isReadOnly() && GeolocationField.this.isEnabled()) {
                message.append(clickMessage, Application.COLOR_SUCCESS);
            }
            message.update();
            super.execute(parent, doNotLock);
            marker.setLocation(GeolocationField.this.getValue());
        }
    }
}
package com.storedobject.ui;

import com.flowingcode.vaadin.addons.googlemaps.GoogleMap;
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker;
import com.flowingcode.vaadin.addons.googlemaps.LatLon;
import com.storedobject.common.Geolocation;
import com.storedobject.core.APIToken;
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
            APIToken token = APIToken.get("Google-Map", Application.get().getTransactionManager().getUser());
            if(token == null) {
                Application.warning("No access configured for Google Map");
                return;
            }
            mapView = new MapView(getLabel(), token.getToken());
        }
        mapView.save.setVisible(!isReadOnly());
        mapView.marker.setDraggable(!isReadOnly());
        mapView.setValue(getValue());
        mapView.execute();
    }

    private class MapView extends View {

        private final GoogleMap map;
        private final Button save = new Button("Save", e -> saveValue());
        private final GoogleMapMarker marker = new GoogleMapMarker();
        private final Geolocation value = new Geolocation();

        private MapView(String label, String key) {
            map = new GoogleMap(key, null, null);
            setCaption(label);
            ButtonLayout b = new ButtonLayout(save, new Button("Close", e -> close()));
            Div div = new Div(b, map);
            setComponent(div);
            map.setWidth("80vw");
            map.setHeight("80vh");
            map.addMarker(marker);
            setWindowMode(true);
            marker.addDragEndEventListener(e -> {
                value.setLongitudeDegree(e.getLongitude());
                value.setLatitudeDegree(e.getLatitude());
            });
        }

        @Override
        public void setCaption(String caption) {
            super.setCaption(caption);
            if(marker != null) {
                marker.setCaption(caption);
            }
        }

        void setValue(Geolocation value) {
            this.value.set(value);
            LatLon pos = new LatLon(value.getLatitudeDegree(), value.getLongitudeDegree());
            marker.setPosition(pos);
            map.setCenter(pos);
        }

        private void saveValue() {
            GeolocationField.this.setValue(this.value);
            close();
        }
    }
}
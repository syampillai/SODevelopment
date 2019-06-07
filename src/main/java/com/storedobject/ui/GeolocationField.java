package com.storedobject.ui;

import com.storedobject.common.Geolocation;
import com.storedobject.vaadin.CustomTextField;

public class GeolocationField extends CustomTextField<Geolocation> {

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
        super(null);
    }

    @Override
    protected Geolocation getModelValue(String string) {
        return new Geolocation(string);
    }
}
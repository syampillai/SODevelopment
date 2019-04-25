package com.storedobject.core.converter;

import com.storedobject.common.Geolocation;

public class GeolocationValueConverter extends ValueConverter<Geolocation> {
	
	private static GeolocationValueConverter instance;

	private GeolocationValueConverter() {
	}
	
	public static GeolocationValueConverter get() {
		return null;
	}

	@Override
	public Class<Geolocation> getValueType() {
		return Geolocation.class;
	}

	@Override
	public Geolocation convert(Object value) {
		return null;
	}
}

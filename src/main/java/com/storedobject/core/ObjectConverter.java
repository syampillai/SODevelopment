package com.storedobject.core;

import java.util.function.Function;

@FunctionalInterface
public interface ObjectConverter<FROM extends StoredObject, TO extends StoredObject> {
	
	public TO convert(FROM object);

	public default Function<FROM, TO> function() {
		return object -> convert(object);
	}
}

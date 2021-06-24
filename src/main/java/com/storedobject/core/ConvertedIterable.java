package com.storedobject.core;

import java.util.Iterator;
import java.util.function.Function;

public class ConvertedIterable<FROM, TO> implements Iterable<TO> {
	
	public ConvertedIterable(Iterable<FROM> original, Function<FROM, TO> converter) {
	}

	@Override
	public Iterator<TO> iterator() {
		return null;
	}
}
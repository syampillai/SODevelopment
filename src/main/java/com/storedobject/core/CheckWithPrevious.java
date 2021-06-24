package com.storedobject.core;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class CheckWithPrevious<T> implements Predicate<T> {
	
	public CheckWithPrevious(BiPredicate<? super T, ? super T> checker) {
	}
	
	public CheckWithPrevious(T initial, BiPredicate<? super T, ? super T> checker) {
	}
	
	@Override
	public boolean test(T current) {
		return false;
	}
	
	public void setPrevious(T previous) {
	}
	
	public T getPrevious() {
		return null;
	}
}

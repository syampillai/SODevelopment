package com.storedobject.core;

import java.util.Comparator;
import java.util.function.Predicate;

public class FileBuffer {
	
	public void begin() {
	}
	
	public void end() {
	}

	public void write(byte[] data) throws Exception {
	}
	
	public void close() {
	}
	
	public int size() {
		return 0;
	}
	
	public byte[] read(int index) {
		return null;
	}
	
	public void swap(int firstIndex, int secondIndex) {
	}
	
	public FileBuffer sort(Comparator<byte[]> comparator) {
		return null;
	}
	
	public FileBuffer filter(Predicate<byte[]> filter) {
		return null;
	}
}

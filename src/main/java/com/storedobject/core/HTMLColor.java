package com.storedobject.core;

import java.awt.Color;


/**
 * Definition of colors - application-wide
 */
public class HTMLColor {

	public static String text = "#0F0A06";
	public static String error = "#DA3926";
	public static String warning = "#EE43B9";
	public static String hilite = "#5903B1";
	public static String status = "#B23CFF";
	public static String info = "#FF0066";
	
	public static void set(Object colors) {
	}
	
	public static Color create(String color) {
		return null;
	}
	
	public static interface Definition {
		
		public default String getTextColor() {
			return null;
		}
		
		public default String getErrorColor() {
			return null;
		}
		
		public default String getWarningColor() {
			return null;
		}
		
		public default String getHiliteColor() {
			return null;
		}
		
		public default String getStatusColor() {
			return null;
		}
		
		public default String getInfoColor() {
			return null;
		}
	}
}

package com.storedobject.core;

public class ReportColumnDefinition extends StoredObject implements Detail {

    public ReportColumnDefinition() {
    }

    public static void columns(Columns columns) {
    }

    public void setAttribute(String attribute) {
    }

    public String getAttribute() {
        return "";
    }

    public void setCaption(String caption) {
    }

    public String getCaption() {
        return "";
    }

    public void setRelativeWidth(int relativeWidth) {
    }

    public int getRelativeWidth() {
        return 0;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
    }

    public int getHorizontalAlignment() {
        return 0;
    }

    public static String[] getHorizontalAlignmentValues() {
        return new String[] {};
    }

    public static String getHorizontalAlignmentValue(int value) {
        return "";
    }

    public String getHorizontalAlignmentValue() {
        return "";
    }

    public void setVerticalAlignment(int verticalAlignment) {
    }

    public int getVerticalAlignment() {
        return 0;
    }

    public static String[] getVerticalAlignmentValues() {
        return new String[] {};
    }

    public static String getVerticalAlignmentValue(int value) {
        return "";
    }

    public String getVerticalAlignmentValue() {
        return "";
    }

    public void setAggregate(int aggregate) {
    }

    public int getAggregate() {
        return 0;
    }

    public static String[] getAggregateValues() {
        return new String[] {};
    }

    public static String getAggregateValue(int value) {
        return "";
    }

    public String getAggregateValue() {
        return "";
    }

    public void setFooter(String footer) {
    }

    public String getFooter() {
        return "";
    }

    public void setFooterAlignment(int footerAlignment) {
    }

    public int getFooterAlignment() {
        return 0;
    }

    public static String[] getFooterAlignmentValues() {
        return new String[] {};
    }

    public static String getFooterAlignmentValue(int value) {
        return "";
    }

    public String getFooterAlignmentValue() {
        return "";
    }

    public void setDisplayOrder(int displayOrder) {
    }

    public int getDisplayOrder() {
        return 0;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return ReportDefinition.class.isAssignableFrom(masterClass);
    }
}

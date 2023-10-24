package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.util.function.Function;

public class ReportColumnDefinition extends StoredObject implements Detail {

    private static final String[] horizontalAlignmentValues =
            new String[] {
                    "Auto", "Left", "Center", "Right",
            };
    private static final String[] verticalAlignmentValues =
            new String[] {
                    "Top", "Center", "Bottom",
            };
    private static final String[] aggregateValues =
            new String[] {
                    "None", "Sum.", "Average", "Count",
            };
    private static final String[] footerAlignmentValues =
            new String[] {
                    "Left", "Center", "Right",
            };
    private String attribute;
    private String caption;
    private int relativeWidth = 0;
    private int horizontalAlignment = 0;
    private int verticalAlignment = 1;
    private int aggregate = 0;
    private String footer = "";
    private int footerAlignment = 0;
    private int displayOrder;
    Function<StoredObject, ?> value;

    public ReportColumnDefinition() {
    }

    public static void columns(Columns columns) {
        columns.add("Attribute", "text");
        columns.add("Caption", "text");
        columns.add("RelativeWidth", "int");
        columns.add("HorizontalAlignment", "int");
        columns.add("VerticalAlignment", "int");
        columns.add("Aggregate", "int");
        columns.add("Footer", "text");
        columns.add("FooterAlignment", "int");
        columns.add("DisplayOrder", "int");
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Column(order = 100)
    public String getAttribute() {
        return attribute;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Column(style = "(large)", required = false, order = 200)
    public String getCaption() {
        return caption;
    }

    public void setRelativeWidth(int relativeWidth) {
        this.relativeWidth = relativeWidth;
    }

    @Column(order = 300, required = false)
    public int getRelativeWidth() {
        return relativeWidth;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    @Column(order = 400)
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public static String[] getHorizontalAlignmentValues() {
        return horizontalAlignmentValues;
    }

    public static String getHorizontalAlignmentValue(int value) {
        String[] s = getHorizontalAlignmentValues();
        return s[value % s.length];
    }

    public String getHorizontalAlignmentValue() {
        return getHorizontalAlignmentValue(horizontalAlignment);
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    @Column(order = 500)
    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    public static String[] getVerticalAlignmentValues() {
        return verticalAlignmentValues;
    }

    public static String getVerticalAlignmentValue(int value) {
        String[] s = getVerticalAlignmentValues();
        return s[value % s.length];
    }

    public String getVerticalAlignmentValue() {
        return getVerticalAlignmentValue(verticalAlignment);
    }

    public void setAggregate(int aggregate) {
        this.aggregate = aggregate;
    }

    @Column(order = 600)
    public int getAggregate() {
        return aggregate;
    }

    public static String[] getAggregateValues() {
        return aggregateValues;
    }

    public static String getAggregateValue(int value) {
        String[] s = getAggregateValues();
        return s[value % s.length];
    }

    public String getAggregateValue() {
        return getAggregateValue(aggregate);
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    @Column(required = false, order = 700)
    public String getFooter() {
        return footer;
    }

    public void setFooterAlignment(int footerAlignment) {
        this.footerAlignment = footerAlignment;
    }

    @Column(order = 800)
    public int getFooterAlignment() {
        return footerAlignment;
    }

    public static String[] getFooterAlignmentValues() {
        return footerAlignmentValues;
    }

    public static String getFooterAlignmentValue(int value) {
        String[] s = getFooterAlignmentValues();
        return s[value % s.length];
    }

    public String getFooterAlignmentValue() {
        return getFooterAlignmentValue(footerAlignment);
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Column(order = 900, required = false)
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(attribute)) {
            throw new Invalid_Value("Attribute");
        }
        super.validateData(tm);
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return ReportDefinition.class.isAssignableFrom(masterClass);
    }

    public Function<StoredObject, ?> getValue() {
        return value;
    }
}
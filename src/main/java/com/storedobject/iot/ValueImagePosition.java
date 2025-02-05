package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

public final class ValueImagePosition extends StoredObject implements Detail {

    private static final String[] ordinalityValues = Unit.getOrdinalityValues();
    private static final String[] layoutStyleValues = Unit.getOrdinalityValues();
    private int ordinality, imageX, imageY, layoutStyle = 0;
    private String label, tooltip;

    public ValueImagePosition() {
    }

    public static void columns(Columns columns) {
        columns.add("Label", "text");
        columns.add("Tooltip", "text");
        columns.add("Ordinality", "int");
        columns.add("ImageX", "int");
        columns.add("ImageY", "int");
        columns.add("LayoutStyle", "int");
    }

    public void setOrdinality(int ordinality) {
        this.ordinality = ordinality;
    }

    @Column(order = 100)
    public int getOrdinality() {
        return ordinality;
    }

    public String getOrdinalityValue() {
        return getOrdinalityValue(ordinality);
    }

    public static String getOrdinalityValue(int ordinality) {
        return ordinalityValues[ordinality % ordinalityValues.length];
    }

    public static String[] getOrdinalityValues() {
        return ordinalityValues;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(order = 200, required = false)
    public String getLabel() {
        return label;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Column(order = 300, required = false)
    public String getTooltip() {
        return tooltip;
    }

    public void setImageX(int imageX) {
        this.imageX = imageX;
    }

    @Column(order = 400, required = false)
    public int getImageX() {
        return imageX;
    }

    public void setImageY(int imageY) {
        this.imageY = imageY;
    }

    @Column(order = 500, required = false)
    public int getImageY() {
        return imageY;
    }


    public void setLayoutStyle(int layoutStyle) {
        this.layoutStyle = layoutStyle;
    }

    @Column(order = 600)
    public int getLayoutStyle() {
        return layoutStyle;
    }

    public String getLayoutStyleValue() {
        return getLayoutStyleValue(layoutStyle);
    }

    public static String getLayoutStyleValue(int layoutStyle) {
        return layoutStyleValues[layoutStyle % layoutStyleValues.length];
    }

    public static String[] getLayoutStyleValues() {
        return layoutStyleValues;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (ordinality < 0 || ordinality >= ordinalityValues.length) {
            throw new Invalid_Value("Ordinality");
        }
        if (layoutStyle < 0 || layoutStyle >= layoutStyleValues.length) {
            throw new Invalid_Value("Layout Style");
        }
        if(ordinality == 0 && layoutStyle != 0) {
            throw new Invalid_Value("Ordinality / Layout Style");
        }
        super.validateData(tm);
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return ValueDefinition.class.isAssignableFrom(masterClass);
    }

    @Override
    public Object getUniqueValue() {
        return ordinality + "/" + layoutStyle;
    }
}

package com.storedobject.iot;

import com.storedobject.common.StyledBuilder;
import com.storedobject.core.Columns;
import com.storedobject.core.Indices;
import com.storedobject.core.ObjectHint;
import com.storedobject.core.StoredObject;
import com.storedobject.core.annotation.Column;

public final class ValueStyle extends StoredObject {

    private static ValueStyle instance;
    private static final String DEFAULT_TEXT = "font-weight:bold\nfont-size:small";
    private static final String DEFAULT_3 = "color:#FFFFFF\nbackground:#FF3333";
    private static final String DEFAULT_2 = "color:#FFFFFF\nbackground:#FF9933";
    private static final String DEFAULT_1 = "color:#333333\nbackground:#FFCC33";
    private static final String DEFAULT_0 = "color:#FFFFFF\nbackground:#66CC66";
    private String style = DEFAULT_TEXT, styleLabel = "color:#FFFFFF\nbackground:#333333", styleOff = DEFAULT_3,
            styleOn = DEFAULT_0, styleLowest = DEFAULT_3, styleLower = DEFAULT_2, styleLow = DEFAULT_1,
            styleNormal = DEFAULT_0, styleHigh = DEFAULT_1, styleHigher = DEFAULT_2, styleHighest = DEFAULT_3;

    public ValueStyle() {
    }

    public static void columns(Columns columns) {
        columns.add("Style", "text");
        columns.add("StyleLabel", "text");
        columns.add("StyleOff", "text");
        columns.add("StyleOn", "text");
        columns.add("StyleLowest", "text");
        columns.add("StyleLower", "text");
        columns.add("StyleLow", "text");
        columns.add("StyleNormal", "text");
        columns.add("StyleHigh", "text");
        columns.add("StyleHigher", "text");
        columns.add("StyleHighest", "text");
    }

    public static void indices(Indices indices) {
        indices.add("T_Family", true);
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST | ObjectHint.SMALL;
    }

    public void setStyle(String style) {
        this.style = style.trim();
    }

    @Column(order = 1400, required = false, style = "(large)")
    public String getStyle() {
        return style.isBlank() ? DEFAULT_TEXT : style;
    }

    public void setStyleLabel(String style) {
        this.styleLabel = style.trim();
    }

    @Column(order = 1500, required = false, style = "(large)")
    public String getStyleLabel() {
        return styleLabel.isBlank() ? DEFAULT_TEXT : styleLabel;
    }

    public void setStyleOff(String style) {
        this.styleOff = style.trim();
    }

    @Column(order = 1600, required = false, style = "(large)")
    public String getStyleOff() {
        return styleOff.isBlank() ? DEFAULT_3 : styleOff;
    }

    public void setStyleOn(String style) {
        this.styleOn = style.trim();
    }

    @Column(order = 1700, required = false, style = "(large)")
    public String getStyleOn() {
        return styleOn.isBlank() ? DEFAULT_0 : styleOn;
    }

    public void setStyleLowest(String style) {
        this.styleLowest = style.trim();
    }

    @Column(order = 1800, required = false, style = "(large)")
    public String getStyleLowest() {
        return styleLowest.isBlank() ? DEFAULT_3 : styleLowest;
    }

    public void setStyleLower(String style) {
        this.styleLower = style.trim();
    }

    @Column(order = 1900, required = false, style = "(large)")
    public String getStyleLower() {
        return styleLower.isBlank() ? DEFAULT_2 : styleLower;
    }

    public void setStyleLow(String style) {
        this.styleLow = style.trim();
    }

    @Column(order = 2000, required = false, style = "(large)")
    public String getStyleLow() {
        return styleLow.isBlank() ? DEFAULT_1 : styleLow;
    }

    public void setStyleNormal(String style) {
        this.styleNormal = style.trim();
    }

    @Column(order = 2100, required = false, style = "(large)")
    public String getStyleNormal() {
        return styleNormal.isBlank() ? DEFAULT_0 : styleNormal;
    }

    public void setStyleHigh(String style) {
        this.styleHigh = style.trim();
    }

    @Column(order = 2200, required = false, style = "(large)")
    public String getStyleHigh() {
        return styleHigh.isBlank() ? DEFAULT_1 : styleHigh;
    }

    public void setStyleHigher(String style) {
        this.styleHigher = style.trim();
    }

    @Column(order = 2300, required = false, style = "(large)")
    public String getStyleHigher() {
        return styleHigher.isBlank() ? DEFAULT_2 : styleHigher;
    }

    public void setStyleHighest(String style) {
        this.styleHighest = style.trim();
    }

    @Column(order = 2400, required = false, style = "(large)")
    public String getStyleHighest() {
        return styleHighest.isBlank() ? DEFAULT_3 : styleHighest;
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        instance = null;
    }

    private static String styles(int level) {
        instance();
        if(level == -100) {
            return instance.getStyleLabel();
        }
        if(level == 100) {
            return instance.getStyle();
        }
        String s = "";
        if(level > 500) {
            s = instance.getStyle() + "\n";
            level = 1000 - level;
        }
        s += switch (level) {
            case 1 -> instance.getStyleHigh();
            case 2 -> instance.getStyleHigher();
            case 3 -> instance.getStyleHighest();
            case -1 -> instance.getStyleLow();
            case -2 -> instance.getStyleLower();
            case -3 -> instance.getStyleLowest();
            default -> instance.getStyleNormal();
        };
        return s;
    }

    /**
     * Get the styles for the given "data status".
     *
     * @param dataStatus Data status.
     * @param includeText Whether to include text-style or not.
     * @param additionalStyles Additional styles to be added.
     * @return Style array.
     */
    public static StyledBuilder.Style[] styles(DataSet.DataStatus<?> dataStatus, boolean includeText,
                                               String additionalStyles) {
        int level = dataStatus.alarm();
        if(includeText) {
            level += 1000;
        }
        String s = styles(level);
        if(additionalStyles != null && !additionalStyles.isBlank()) {
            s += "\n" + additionalStyles;
        }
        return StyledBuilder.Style.array(s);
    }

    /**
     * Get the text style.
     *
     * @return Text style array.
     */
    public static StyledBuilder.Style[] textStyle() {
        return StyledBuilder.Style.array(instance().style);
    }

    /**
     * Get the label style.
     *
     * @return Label style array.
     */
    public static StyledBuilder.Style[] labelStyle() {
        return StyledBuilder.Style.array(instance().style + "\n" + instance().styleLabel);
    }

    private static ValueStyle instance() {
        if(instance == null) {
            instance = get(ValueStyle.class);
            if(instance == null) {
                instance = new ValueStyle();
            }
        }
        return instance;
    }
}

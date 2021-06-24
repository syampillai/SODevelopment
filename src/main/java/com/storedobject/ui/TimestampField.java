package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.core.Utility;
import com.storedobject.vaadin.TranslatedField;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Field to accept {@link Timestamp} values.
 *
 * @author Syam
 */
public class TimestampField extends TranslatedField<Timestamp, LocalDateTime> {

    private final static Timestamp nullValue = new Timestamp(Utility.BLANK_TIME);
    private final TimeResolution resolution;

    /**
     * Constructor.
     */
    public TimestampField() {
        this((String) null);
    }

    /**
     * Constructor.
     *
     * @param label Field label.
     */
    public TimestampField(String label) {
        this(label, (TimeResolution) null);
    }

    /**
     * Constructor.
     *
     * @param initialValue Initial value.
     */
    public TimestampField(Timestamp initialValue) {
        this(null, initialValue, null);
    }

    /**
     * Constructor.
     *
     * @param label Field label.
     * @param initialValue Initial value.
     */
    public TimestampField(String label, Timestamp initialValue) {
        this(label, initialValue, null);
    }

    /**
     * Constructor.
     *
     * @param resolution Resolution.
     */
    public TimestampField(TimeResolution resolution) {
        super(new DateTimePicker(), (f, d) -> create(d), (f, d) -> create(d), null);
        this.resolution = resolution == null ? TimeResolution.MINUTES : resolution;
        getField().setStep(this.resolution == TimeResolution.SECONDS ? Duration.ofSeconds(30) : Duration.ofMinutes(30));
    }

    /**
     * Constructor.
     *
     * @param label Field label.
     * @param resolution Resolution.
     */
    public TimestampField(String label, TimeResolution resolution) {
        this(label, DateUtility.now(), resolution);
    }

    /**
     * Constructor.
     *
     * @param initialValue Initial value.
     * @param resolution Resolution.
     */
    public TimestampField(Timestamp initialValue, TimeResolution resolution) {
        this(null, initialValue, resolution);
    }

    /**
     * Constructor.
     *
     * @param label Field label.
     * @param initialValue Initial value.
     * @param resolution Resolution.
     */
    public TimestampField(String label, Timestamp initialValue, TimeResolution resolution) {
        this(resolution);
        setLabel(label);
        //setValue(initialValue);
    }

    private static Timestamp create(LocalDateTime d) {
        return d == null ? null : DateUtility.createTimestamp(d);
    }

    private static LocalDateTime create(Timestamp d) {
        return d == null ? null : DateUtility.localTime(d);
    }

    @Override
    public Timestamp getValue() {
        Timestamp ts = super.getValue();
        return ts == null ? nullValue : ts;
    }

    @Override
    public void setValue(Timestamp value) {
        if(time(value) == time(nullValue)) {
            value = null;
        }
        super.setValue(value);
    }

    private static long time(Timestamp ts) {
        return ts == null ? 0L : ts.getTime();
    }

    /**
     * Get the resolution.
     *
     * @return Resolution.
     */
    public final TimeResolution getResolution() {
        return this.resolution;
    }

    @Override
    public DateTimePicker getField() {
        return (DateTimePicker) super.getField();
    }
    /**
     * Set the minimum value allowed.
     *
     * @param value Minimum value allowed.
     */
    public void setMin(Timestamp value) {
        getField().setMin(value == null ? null : create(value));
    }

    /**
     * Set the maximum value allowed.
     *
     * @param value Maximum value allowed.
     */
    public void setMax(Timestamp value) {
        getField().setMax(value == null ? null : create(value));
    }

    /**
     * Get the allowed minimum value that is currently set. Null is returned of no minimum value was set.
     *
     * @return Minimum value.
     */
    public Timestamp getMin() {
        LocalDateTime d = getField().getMin();
        return d == null ? null : create(d);
    }

    /**
     * Get the allowed maximum value that is currently set. Null is returned of no maximum value was set.
     *
     * @return Maximum value.
     */
    public Timestamp getMax() {
        LocalDateTime d = getField().getMax();
        return d == null ? null : create(d);
    }

    /**
     * Set the visibility of "week numbers" while accepting the value.
     *
     * @param weekNumbersVisible True if visible.
     */
    public void setWeekNumbersVisible(boolean weekNumbersVisible) {
        getField().setWeekNumbersVisible(weekNumbersVisible);
    }

    /**
     * Check the visibility of "week numbers" while accepting the value.
     *
     * @return True if visible.
     */
    public boolean isWeekNumbersVisible() {
        return getField().isWeekNumbersVisible();
    }

    /**
     * Sets the step property of the time picker using duration.
     * It specifies the intervals for the displayed items in the time picker dropdown and also the displayed time format.
     * <p>The set step needs to evenly divide a day or an hour and has to be larger than 0 milliseconds.</p>
     * <p>If the step is less than 60 seconds, the format will be changed to hh:mm:ss and it can be in
     * hh:mm:ss.fff format, when the step is less than 1 second.</p>
     * <p>NOTE: If the step is less than 900 seconds, the dropdown is hidden.</p>
     * <p>NOTE: changing the step to a larger duration can cause a new HasValue.ValueChangeEvent to be fired
     * if some parts (eg. seconds) is discarded from the value.</p>
     *
     * @param step Step.
     */
    public void setStep(Duration step) {
        getField().setStep(step);
    }

    @Override
    protected boolean valueEquals(Timestamp value1, Timestamp value2) {
        long nullTime = time(nullValue);
        if(nullTime == time(value1)) {
            value1 = null;
        }
        if(nullTime == time(value2)) {
            value2 = null;
        }
        return super.valueEquals(value1, value2);
    }
}
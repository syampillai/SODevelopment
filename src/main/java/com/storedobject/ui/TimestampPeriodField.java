package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.core.TimestampPeriod;

import java.sql.Timestamp;
import java.time.Duration;

public class TimestampPeriodField extends RangeField<TimestampPeriod, Timestamp> {

    public TimestampPeriodField() {
        this(null, null);
    }

    public TimestampPeriodField(String label) {
        this(label, null);
    }

    public TimestampPeriodField(String label, TimestampPeriod initialValue) {
        super((x) -> new TimestampField(), (x) -> new TimestampField());
        if(initialValue == null) {
            initialValue = new TimestampPeriod(DateUtility.startOfToday(), DateUtility.now());
        }
        setValue(initialValue);
        setPresentationValue(getValue());
        setLabel(label);
        getFromField().addValueChangeListener(e -> updateValue());
        getToField().addValueChangeListener(e -> updateValue());
        setMinWidth("60ch");
    }

    @Override
    protected TimestampPeriod create(Timestamp from, Timestamp to) {
        return new TimestampPeriod(from, to);
    }

    /**
     * Get the resolution.
     *
     * @return Resolution.
     */
    public final TimeResolution getResolution() {
        return getFromField().getResolution();
    }

    @Override
    protected TimestampField getFromField() {
        return (TimestampField)super.getFromField();
    }

    @Override
    protected TimestampField getToField() {
        return (TimestampField) super.getToField();
    }

    /**
     * Set the minimum value allowed.
     *
     * @param value Minimum value allowed.
     */
    public void setMin(Timestamp value) {
        getFromField().setMin(value);
        getToField().setMin(value);
    }

    /**
     * Set the maximum value allowed.
     *
     * @param value Maximum value allowed.
     */
    public void setMax(Timestamp value) {
        getFromField().setMax(value);
        getToField().setMax(value);
    }

    /**
     * Get the allowed minimum value that is currently set. Null is returned of no minimum value was set.
     *
     * @return Minimum value.
     */
    public Timestamp getMin() {
        return getFromField().getMin();
    }

    /**
     * Get the allowed maximum value that is currently set. Null is returned of no maximum value was set.
     *
     * @return Maximum value.
     */
    public Timestamp getMax() {
        return getToField().getMax();
    }

    /**
     * Set the visibility of "week numbers" while accepting the value.
     *
     * @param weekNumbersVisible True if visible.
     */
    public void setWeekNumbersVisible(boolean weekNumbersVisible) {
        getFromField().setWeekNumbersVisible(weekNumbersVisible);
        getToField().setWeekNumbersVisible(weekNumbersVisible);
    }

    /**
     * Check the visibility of "week numbers" while accepting the value.
     *
     * @return True if visible.
     */
    public boolean isWeekNumbersVisible() {
        return getFromField().isWeekNumbersVisible();
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
        getFromField().setStep(step);
        getToField().setStep(step);
    }
}
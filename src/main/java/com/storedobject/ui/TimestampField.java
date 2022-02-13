package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.core.Utility;
import com.storedobject.vaadin.TranslatedField;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;

import static java.util.Calendar.YEAR;

/**
 * Field to accept {@link Timestamp} values.
 *
 * @author Syam
 */
public class TimestampField extends TranslatedField<Timestamp, LocalDateTime> {

    private final static Timestamp nullValue = new Timestamp(Utility.BLANK_TIME);
    private final TimeResolution resolution;
    private final Converter converter;

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
        this(new Converter(), resolution);
    }

    private TimestampField(Converter converter, TimeResolution resolution) {
        super(new DateTimePicker(), converter::create, (f, d) -> converter.create(d), null);
        this.converter = converter;
        this.resolution = resolution == null ? TimeResolution.MINUTES : resolution;
        getField().setStep(this.resolution == TimeResolution.SECONDS ? Duration.ofSeconds(30) : Duration.ofMinutes(30));
        getField().getChildren().forEach(c -> {
            if(c instanceof  HasSize hs) {
                if(c.getClass().getName().endsWith("TimePicker")) {
                    hs.setMaxWidth("13ch");
                } else {
                    hs.setMaxWidth("15ch");
                }
            }
        });
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
        if(initialValue != null) {
            setValue(initialValue);
        }
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
        getField().setMin(value == null ? null : converter.create(value));
    }

    /**
     * Set the maximum value allowed.
     *
     * @param value Maximum value allowed.
     */
    public void setMax(Timestamp value) {
        getField().setMax(value == null ? null : converter.create(value));
    }

    /**
     * Get the allowed minimum value that is currently set. Null is returned of no minimum value was set.
     *
     * @return Minimum value.
     */
    public Timestamp getMin() {
        LocalDateTime d = getField().getMin();
        return d == null ? null : converter.create(null, d);
    }

    /**
     * Get the allowed maximum value that is currently set. Null is returned of no maximum value was set.
     *
     * @return Maximum value.
     */
    public Timestamp getMax() {
        LocalDateTime d = getField().getMax();
        return d == null ? null : converter.create(null, d);
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

    /**
     * Epoch value to set. Epoch value determines how a 2 digit year value is interpreted. Epoch value is added to
     * the 2 digit year value. The default value of epoch is the first year of the century. For example, for the 21st
     * century, the default epoch value is 2000.
     *
     * @param epoch Epoch value to set.
     */
    public void setEpoch(int epoch) {
        converter.setEpoch(epoch);
    }

    /**
     * Get the current epoch value. (Please see {@link #setEpoch(int)}).
     *
     * @return Current the current epoch value.
     */
    public int getEpoch() {
        return converter.getEpoch();
    }

    /**
     * Class that handles the date conversion part.
     *
     * @author Syam
     */
    private static class Converter {

        private int epoch;

        /**
         * Constructor
         */
        Converter() {
            epoch = (getYear(DateUtility.today()) / 100) * 100;
        }

        /**
         * Create an instance of a {@link Date} from a {@link LocalDate} instance.
         *
         * @param date Instance to convert.
         * @return Converted value.
         */
        Timestamp create(HasValue<?, LocalDateTime> f, LocalDateTime date) {
            Timestamp d = create(date, epoch);
            if(f != null && getYear(d) != date.getYear()) {
                f.setValue(LocalDateTime.of(getYear(d), date.getMonth(), date.getDayOfMonth(), date.getHour(),
                        date.getMinute(), date.getSecond()));
            }
            return d;
        }

        /**
         * Create an instance of a {@link Date} from a {@link LocalDate} instance.
         *
         * @param date Instance to convert.
         * @param epoch Epoch value to adjust the year.
         * @return Converted value.
         */
        static Timestamp create(LocalDateTime date, int epoch) {
            if(date == null) {
                return null;
            }
            int year = date.getYear();
            if(year < 100) {
                year += epoch;
            }
            date = LocalDateTime.of(year, date.getMonth(), date.getDayOfMonth(), date.getHour(), date.getMinute(), date.getSecond());
            return DateUtility.createTimestamp(date);
        }

        /**
         * Create an instance of a {@link LocalDate} from a {@link Date} instance.
         *
         * @param date Instance to convert.
         * @param <D> Type of instance to convert.
         * @return Converted value.
         */
        <D extends java.util.Date> LocalDateTime create(D date) {
            return date == null ? null : DateUtility.localTime(date);
        }

        private static <D extends java.util.Date> int getYear(D date) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            return c.get(YEAR);
        }

        /**
         * Epoch value to set. Epoch value determines how a 2 digit year value is interpreted. Epoch value is added to
         * the 2 digit year value. The default value of epoch is the first year of the century. For example, for 21st
         * century, epoch value is 2000.
         *
         * @param epoch Epoch value to set.
         */
        void setEpoch(int epoch) {
            this.epoch = epoch;
        }

        /**
         * Get the current epoch value. (Please see {@link #setEpoch(int)}).
         *
         * @return Current the current epoch value.
         */
        int getEpoch() {
            return epoch;
        }
    }
}
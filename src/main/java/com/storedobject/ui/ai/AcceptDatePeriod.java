package com.storedobject.ui.ai;

import com.storedobject.core.DatePeriod;
import com.storedobject.ui.DatePeriodField;

/**
 * A class that extends {@link AcceptData} to handle the selection and processing of a date period.
 * The {@code AcceptDatePeriod} class enables users to select a range of dates by interacting with
 * a {@link DatePeriodField} field. The selected date period is then stored in an internal map and
 * can be retrieved for further processing.
 * <p>
 * This class is typically used in workflows where capturing a specific date range is required,
 * allowing integration with other components such as AI knowledge systems.
 * </p>
 */
public class AcceptDatePeriod extends AcceptData<DatePeriod> {

    private final DatePeriodField datePeriodField;

    /**
     * Constructs an {@code AcceptDatePeriod} instance to handle the selection and processing
     * of a date period, linking it to the provided knowledge system and assigning a specific purpose.
     *
     * @param knowledge the {@link Knowledge} instance used for processing and integrating
     *                  the date period with the associated AI knowledge system
     * @param purpose   a string describing the purpose of this {@code AcceptDatePeriod} instance,
     *                  typically used to provide context or identify the intent of the date period selection
     */
    public AcceptDatePeriod(Knowledge knowledge, String purpose) {
        super(DatePeriod.class, knowledge, "Select");
        datePeriodField = new DatePeriodField(purpose);
        addField(datePeriodField);
        setRequired(datePeriodField);
    }

    @Override
    protected boolean process() {
        data = datePeriodField.getValue();
        return true;
    }

    /**
     * Retrieves the {@link DatePeriodField} instance associated with this class.
     * The {@code DatePeriodField} allows for the selection and management of a date range.
     *
     * @return the {@code DatePeriodField} used for specifying and interacting with a date period.
     */
    public DatePeriodField getDatePeriodField() {
        return datePeriodField;
    }

    @Override
    public String getDataLabel() {
        return "datePeriod";
    }
}

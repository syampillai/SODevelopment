package com.storedobject.ui.ai;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.JSONMap;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.vaadin.View;

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
public class AcceptDatePeriod extends AcceptData {

    private DatePeriod date;
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
        this(knowledge, purpose, null);
    }

    /**
     * Constructs an instance of the {@code AcceptDatePeriod} class with the provided arguments.
     * This constructor initializes the {@link DatePeriodField} used for specifying the date period
     * and integrates it with the parent {@link AcceptData} workflow logic.
     *
     * @param knowledge the {@link Knowledge} instance to be used. Provides the contextual knowledge
     *                  necessary for executing the workflow.
     * @param purpose   a descriptive string indicating the purpose of the date period selection. It
     *                  is used for contextual labeling within the {@link DatePeriodField}.
     * @param map       a {@link JSONMap} instance that stores the input/output data of the date
     *                  period selection process. Can be null if not required.
     */
    public AcceptDatePeriod(Knowledge knowledge, String purpose, JSONMap map) {
        super(knowledge, "Select", map);
        datePeriodField = new DatePeriodField(purpose);
        addField(datePeriodField);
        setRequired(datePeriodField);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        date = null;
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        date = datePeriodField.getValue();
        map.put("datePeriod", date);
        return true;
    }

    /**
     * Retrieves the currently selected date period.
     * The date period is determined through user interaction with the associated
     * {@link DatePeriodField}, and its value is stored for retrieval and further processing.
     * This method also ensures the underlying data map is accessed and updated as needed
     * by invoking {@code getMap()}.
     *
     * @return the {@link DatePeriod} object representing the selected date range,
     *         or {@code null} if no date period has been selected or processed.
     */
    public DatePeriod getDatePeriod() {
        getMap();
        return date;
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
}

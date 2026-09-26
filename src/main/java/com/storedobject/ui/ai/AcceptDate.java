package com.storedobject.ui.ai;

import com.storedobject.vaadin.DateField;

import java.sql.Date;

/**
 * Handles the acceptance of a date input from the user by utilizing a {@link DateField}.
 * This class extends {@link AcceptData} and provides functionality to process the
 * selected date, store it in a JSON map, and retrieve it as needed.
 * <p>
 * The primary role of this class is to facilitate creating and managing user workflows
 * that involve date selection, ensuring the date is captured and made available
 * for subsequent operations. The date input is validated and marked as
 * a required field within the workflow.
 * </p>
 *
 * @author Syam
 */
public class AcceptDate extends AcceptData<Date> {

    private final DateField dateField;

    /**
     * Constructs an instance of AcceptDate, which facilitates the processing of a selected date
     * within a user workflow. The date is captured, validated, and made available for
     * further operations. This constructor initializes the AcceptDate instance with
     * required parameters for knowledge and purpose.
     *
     * @param knowledge the knowledge context used for managing workflows and operations.
     * @param purpose the descriptive purpose for the date input, used to set up the date field.
     */
    public AcceptDate(Knowledge knowledge, String purpose) {
        super(Date.class, knowledge, "Select");
        dateField = new DateField(purpose);
        addField(dateField);
        setRequired(dateField);
    }

    @Override
    protected boolean process() {
        data = dateField.getValue();
        return true;
    }

    /**
     * Retrieves the {@link DateField} instance associated with this class.
     * The returned {@link DateField} allows interaction with the date input field,
     * providing access to its configuration and the selected date value.
     *
     * @return the {@link DateField} object representing the date input field.
     */
    public DateField getDateField() {
        return dateField;
    }

    @Override
    public String getDataLabel() {
        return "date";
    }
}

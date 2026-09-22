package com.storedobject.ui.ai;

import com.storedobject.core.JSONMap;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.View;

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
public class AcceptDate extends AcceptData {

    private Date date;
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
        this(knowledge, purpose, null);
    }

    /**
     * Constructs an instance of the AcceptDate class used to handle date input through a {@link DateField}.
     * Initializes the date field with the specified purpose and associates it with the provided JSON map.
     *
     * @param knowledge An instance of {@link Knowledge} providing context and shared information for the workflow.
     * @param purpose A descriptive purpose string used to initialize and configure the {@link DateField}.
     * @param map A {@link JSONMap} to store the selected date and related data; can be null if not needed.
     */
    public AcceptDate(Knowledge knowledge, String purpose, JSONMap map) {
        super(knowledge, "Select", map);
        dateField = new DateField(purpose);
        addField(dateField);
        setRequired(dateField);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        date = null;
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        date = dateField.getValue();
        map.put("date", date);
        return true;
    }

    /**
     * Retrieves the date associated with this instance.
     *
     * @return the date object representing the date. If the date is not set, it may return null.
     */
    public Date getDate() {
        getMap();
        return date;
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
}

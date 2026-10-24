package com.storedobject.core;

import java.sql.Date;

/**
 * Functional interface representing a Reporter with the ability to manage and interact with
 * reporting-related data.
 * <p>
 * The Reporter interface provides default methods to retrieve date-related information such as the
 * current date and the date period representing the current year. It also defines a method for
 * obtaining the {@link TransactionManager}, which provides transaction management context.
 * </p>
 *
 * @author Syam
 */
@FunctionalInterface
public interface Reporter {

    /**
     * Retrieves the reporting date.
     *
     * @return the reporting date as a {@link Date} object
     */
    default Date getDate() {
        return DateUtility.today();
    }

    /**
     * Retrieves the reporting date period.
     *
     * @return the reporting period
     */
    default DatePeriod getDatePeriod() {
        return DatePeriod.thisYear();
    }

    /**
     * Retrieves the {@link TransactionManager} instance.
     *
     * @return an instance of {@link TransactionManager}
     */
    TransactionManager getTransactionManager();
}

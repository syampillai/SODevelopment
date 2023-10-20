package com.storedobject.core;

import com.storedobject.common.JSON;

/**
 * Interface to denote that a {@link ContentProducer} instance such as a report supports
 * {@link com.storedobject.common.JSON} parameter.
 *
 * @author Syam
 */
public interface JSONParameter {

    /**
     * If implemented, this method is typically invoked by the tools before a
     * {@link ContentProducer} is asked to produce its output. It can extract its required parameter values from
     * the {@link JSON} structure.
     *
     * @param json {@link JSON} containing parameter values.
     */
    void setParameters(JSON json);
}
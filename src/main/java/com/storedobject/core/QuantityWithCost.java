package com.storedobject.core;

/**
 * A simple class that represents a "quantity" {@link Quantity} with its "cost" {@link Money}.
 *
 * @author Syam
 */
public record QuantityWithCost(Quantity quantity, Money cost) {

    /**
     * Add another instance to this instance and return the resulted instance.
     *
     * @param another Another instance.
     * @return New instance.
     */
    public QuantityWithCost add(QuantityWithCost another) {
        return new QuantityWithCost(quantity.add(another.quantity), cost.add(another.cost));
    }

    /**
     * Subtract another instance to this instance and return the resulted instance.
     *
     * @param another Another instance.
     * @return New instance.
     */
    public QuantityWithCost subtract(QuantityWithCost another) {
        return new QuantityWithCost(quantity.subtract(another.quantity), cost.subtract(another.cost));
    }
}

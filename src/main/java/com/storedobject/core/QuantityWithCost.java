package com.storedobject.core;

/**
 * A simple class that represents a "quantity" {@link Quantity} with its "cost" {@link Money}.
 *
 * @author Syam
 */
public class QuantityWithCost {

    private final Quantity quantity;
    private final Money cost;

    public QuantityWithCost(Quantity quantity, Money cost) {
        this.quantity = quantity;
        this.cost = cost;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Money getCost() {
        return cost;
    }

    public QuantityWithCost add(QuantityWithCost another) {
        return new QuantityWithCost(quantity.add(another.quantity), cost.add(another.cost));
    }

    public QuantityWithCost subtract(QuantityWithCost another) {
        return new QuantityWithCost(quantity.subtract(another.quantity), cost.subtract(another.cost));
    }
}
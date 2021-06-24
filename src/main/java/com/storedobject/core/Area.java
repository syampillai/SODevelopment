package com.storedobject.core;

import java.math.BigDecimal;

public class Area extends Quantity {

    public static MeasurementUnit defaultUnit = MeasurementUnit.get("m2");

    public Area() {
        this(BigDecimal.ZERO, defaultUnit);
    }

    public Area(double value, String unit) {
        this(BigDecimal.valueOf(value), unit);
    }

    public Area(double value, MeasurementUnit unit) {
        this(BigDecimal.valueOf(value), unit);
    }

    public Area(BigDecimal value, String unit) {
        this(value, MeasurementUnit.get(unit, Area.class));
    }

    public Area(BigDecimal value, MeasurementUnit unit) {
        super(value, unit);
    }

    public static Area circle(Distance radius) {
        return radius.circle();
    }

    public static Area rectangle(Distance length, Distance width) {
        return length.rectangle(width);
    }

    public static Area square(Distance side) {
        return side.square();
    }

    /**
     * Create a quantity of this type with zero value.
     * @return Result
     */
    @Override
    public Area zero() {
        return (Area)super.zero();
    }

    /**
     * Add quantity value
     *
     * @param quantity The quantity value to add
     * @return Result
     */
    @Override
    public Area add(String quantity) {
        return (Area)super.add(quantity);
    }

    /**
     * Add quantity
     *
     * @param quantity The quantity value to add
     * @return Result
     */
    @Override
    public Area add(BigDecimal quantity) {
        return (Area)super.add(quantity);
    }

    /**
     * Add quantity
     *
     * @param quantity The quantity value to add
     * @return Result
     */
    @Override
    public Area add(Quantity quantity) {
        return (Area)super.add(quantity);
    }

    /**
     * Subtract quantity
     *
     * @param quantity The quantity value to subtract
     * @return Result
     */
    @Override
    public Area subtract(String quantity) {
        return (Area)super.subtract(quantity);
    }

    /**
     * Subtract quantity
     *
     * @param quantity The quantity value to subtract
     * @return Result
     */
    @Override
    public Area subtract(BigDecimal quantity) {
        return (Area)super.subtract(quantity);
    }

    /**
     * Subtract quantity
     *
     * @param quantity The quantity value to subtract
     * @return Result
     */
    @Override
    public Area subtract(Quantity quantity) {
        return (Area)super.subtract(quantity);
    }

    /**
     * Multiply
     * @param multiplicand Multiplicand
     * @return Result
     */
    @Override
    public Area multiply(BigDecimal multiplicand) {
        return (Area)super.multiply(multiplicand);
    }

    /**
     * Multiply
     * @param multiplicand Multiplicand
     * @return Result
     */
    @Override
    public Area multiply(double multiplicand) {
        return (Area)super.multiply(multiplicand);
    }

    /**
     * Divide the quantity with a value
     * @param divisor Divisor
     * @return Result
     */
    @Override
    public Area divide(double divisor) {
        return (Area)super.divide(divisor);
    }

    /**
     * Divide the quantity with a value
     * @param divisor Divisor
     * @return Result
     */
    @Override
    public Area divide(BigDecimal divisor) {
        return (Area)super.divide(divisor);
    }

    /**
     * Reverses the sign of this quantity
     * @return Negated value
     */
    @Override
    public Area negate() {
        return (Area)super.negate();
    }

    /**
     * Absolute value of this quantity.
     *
     * @return Absolute value.
     */
    @Override
    public Area absolute() {
        return (Area)super.absolute();
    }

    /**
     * Create volume of a prism with this as the base and for the given height.
     *
     * @param height Height of the prism.
     * @return Volume of the prism.
     */
    public Volume prism(Distance height) {
        return new Volume(convert(defaultUnit).getValue().multiply(height.convert(Distance.defaultUnit).getValue(),
                PRECISION), Volume.defaultUnit);
    }

    /**
     * Create volume of a pyramid with this as the base and for the given height.
     *
     * @param height Height of the pyramid.
     * @return Volume of the pyramid.
     */
    public Volume pyramid(Distance height) {
        return prism(height).divide(new BigDecimal(3));
    }
}

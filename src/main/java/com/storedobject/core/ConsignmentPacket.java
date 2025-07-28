package com.storedobject.core;

import com.storedobject.core.annotation.Column;

public class ConsignmentPacket extends StoredObject implements Detail {

    private String label = "";
    private Distance length = Quantity.create(Distance.class, "cm");
    private Distance width = Quantity.create(Distance.class, "cm");
    private Distance height = Quantity.create(Distance.class, "cm");
    private Weight netWeight = Quantity.create(Weight.class, "kg");
    private Weight grossWeight = Quantity.create(Weight.class, "kg");
    private int number;

    public ConsignmentPacket() {
    }

    public static void columns(Columns columns) {
        columns.add("Label", "text");
        columns.add("Number", "int");
        columns.add("Length", "distance");
        columns.add("Width", "distance");
        columns.add("Height", "distance");
        columns.add("NetWeight", "weight");
        columns.add("GrossWeight", "weight");
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(required = false, order = 100)
    public String getLabel() {
        return label;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Column(caption = "Box Number", order = 200)
    public int getNumber() {
        return number;
    }

    public void setLength(Distance length) {
        this.length = length;
    }

    public void setLength(Object value) {
        setLength(Distance.create(value, Distance.class));
    }

    @Column(order = 300)
    public Distance getLength() {
        return length;
    }

    public void setWidth(Distance width) {
        this.width = width;
    }

    public void setWidth(Object value) {
        setWidth(Distance.create(value, Distance.class));
    }

    @Column(order = 400)
    public Distance getWidth() {
        return width;
    }

    public void setHeight(Distance height) {
        this.height = height;
    }

    public void setHeight(Object value) {
        setHeight(Distance.create(value, Distance.class));
    }

    @Column(order = 500)
    public Distance getHeight() {
        return height;
    }

    public void setNetWeight(Weight netWeight) {
        this.netWeight = netWeight;
    }

    public void setNetWeight(Object value) {
        setNetWeight(Weight.create(value, Weight.class));
    }

    @Column(order = 600, required = false)
    public Weight getNetWeight() {
        return netWeight;
    }

    public void setGrossWeight(Weight grossWeight) {
        this.grossWeight = grossWeight;
    }

    public void setGrossWeight(Object value) {
        setGrossWeight(Weight.create(value, Weight.class));
    }

    @Column(order = 700, required = false)
    public Weight getGrossWeight() {
        return grossWeight;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(number <= 0) {
            throw new Invalid_Value("Number");
        }
        if(!(length.isPositive() && width.isPositive() && height.isPositive() && !netWeight.isNegative()
                && !grossWeight.isNegative())) {
            throw new Invalid_Value("Dimensions");
        }
        if(grossWeight.isPositive() && netWeight.isPositive() && grossWeight.isLessThan(netWeight)) {
            throw new Invalid_Value("Weight");
        }
        super.validateData(tm);
    }

    public String getDimensions() {
        return length + " x " + width + " x " + height;
    }

    @Override
    public String toDisplay() {
        StringBuilder s = new StringBuilder(label);
        if(!s.isEmpty()) {
            s.append(' ');
        }
        s.append(getDimensions());
        if(netWeight.isPositive() || grossWeight.isPositive()) {
            s.append(" (");
            if(netWeight.isPositive()) {
                s.append("Net: ").append(netWeight);
            }
            if(grossWeight.isPositive()) {
                if(netWeight.isPositive()) {
                    s.append(", ");
                }
                s.append("Gross: ").append(grossWeight);
            }
            s.append(")");
        }
        return s.toString();
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return Consignment.class == masterClass;
    }

    @Override
    public Object getUniqueValue() {
        return number;
    }
}

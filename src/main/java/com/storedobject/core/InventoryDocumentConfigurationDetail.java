package com.storedobject.core;

import java.math.BigDecimal;

public class InventoryDocumentConfigurationDetail extends StoredObject implements Detail {

    public InventoryDocumentConfigurationDetail() {
    }

    public static void columns(Columns columns) {
    }

    public void setDisplayOrder(int displayOrder) {
    }

    public int getDisplayOrder() {
        return 0;
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setApplicabilityLevel(int applicabilityLevel) {
    }

    public int getApplicabilityLevel() {
        return 0;
    }

    public static String[] getApplicabilityLevelValues() {
        return null;
    }

    public static String getApplicabilityLevelValue(int value) {
        return null;
    }

    public String getApplicabilityLevelValue() {
        return null;
    }

    public void setValueType(int valueType) {
    }

    public int getValueType() {
        return 0;
    }

    public static String[] getValueTypeValues() {
        return null;
    }

    public static String getValueTypeValue(int value) {
        return null;
    }

    public String getValueTypeValue() {
        return null;
    }

    public void setComputation(int computation) {
    }

    public int getComputation() {
        return 0;
    }

    public static String[] getComputationValues() {
        return null;
    }

    public static String getComputationValue(int value) {
        return null;
    }

    public String getComputationValue() {
        return null;
    }

    public void setPercentage(DecimalNumber percentage) {
    }

    public void setPercentage(Object value) {
    }

    public DecimalNumber getPercentage() {
        return null;
    }

    public void setDefaultAmount(Money defaultAmount) {
    }

    public void setDefaultAmount(Object moneyValue) {
    }

    public Money getDefaultAmount() {
        return null;
    }

    public void setMinimumAmount(Money minimumAmount) {
    }

    public void setMinimumAmount(Object moneyValue) {
    }

    public Money getMinimumAmount() {
        return null;
    }

    public void setMaximumAmount(Money maximumAmount) {
    }

    public void setMaximumAmount(Object moneyValue) {
    }

    public Money getMaximumAmount() {
        return null;
    }

    public void setEditable(boolean editable) {
    }
    
    public boolean getEditable() {
    	return false;
    }

    public void setBookingAccount(Id bookingAccountId) {
    }

    public void setBookingAccount(BigDecimal idValue) {
    }

    public void setBookingAccount(Account bookingAccount) {
    }

    public Id getBookingAccountId() {
        return null;
    }

    public Account getBookingAccount() {
        return null;
    }

    public Id getUniqueId() {
        return null;
    }

    public void copyValuesFrom(Detail detail) {
    }

    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }
}
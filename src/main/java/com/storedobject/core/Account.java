package com.storedobject.core;

public class Account extends com.storedobject.core.StoredObject {

    public Account(com.storedobject.core.Id p1) {
        this();
    }

    public Account(com.storedobject.core.Id p1, java.lang.String p2) {
        this();
    }

    public Account(com.storedobject.core.Id p1, com.storedobject.core.Id p2) {
        this();
    }

    public Account(com.storedobject.core.Id p1, java.lang.String p2, com.storedobject.core.Id p3) {
        this();
    }

    public Account() {
    }

    public java.lang.String toString() {
        return null;
    }

    public java.lang.String getName() {
        return null;
    }

    public void setName(java.lang.String p1) {
    }

    public void close() throws java.lang.Exception {
    }

    public final java.lang.String getNumber() {
        return null;
    }

    public final void refresh() {
    }

    public void setCurrency(java.util.Currency p1) {
    }

    public void setCurrency(java.lang.String p1) {
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void readOnlyColumns(com.storedobject.core.ColumnNames p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public static java.lang.String[] displayColumns() {
        return null;
    }

    public static java.lang.String[] browseColumns() {
        return null;
    }

    public static java.lang.String[] protectedColumns() {
        return null;
    }

    public static java.lang.String filter(java.lang.Class <?> p1, java.lang.String p2) {
        return null;
    }

    public java.util.Currency getLocalCurrency() {
        return null;
    }

    public java.util.Currency getCurrency() {
        return null;
    }

    public com.storedobject.core.Id getSystemEntityId() {
        return null;
    }

    public void setSystemEntity(java.math.BigDecimal p1) {
    }

    public com.storedobject.core.SystemEntity getSystemEntity() {
        return null;
    }

    protected void addBalance(com.storedobject.core.Money p1) {
    }

    public void setBalance(java.lang.Object p1) {
    }

    public com.storedobject.core.Money getBalance() {
        return null;
    }

    public void addLocalCurrencyBalance(com.storedobject.core.Money p1) {
    }

    public void setLocalCurrencyBalance(java.lang.Object p1) {
    }

    public com.storedobject.core.Money getLocalCurrencyBalance() {
        return null;
    }

    public void setOpeningBalance(java.lang.Object p1) {
    }

    public com.storedobject.core.Money getOpeningBalance() {
        return null;
    }

    public void setLocalCurrencyOpeningBalance(java.lang.Object p1) {
    }

    public com.storedobject.core.Money getLocalCurrencyOpeningBalance() {
        return null;
    }

    public boolean isLocalCurrency() {
        return false;
    }

    public boolean isForeignCurrency() {
        return false;
    }

    public int getAccountStatus() {
        return 0;
    }

    public void setAccountStatus(int p1) {
    }

    public void reopen() throws java.lang.Exception {
    }

    public void validateData() throws java.lang.Exception {
    }

    public java.lang.String getTitle() {
        return null;
    }

    public com.storedobject.core.AccountChart getChart() {
        return null;
    }

    public final void setNumber(java.lang.String p1) {
    }

    public void setChart(java.math.BigDecimal p1) {
    }

    public void setChart(com.storedobject.core.Id p1) {
    }

    public com.storedobject.core.Id getChartId() {
        return null;
    }

    public com.storedobject.core.Money createAmount(java.math.BigDecimal p1) {
        return null;
    }

    public com.storedobject.core.Money createLocalCurrencyAmount(java.math.BigDecimal p1) {
        return null;
    }

    public final void debit(com.storedobject.core.Money p1, int p2) throws java.lang.Exception {
    }

    public final void debit(java.math.BigDecimal p1, int p2) throws java.lang.Exception {
    }

    public final void debit(com.storedobject.core.Money p1, com.storedobject.core.Money p2, int p3) throws java.lang.Exception {
    }

    public final void debit(java.math.BigDecimal p1, java.math.BigDecimal p2, int p3) throws java.lang.Exception {
    }

    public final void credit(com.storedobject.core.Money p1, int p2) throws java.lang.Exception {
    }

    public final void credit(java.math.BigDecimal p1, int p2) throws java.lang.Exception {
    }

    public final void credit(com.storedobject.core.Money p1, com.storedobject.core.Money p2, int p3) throws java.lang.Exception {
    }

    public final void credit(java.math.BigDecimal p1, java.math.BigDecimal p2, int p3) throws java.lang.Exception {
    }
}

package com.storedobject.core;

import java.math.BigDecimal;

public final class AccountChartMap extends StoredObject {

    private String accountClassName;
    private Id chartId;

    public AccountChartMap() {
    }

    public static void columns(Columns columns) {
        columns.add("AccountClassName", "text");
        columns.add("Chart", "id");
    }

    public static void indices(Indices indices) {
        indices.add("lower(AccountClassName)", false);
    }

    public static String[] displayColumns() {
        return new String[] { "AccountClassName" };
    }

    public static String[] browseColumns() {
        return new String[] { "AccountClassName", "Chart.Name as Chart Name" };
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setAccountClassName(String className) {
        this.accountClassName = className;
    }

    public String getAccountClassName() {
        return accountClassName;
    }

    public void setChart(BigDecimal chartId) {
        setChart(new Id(chartId));
    }

    public void setChart(Id chartId) {
        this.chartId = chartId;
    }

    public void setChart(AccountChart chart) {
        setChart(chart.getId());
    }

    public Id getChartId() {
        return chartId;
    }

    public AccountChart getChart() {
        return get(AccountChart.class, chartId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(getAccountClass() == null) {
            throw new Invalid_Value("Account Class Name");
        }
        chartId = tm.checkType(this, chartId, AccountChart.class);
        super.validateData(tm);
    }

    public <A extends Account> Class<A> getAccountClass() {
        try {
            //noinspection unchecked
            return (Class<A>) JavaClassLoader.getLogic(accountClassName);
        } catch(Throwable ignored) {
        }
        return null;
    }
}

package com.storedobject.ui;

import com.storedobject.chart.CategoryData;
import com.storedobject.chart.Data;
import com.storedobject.chart.FunnelChart;
import com.storedobject.chart.SOChart;
import com.storedobject.common.JSON;
import com.storedobject.core.SingletonLogic;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Test extends View implements SingletonLogic, CloseableView {

    private final SOChart soChart;

    public Test() {
        super("Dashboard");
        soChart = new SOChart() {
            @Override
            protected String customizeJSON(String json) throws Exception {
                System.err.println(JSON.create(json).toPrettyString());
                return json;
            }
        };
        setComponent(new VerticalLayout(soChart));
        createChart();
    }

    private void createChart() {
        FunnelChart fc = new FunnelChart(new CategoryData("Twenty", "Forty", "Fifty", "EightY"),
                new Data(20, 40, 50, 80));
        soChart.add(fc);
    }
}

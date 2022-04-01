package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.common.JSON;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Test extends View implements CloseableView {

    public Test() {
        super("Gantt Chart Example");

        // Define a chart component
        SOChart soChart = new SOChart() {
            @Override
            protected String customizeDataJSON(String json, AbstractDataProvider<?> data) throws Exception {
                System.err.println(data.getSerial() + ": " + JSON.create(json).toPrettyString());
                return super.customizeDataJSON(json, data);
            }

            @Override
            protected String customizeJSON(String json) throws Exception {
                System.err.println(JSON.create(json).toPrettyString());
                return super.customizeJSON(json);
            }
        };
        soChart.setSize("100%", "100%");

        // Sample project with few entries
        ActivityList project = new ActivityList();
        project.setStart(LocalDateTime.now().minusDays(10));
        ActivityList.ActivityGroup tg1 = project.createActivityGroup("Group 1");
        ActivityList.Activity tg1T1 = tg1.createActivity("Activity 1/1", LocalDateTime.now(), 6);
        ActivityList.Activity tg1T2 = tg1T1.createNext("Activity 1/2",5);
        tg1T2.setCompleted(100); // This task is 100% complete
        ActivityList.Activity tg1T3 = tg1T2.createNext("Activity 1/3", 11);
        tg1T3.createNext("Example Milestone", 10); // Milestone
        ActivityList.ActivityGroup tg2 = project.createActivityGroup("Group 2");
        ActivityList.Activity tg2T1 = tg2.createActivity("Activity 2/1", LocalDateTime.now(), 3);
        ActivityList.Activity tg2T2 = tg2T1.createNext("Activity 2/2",7);
        ActivityList.Activity tg2T3 = tg2T2.createNext("Activity 2/3",13);
        tg2T3.createNext("Activity 2/4",9);
        tg2T3.setColor(new Color("green")); // Specific color for this task
        tg2T1.setCompleted(35); // This task is 35% complete

        // Plot the project on a Gantt Chart
        ActivityChart ac = new ActivityChart(project);
        ac.getTimeAxisZoom().hide();

        // Add the Gantt Chart to our chart component
        //soChart.add(ac);

        XRangeChart<Number, String> xrc1 = new XRangeChart<>();
        xrc1.addData(4, 6, "Y5", new Color("red"));
        xrc1.addData(5, 8, "Y4", new Color("yellow"));
        xrc1.addData(1, 4, "Y4", "Delayed", new Color("pink"));
        xrc1.addData(8, 12, "Y2", new Color("blue"));
        xrc1.setYData(new CategoryData("Y1", "Y2", "Y3", "Y4", "Y5"));
        xrc1.getCoordinateSystem().getPosition(true).setBottom(Size.percentage(55));
        soChart.add(xrc1);
        XRangeChart<Number, Number> xrc2 = new XRangeChart<>();
        xrc2.addData(4, 6, 0, "Hello @ Zero", new Color("red"));
        xrc2.addData(4, 6, 5, "Hello", new Color("red"));
        xrc2.addData(5, 8, 4, new Color("yellow"), 20);
        xrc2.addData(1, 4, 4, new Color("pink"), 50);
        xrc2.addData(8, 12, 2, "How", new Color("blue"), 100);
        xrc2.getCoordinateSystem().getPosition(true).setTop(Size.percentage(55));
        xrc2.getYAxis().setMax(6);
        xrc2.showProgressLabel(true);
        xrc2.getYAxis().setMin(-1);
        xrc2.getXZoom(true);
        soChart.add(xrc2);

        setComponent(new VerticalLayout(soChart));
    }
}
